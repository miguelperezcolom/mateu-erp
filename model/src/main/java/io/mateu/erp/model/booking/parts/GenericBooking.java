package io.mateu.erp.model.booking.parts;

import com.google.common.base.Strings;
import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.PriceBreakdownItem;
import io.mateu.erp.model.booking.ValidationStatus;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.generic.GenericServiceExtra;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.generic.Contract;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Getter@Setter
public class GenericBooking extends Booking {

    @NotNull
    @ManyToOne
    @Position(14)
    private Office office;

    @ManyToOne@NotNull
    @Position(15)
    private GenericProduct product;

    @ManyToOne@NotNull
    @Position(16)
    private Variant variant;

    public DataProvider getVariantDataProvider() {
        return new ListDataProvider(product != null?product.getVariants():new ArrayList());
    }

    private int units;



    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @Position(17)
    private List<GenericBookingExtra> extras = new ArrayList<>();


    @ManyToOne@Output
    private Contract contract;


    public GenericBooking() {
        setIcons(FontAwesome.GIFT.getHtml());
    }


    @Override
    protected void completeChangeSignatureData(Map<String, String> data) {
        if (office != null) data.put("Office", office.getName());
        if (product != null) data.put("Product", product.getName());
        if (variant != null) data.put("Variant", variant.getName().toString());
        extras.forEach(p -> data.put("Extra " + extras.indexOf(p), p.toString()));
        if (contract != null) data.put("Contract", contract.getTitle());
    }

    @Override
    public void validate() throws Exception {
        setValidationStatus(ValidationStatus.VALID);
        setAvailable(true);
    }

    @Override
    public void generateServices(EntityManager em) {
        GenericService s = null;
        if (getServices().size() > 0) {
            s = (GenericService) getServices().get(0);
        }
        if (s == null) {
            getServices().add(s = new GenericService());
            s.setBooking(this);
            s.setAudit(new Audit(MDD.getCurrentUser()));
        }
        s.setOffice(office);
        s.setFinish(getEnd());
        s.setStart(getStart());
        s.setProduct(getProduct());
        s.setVariant(getVariant());
        for (GenericBookingExtra e : getExtras()) s.getExtras().add(new GenericServiceExtra(s, e));
        s.setDeliveryDate(getStart());
        s.setReturnDate(getEnd());

        s.setInfants(getInfants());
        s.setChildren(getChildren());
        s.setJuniors(getJuniors());
        s.setAdults(getAdults());
        s.setSeniors(getSeniors());

        s.setActive(isActive());
    }

    @Override
    protected ProductLine getEffectiveProductLine() {
        return getProduct().getProductLine();
    }

    @Override
    public void priceServices(EntityManager em, List<PriceBreakdownItem> breakdown) {
        Map<Contract, Double> prices = new HashMap<>();
        Accessor.get(em).getGenericContracts().stream().filter(c ->
                ContractType.SALE.equals(c.getType())
                    && c.isActive()
                    && (c.getValidFrom() == null || getEnd() == null || !c.getValidFrom().isAfter(getEnd()))
                    && (c.getValidTo() == null || getStart() == null || !c.getValidTo().isBefore(getStart()))
        ).forEach(c -> {
            final double[] v = {0};
            int dias = 1;
            if (getStart() != null && getEnd() != null) {
                dias = (int) DAYS.between(getStart(), getEnd());
            }
            int finalDias = dias;
            c.getPrices().stream()
                    .sorted((p0, p1) -> p0.getOrder() - p1.getOrder())
                    .filter(p -> p.getProduct() == null || p.getProduct().equals(product))
                    .filter(p -> p.getVariant() == null || p.getVariant().equals(variant))
                    .forEach(p -> {

                        v[0] += getInfants() * p.getInfantPrice();
                        v[0] += getChildren() * p.getChildPrice();
                        v[0] += getJuniors() * p.getJuniorPrice();
                        v[0] += getAdults() * p.getAdultPrice();
                        v[0] += getSeniors() * p.getSeniorPrice();
                        v[0] += finalDias * getInfants() * p.getInfantPricePerDay();
                        v[0] += finalDias * getChildren() * p.getChildPricePerDay();
                        v[0] += finalDias * getJuniors() * p.getJuniorPricePerDay();
                        v[0] += finalDias * getAdults() * p.getAdultPricePerDay();
                        v[0] += finalDias * getSeniors() * p.getSeniorPricePerDay();

            });
            prices. put(c, v[0]);
        });

        setTotalValue(0);
        prices.keySet().stream().min((v0, v1) -> prices.get(v0).compareTo(prices.get(v1))).ifPresent(v -> {
            setTotalValue(Helper.roundEuros(prices.get(v)));
            setContract(v);

            breakdown.add(new PriceBreakdownItem(contract.getBillingConcept(), getChargeSubject(), getTotalValue()));
        });
    }

    @Override
    protected BillingConcept getDefaultBillingConcept(EntityManager em) {
        return AppConfig.get(em).getBillingConceptForOthers();
    }

    public String getChargeSubject() {
        return "" + (product != null?product.getName():"--") + " from " + (getStart() != null?getStart().toString():"--") + " to " + (getEnd() != null?getEnd().toString():"--");
    }

    @Override
    protected void completeSignature(Map<String, Object> m) {
        if (getProduct() != null) m.put("product", getProduct().getName());
        if (getVariant() != null) m.put("variant", getVariant().getName());
        List<Map<String, Object>> extras = new ArrayList<>();
        getExtras().forEach(e -> {
            HashMap<String, Object> x;
            extras.add(x = new HashMap<>());
            if (e.getExtra() != null) x.put("extra", e.getExtra().getName());
            x.put("units", e.getUnits());
        });
        if (extras.size() > 0) m.put("extras", extras);

        m.put("infants", getInfants());
        m.put("children", getChildren());
        m.put("juniors", getJuniors());
        m.put("adults", getAdults());
        m.put("seniors", getSeniors());

        if (getProvider() != null) m.put("provider", "" + getProvider().getId() + " - " + getProvider().getName());
        m.put("serviceCost", "" + getOverridedCost());
    }

    @Override
    public String getServiceDataHtml() {
        final String[] h = {"<pre>"};

        h[0] += "GENERIC BOOKING \n";

        h[0] += "Start: " + getStart().format(DateTimeFormatter.ISO_DATE) + "\n";
        h[0] += "End: " + getEnd().format(DateTimeFormatter.ISO_DATE) + "\n";
        h[0] += "Product: " + getProduct().getName() + " \n";
        if (getVariant() != null) h[0] += "Variant: " + getVariant().getName() + " \n";
        if (getInfants() != 0) h[0] += getInfants() + " infants \n";
        if (getChildren() != 0) h[0] += getChildren() + " children \n";
        if (getJuniors() != 0) h[0] += getJuniors() + " juniors \n";
        if (getAdults() != 0) h[0] += getAdults() + " adults \n";
        if (getSeniors() != 0) h[0] += getSeniors() + " seniors \n";

        for (GenericBookingExtra e : getExtras()) {
            h[0] += "Extra: " + e.getExtra().getName() + " X " + e.getUnits() + " \n";
        }

        if (!Strings.isNullOrEmpty(getSpecialRequests())) h[0] += "Special requests: " + getSpecialRequests() + "\n";

        h[0] += "</pre>";
        return h[0];
    }
}
