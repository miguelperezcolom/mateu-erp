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
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.ExcursionLanguage;
import io.mateu.erp.model.product.tour.ExcursionShift;
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
    @NotNull
    @Position(19)
    private Variant variant;

    public DataProvider getVariantDataProvider() {
        return new ListDataProvider(excursion != null?excursion.getVariants():new ArrayList());
    }

    @ManyToOne
    @NotNull
    @Position(20)
    private ExcursionShift shift;

    public DataProvider getShiftDataProvider() {
        return new ListDataProvider(excursion != null?excursion.getShifts():new ArrayList());
    }

    @ManyToOne
    @NotNull
    @Position(21)
    private ExcursionLanguage language;

    public DataProvider getLanguageDataProvider() {
        return new ListDataProvider(shift != null?shift.getLanguages():new ArrayList());
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
            s.setAdults(getAdults());
            s.setChildren(getChildren());
            s.setUnits(c.getUnits());
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
                        v[0] += getAdults() * p.getPricePerAdult();
                        v[0] += getChildren() * p.getPricePerChild();
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

    public String getChargeSubject() {
        return "" + excursion.getName() + " from " + getStart().toString() + " to " + getEnd().toString() + " for " + getAdults() + " ad/" + getChildren() + "ch";
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
        m.put("adults", getAdults());
        m.put("children", getChildren());
        if (getProvider() != null) m.put("provider", "" + getProvider().getId() + " - " + getProvider().getName());
        m.put("serviceCost", "" + getOverridedCost());
    }


    @Override
    public String getServiceDataHtml() {
        String h = "<pre>";

        h += "EXCURSION BOOKING \n";

        h += "Start: " + getStart().format(DateTimeFormatter.ISO_DATE) + "\n";
        h += "End: " + getEnd().format(DateTimeFormatter.ISO_DATE) + "\n";
        h += "Excursion: " + getExcursion().getName() + " \n";
        if (getVariant() != null) h += "Variant: " + getVariant().getName() + " \n";
        if (getShift() != null) h += "Shift: " + getShift().getName() + " \n";
        h += "Adults: " + getAdults() + " \n";
        h += "Children: " + getChildren() + " \n";

        for (TourBookingExtra e : getExtras()) {
            h += "Extra: " + e.getExtra().getName() + " X " + e.getUnits() + " \n";
        }

        if (!Strings.isNullOrEmpty(getSpecialRequests())) h += "Special requests: " + getSpecialRequests() + "\n";

        h += "</pre>";
        return h;
    }

}
