package io.mateu.erp.model.booking.excursion;

import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.ServiceType;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.TourPrice;
import io.mateu.erp.model.product.tour.TourShift;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity@Getter@Setter
public class ExcursionService extends Service {

    @ManyToOne@NotNull@Output
    private Excursion excursion;

    @ManyToOne@NotNull@Output
    private Variant variant;

    @ManyToOne@NotNull@Output
    private TourShift shift;


    private int units;

    private int adults;

    private int children;






    public ExcursionService() {
        setServiceType(ServiceType.EXCURSION);
        setIcons("<i class=\"v-icon fas fa-hiking\"></i>");
    }


    @Override
    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = toMap();
            m.put("excursion", getExcursion().getName());
            m.put("variant", getVariant().getName());
            m.put("shift", getShift().getName());
            m.put("units", getUnits());
            m.put("adults", getAdults());
            m.put("children", getChildren());
            m.put("cancelled", "" + !isActive());
            m.put("opsComment", "" + getOperationsComment());
            m.put("comment", "" + getBooking().getSpecialRequests());
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public double rateSale(EntityManager em, PrintWriter report) throws Throwable {
        Map<io.mateu.erp.model.product.tour.Contract, Double> prices = new HashMap<>();
        Accessor.get(em).getTourContracts().stream().filter(c ->
                ContractType.SALE.equals(c.getType())
                        && c.isActive()
                        && (c.getValidFrom() == null || getFinish() == null || !c.getValidFrom().isAfter(getFinish()))
                        && (c.getValidTo() == null || getStart() == null || !c.getValidTo().isBefore(getStart()))
        ).forEach(c -> {
            final double[] v = {0};
            int dias = 1;
            if (getStart() != null && getFinish() != null) {
                dias = (int) DAYS.between(getStart(), getFinish());
            }
            int finalDias = dias;
            c.getPrices().stream()
                    .sorted((p0, p1) -> p0.getOrder() - p1.getOrder())
                    .filter(p -> p.getTour() == null || p.getTour().equals(excursion))
                    .filter(p -> p.getVariant() == null || p.getVariant().equals(getVariant()))
                    .forEach(p -> {
                        v[0] += getAdults() * p.getPricePerAdult();
                        v[0] += getChildren() * p.getPricePerChild();
                    });
            prices. put(c, v[0]);
        });

        AtomicReference<Double> min = new AtomicReference<>((double) 0);
        prices.keySet().stream().min((v0, v1) -> prices.get(v0).compareTo(prices.get(v1))).ifPresent(v -> {
            min.set(Helper.roundEuros(prices.get(v)));
        });

        return min.get();
    }

    @Override
    public double rateCost(EntityManager em, Provider supplier, PrintWriter report) throws Throwable {
        Map<io.mateu.erp.model.product.tour.Contract, Double> prices = new HashMap<>();
        Accessor.get(em).getTourContracts().stream().filter(c ->
                ContractType.PURCHASE.equals(c.getType())
                        && c.isActive()
                        && (c.getValidFrom() == null || getFinish() == null || !c.getValidFrom().isAfter(getFinish()))
                        && (c.getValidTo() == null || getStart() == null || !c.getValidTo().isBefore(getStart()))
        ).forEach(c -> {
            final double[] v = {0};
            int dias = 1;
            if (getStart() != null && getFinish() != null) {
                dias = (int) DAYS.between(getStart(), getFinish());
            }
            int finalDias = dias;
            c.getPrices().stream()
                    .sorted((p0, p1) -> p0.getOrder() - p1.getOrder())
                    .filter(p -> p.getTour() == null || p.getTour().equals(excursion))
                    .filter(p -> p.getVariant() == null || p.getVariant().equals(getVariant()))
                    .forEach(p -> {
                        v[0] += getAdults() * p.getPricePerAdult();
                        v[0] += getChildren() * p.getPricePerChild();
                    });
            prices. put(c, v[0]);
        });

        AtomicReference<Double> min = new AtomicReference<>((double) 0);
        prices.keySet().stream().min((v0, v1) -> prices.get(v0).compareTo(prices.get(v1))).ifPresent(v -> {
            min.set(Helper.roundEuros(prices.get(v)));
        });

        return min.get();

    }

    @Override
    public Provider findBestProvider(EntityManager em) throws Throwable {
        // seleccionamos los contratos válidos
        List<io.mateu.erp.model.product.tour.Contract> contracts = new ArrayList<>();
        for (io.mateu.erp.model.product.tour.Contract c : Accessor.get(em).getTourContracts()) {
            boolean ok = true;
            ok &= ContractType.PURCHASE.equals(c.getType());
            ok &= c.getAgencies().size() == 0 || c.getAgencies().contains(this.getBooking().getAgency());
            ok &= c.getValidFrom().isBefore(getStart());
            ok &= c.getValidTo().isAfter(getFinish());
            LocalDate created = (getAudit() != null && getAudit().getCreated() != null)?getAudit().getCreated().toLocalDate():LocalDate.now();
            ok &= c.getBookingWindowFrom() == null || c.getBookingWindowFrom().isBefore(created) || c.getBookingWindowFrom().equals(created);
            ok &= c.getBookingWindowTo() == null || c.getBookingWindowTo().isAfter(created) || c.getBookingWindowTo().equals(created);
            if (ok) contracts.add(c);
        }
        if (contracts.size() == 0) throw new Exception("No valid contract");

        List<TourPrice> prices = new ArrayList<>();
        for (io.mateu.erp.model.product.tour.Contract c : contracts) for (TourPrice p : c.getPrices()) {
            boolean ok = true;
            ok &= p.getTour().equals(excursion);

            if (ok) prices.add(p);
        }
        if (prices.size() == 0) throw new Exception("No valid price in selectable contracts");

        // valoramos con cada uno de ellos y nos quedamos con el precio más económico
        double value = Double.MAX_VALUE;
        Provider provider = null;
        long noches = DAYS.between(getStart(), getFinish());
        for (TourPrice p : prices) {
            double v = 0;
            v += getAdults() * p.getPricePerAdult();
            v += getChildren() * p.getPricePerChild();
            if (v < value) {
                value = v;
                provider = p.getContract().getSupplier();
            }
        }

        return provider;

    }

    @Override
    protected String getDescription() {
        return "" + ((getExcursion() != null)?getExcursion().getName():"")
                + "-" + ((getVariant() != null)?getVariant().getName():"")
                + "-" + ((getShift() != null)? getShift().getName():"");
    }

    @Override
    public BillingConcept getBillingConcept(EntityManager em) {
        return AppConfig.get(em).getBillingConceptForExcursion();
    }

}
