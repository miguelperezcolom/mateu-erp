package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.ValidationStatus;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.generic.GenericServiceExtra;
import io.mateu.erp.model.financials.Amount;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.generic.Contract;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Getter@Setter
public class GenericBooking extends Booking {

    @Position(13)
    private int units;

    @NotNull
    @ManyToOne
    @Position(14)
    private Office office;

    @ManyToOne@NotNull
    @Position(15)
    private GenericProduct product;

    @ManyToOne
    @Position(16)
    private Variant variant;


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
        data.put("Units", "" + units);
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
        s.setUnits(getUnits());
        s.setAdults(getAdults());
        s.setChildren(getChildren());
        s.setActive(isActive());
    }

    @Override
    public void priceServices(EntityManager em) {
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
                v[0] += getUnits() * p.getPricePerUnit();
                v[0] += getAdults() * p.getPricePerAdult();
                v[0] += getChildren() * p.getPricePerChild();

                v[0] += finalDias * getUnits() * p.getPricePerUnitAndDay();
                v[0] += finalDias * getAdults() * p.getPricePerAdultAndDay();
                v[0] += finalDias * getChildren() * p.getPricePerChildAndDay();

            });
            prices. put(c, v[0]);
        });

        setTotalValue(0);
        prices.keySet().stream().min((v0, v1) -> prices.get(v0).compareTo(prices.get(v1))).ifPresent(v -> {
            setTotalValue(Helper.roundEuros(prices.get(v)));
            setContract(v);
        });
    }

    @Override
    public void createCharges(EntityManager em) throws Throwable {
        BookingCharge c;
        getServiceCharges().add(c = new BookingCharge(this));
        c.setTotal(getTotalValue());
        c.setCurrency(getCurrency());
        c.setText("" + product.getName() + " from " + getStart().toString() + " to " + getEnd().toString() + " for " + getUnits() + "u/" + getAdults() + " ad/" + getChildren() + "ch");
        c.setBillingConcept(getContract().getBillingConcept());
        c.setAgency(getAgency());
    }

    @Override
    protected void completeSignature(Map<String, Object> m) {

    }
}
