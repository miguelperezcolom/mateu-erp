package io.mateu.erp.model.booking.parts;

import com.google.common.base.Strings;
import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.model.booking.PriceBreakdownItem;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.tour.Circuit;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Getter@Setter
public class CircuitBooking extends TourBooking {

    @ManyToOne
    @NotNull
    @Position(18)
    private Circuit circuit;


    @ManyToOne
    @NotNull
    @Position(19)
    private Variant variant;





    public DataProvider getVariantDataProvider() {
        return new ListDataProvider(circuit != null?circuit.getVariants():new ArrayList());
    }


    public boolean isEndOutput() { return true; }


    public CircuitBooking() {
        setIcons(FontAwesome.GLOBE.getHtml());
    }


    @Override
    protected void completeChangeSignatureData(Map<String, String> data) {

    }

    @Override
    public void validate() throws Exception {

    }

    @Override
    public void generateServices(EntityManager em) {

    }

    @Override
    protected ProductLine getEffectiveProductLine() {
        return getCircuit().getProductLine();
    }

    @Override
    public void priceServices(EntityManager em, List<PriceBreakdownItem> breakdown) {
        Map<io.mateu.erp.model.product.tour.Contract, Double> prices = new HashMap<>();
        Accessor.get(em).getTourContracts().stream().filter(c ->
                ContractType.SALE.equals(c.getType())
                        && c.isActive()
                        && (c.getValidFrom() == null || getEnd() == null || !c.getValidFrom().isAfter(getEnd()))
                        && (c.getValidTo() == null || getStart() == null || !c.getValidTo().isBefore(getStart()))
        ).forEach(c -> {
            final double[] v = {0};
            int dias = 1;
            if (getStart() != null && getEnd() != null) {
                dias = (int) DAYS.between(getStart(), getEnd()) + 1;
            }
            int finalDias = dias;
            c.getPrices().stream()
                    .sorted((p0, p1) -> p0.getOrder() - p1.getOrder())
                    .filter(p -> p.getTour() == null || p.getTour().equals(circuit))
                    .filter(p -> p.getVariant() == null || p.getVariant().equals(variant))
                    .forEach(p -> {
                        v[0] += getInfants() * p.getInfantPrice();
                        v[0] += getChildren() * p.getChildPrice();
                        v[0] += getJuniors() * p.getJuniorPrice();
                        v[0] += getAdults() * p.getAdultPrice();
                        v[0] += getSeniors() * p.getSeniorPrice();
                    });
            if (v[0] != 0) prices. put(c, v[0]);
        });

        setTotalValue(0);
        prices.keySet().stream().min((v0, v1) -> prices.get(v0).compareTo(prices.get(v1))).ifPresent(v -> {
            setTotalValue(Helper.roundEuros(prices.get(v)));
            setContract(v);
        });
    }

    @Override
    protected BillingConcept getDefaultBillingConcept(EntityManager em) {
        return AppConfig.get(em).getBillingConceptForCircuit();
    }

    @Override
    protected void completeSignature(Map<String, Object> m) {
        if (getCircuit() != null) m.put("circuit", getCircuit().getName());
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

        h[0] += "CIRCUIT BOOKING \n";

        h[0] += "Start: " + getStart().format(DateTimeFormatter.ISO_DATE) + "\n";
        h[0] += "End: " + getEnd().format(DateTimeFormatter.ISO_DATE) + "\n";
        h[0] += "Circuit: " + getCircuit().getName() + " \n";
        if (getVariant() != null) h[0] += "Variant: " + getVariant().getName() + " \n";

        if (getInfants() != 0) h[0] += getInfants() + " infants \n";
        if (getChildren() != 0) h[0] += getChildren() + " children \n";
        if (getJuniors() != 0) h[0] += getJuniors() + " juniors \n";
        if (getAdults() != 0) h[0] += getAdults() + " adults \n";
        if (getSeniors() != 0) h[0] += getSeniors() + " seniors \n";

        for (TourBookingExtra e : getExtras()) {
            h[0] += "Extra: " + e.getExtra().getName() + " X " + e.getUnits() + " \n";
        }

        if (!Strings.isNullOrEmpty(getSpecialRequests())) h[0] += "Special requests: " + getSpecialRequests() + "\n";

        h[0] += "</pre>";
        return h[0];
    }
}
