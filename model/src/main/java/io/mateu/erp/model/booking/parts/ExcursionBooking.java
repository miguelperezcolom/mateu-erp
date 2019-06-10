package io.mateu.erp.model.booking.parts;

import com.google.common.base.Strings;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.erp.model.booking.PriceBreakdownItem;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.ValidationStatus;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.organization.SalesPoint;
import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.ExcursionLanguage;
import io.mateu.erp.model.product.tour.ExcursionShift;
import io.mateu.erp.model.product.tour.TourPickupTime;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Getter@Setter
public class ExcursionBooking extends TourBooking {

    @ManyToOne
    @NotNull
    @Position(18)
    private Excursion excursion;


    @ManyToOne
    @Position(19)
    private Variant variant;

    public DataProvider getVariantDataProvider() {
        return new ListDataProvider(excursion != null?excursion.getVariants():new ArrayList());
    }

    @ManyToOne
    @Position(20)
    private ExcursionShift shift;

    public DataProvider getShiftDataProvider() {
        return new ListDataProvider(excursion != null?excursion.getShifts():new ArrayList());
    }

    @ManyToOne
    @Position(21)
    private ExcursionLanguage language;

    public DataProvider getLanguageDataProvider() {
        return new ListDataProvider(shift != null?shift.getLanguages():new ArrayList());
    }

    public void setSalesPoint(SalesPoint salesPoint) {
        super.setSalesPoint(salesPoint);
        if (salesPoint != null && salesPoint.getPickupPoint() != null) {
            setPickup(salesPoint.getPickupPoint());
        }
    }

    public void setPickup(TransferPoint pickup) {
        super.setPickup(pickup);
        if (pickup != null) for (TourPickupTime t : getShift().getPickupTimes()) {
            if (pickup.equals(t.getPoint()) && t.getTime() != null) setPickupTime(t.getTime().atDate(getStart()));
        }
    }



    public boolean isEndOutput() { return true; }


    public ExcursionBooking() {
        setIcons("<i class=\"v-icon fas fa-hiking\"></i>");
    }


    @Override
    protected void completeChangeSignatureData(Map<String, String> data) {

    }

    @Override
    public void validate() throws Exception {
        setValidationStatus(ValidationStatus.VALID);
        setAvailable(true);
    }

    @Override
    public void generateServices(EntityManager em) {
        // necesitamos el evento
        Optional<ManagedEvent> oe = excursion.getEvents().stream().filter(e -> e.getDate().equals(getStart()) && e.getShift().equals(getShift())).findFirst();
        ManagedEvent me = null;
        if (oe.isPresent()) {
            me = oe.get();
        } else {
            excursion.getEvents().add(me = new ManagedEvent());
            me.setTour(excursion);
            me.setShift(shift);
            me.setActive(true);
            me.setOffice(excursion.getOffice());
            me.setDate(getStart());
            me.setMaxUnits(shift.getMaxPax());
            em.persist(me);
        }
        if (!me.getBookings().contains(this)) {
            me.getBookings().add(this);
        }
        setManagedEvent(me);
        me.setUpdatePending(true);


        List<Service> oldServices = new ArrayList<>(getServices());

        ManagedEvent finalMe = me;
        excursion.getCosts().forEach(c -> {
            // si la excursión es propia
            GenericService s = null;
            for (Service x : oldServices) if (x instanceof GenericService) s = (GenericService) x;
            if (s != null) oldServices.remove(s);
            else {
                GenericService gs;
                getServices().add(gs = s = new GenericService());
                gs.setBooking(this);
            }
            s.setActive(isActive());

            s.setInfants(getInfants());
            s.setChildren(getChildren());
            s.setJuniors(getJuniors());
            s.setAdults(getAdults());
            s.setSeniors(getSeniors());

            s.setVariant(c.getProductVariant());
            s.setProduct((GenericProduct) c.getProduct());
            s.setDeliveryDate(getStart());
            s.setReturnDate(getEnd());
            s.setAudit(new Audit(MDD.getCurrentUser()));
            s.setManagedEvent(finalMe);
            s.setOffice(finalMe.getOffice());
        });

        oldServices.forEach(s -> s.cancel(em, MDD.getCurrentUser()));

    }

    @Override
    protected ProductLine getEffectiveProductLine() {
        return getExcursion().getProductLine();
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
                    .filter(p -> p.getTour() == null || p.getTour().equals(excursion))
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

        breakdown.add(new PriceBreakdownItem(getContract() != null?getContract().getBillingConcept():AppConfig.get(em).getBillingConceptForExcursion(), getChargeSubject(), getTotalValue()));
    }

    @Override
    protected BillingConcept getDefaultBillingConcept(EntityManager em) {
        return AppConfig.get(em).getBillingConceptForExcursion();
    }

    public String getChargeSubject() {
        return "" + excursion.getName() + " from " + getStart().toString() + " to " + getEnd().toString() + " for " + getPax() + " pax";
    }

    @Override
    protected void completeSignature(Map<String, Object> m) {
        if (getExcursion() != null) m.put("excursion", getExcursion().getName());
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

        h[0] += "EXCURSION BOOKING \n";

        h[0] += "Start: " + getStart().format(DateTimeFormatter.ISO_DATE) + "\n";
        h[0] += "End: " + getEnd().format(DateTimeFormatter.ISO_DATE) + "\n";
        h[0] += "Excursion: " + getExcursion().getName() + " \n";
        if (getVariant() != null) h[0] += "Variant: " + getVariant().getName() + " \n";
        if (getShift() != null) h[0] += "Shift: " + getShift().getName() + " \n";

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
