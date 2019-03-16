package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.ValidationStatus;
import io.mateu.erp.model.booking.excursion.ExcursionService;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.TourShift;
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
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Getter@Setter
public class ExcursionBooking extends TourBooking {

    @ManyToOne
    @NotNull
    @Position(13)
    private Excursion excursion;


    @ManyToOne
    @NotNull
    @Position(14)
    private Variant variant;

    @ManyToOne
    @NotNull
    @Position(15)
    private TourShift shift;


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
            me.setUnitsBooked(1);
            me.setUnitsLeft(-1);
            me.setMaxUnits(0);
            em.persist(me);
        }
        if (!me.getBookings().contains(this)) {
            me.getBookings().add(this);
        }

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
        // si la excusión es de terceros
        if (excursion.isCostPerTicket()) {
            ExcursionService s = null;
            for (Service x : oldServices) if (x instanceof ExcursionService) s = (ExcursionService) x;
            if (s != null) oldServices.remove(s);
            else {
                ExcursionService gs;
                getServices().add(gs = new ExcursionService());
                gs.setBooking(this);
            }
            ExcursionService gs;
            getServices().add(gs = new ExcursionService());
            gs.setBooking(this);
            gs.setActive(isActive());
            gs.setAdults(getAdults());
            gs.setChildren(getChildren());
            gs.setUnits(0);
            gs.setExcursion(getExcursion());
            gs.setVariant(getVariant());
            gs.setShift(getShift());
            gs.setStart(getStart());
            gs.setFinish(getEnd());
            gs.setAudit(new Audit(MDD.getCurrentUser()));
            gs.setManagedEvent(finalMe);
        }


        oldServices.forEach(s -> s.cancel(em, MDD.getCurrentUser()));

    }

    @Override
    public void priceServices(EntityManager em) {
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
                dias = (int) DAYS.between(getStart(), getEnd());
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
        c.setText("" + excursion.getName() + " from " + getStart().toString() + " to " + getEnd().toString() + " for " + getAdults() + " ad/" + getChildren() + "ch");
        c.setBillingConcept(getContract().getBillingConcept());
        c.setAgency(getAgency());
    }

    @Override
    protected void completeSignature(Map<String, Object> m) {

    }
}
