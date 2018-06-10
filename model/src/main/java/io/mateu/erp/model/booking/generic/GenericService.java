package io.mateu.erp.model.booking.generic;

import com.google.common.base.Strings;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.generic.Contract;
import io.mateu.erp.model.product.generic.Extra;
import io.mateu.erp.model.product.generic.Price;
import io.mateu.erp.model.product.generic.Product;
import io.mateu.ui.mdd.server.annotations.Subtitle;
import io.mateu.ui.mdd.server.annotations.Tab;
import io.mateu.ui.mdd.server.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by miguel on 12/4/17.
 */
@Entity
@Getter
@Setter
public class GenericService extends Service {

    @Tab("Service")
    @NotNull
    @ManyToOne
    private Product product;

    @OneToMany
    @OrderBy("id asc")
    private List<Extra> extras = new ArrayList<>();

    private int units;

    private int adults;

    private int children;

    @NotNull
    private LocalDate deliveryDate;

    @NotNull
    private LocalDate returnDate;


    @PrePersist@PreUpdate
    public void pre(){
        setStart(getDeliveryDate());
        setFinish(getReturnDate());
    }



    @Override
    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = toMap();
            m.put("product", getProduct().getName());
            List<Map<String, Object>> ls = new ArrayList<>();
            for (Extra l : getExtras()) {
                Map<String, Object> x;
                ls.add(x = new HashMap<>());
                x.put("description", l.getName());
            }

            m.put("units", getUnits());
            m.put("adults", getAdults());
            m.put("children", getChildren());
            m.put("cancelled", "" + isCancelled());
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
    @Override
    public double rate(EntityManager em, boolean sale, Partner supplier, PrintWriter report) throws Throwable {
        // seleccionamos los contratos válidos
        List<Contract> contracts = new ArrayList<>();
        for (Contract c : (List<Contract>) em.createQuery("select x from " + Contract.class.getName() + " x").getResultList()) {
            boolean ok = true;
            ok &= ContractType.SALE.equals(c.getType());
            ok &= c.getTargets().size() == 0 || c.getTargets().contains(getBooking().getAgency());
            ok &= c.getValidFrom().isBefore(getStart());
            ok &= c.getValidTo().isAfter(getFinish());
            LocalDate created = (getAudit() != null && getAudit().getCreated() != null)?getAudit().getCreated().toLocalDate():LocalDate.now();
            ok &= c.getBookingWindowFrom() == null || c.getBookingWindowFrom().isBefore(created) || c.getBookingWindowFrom().equals(created);
            ok &= c.getBookingWindowTo() == null || c.getBookingWindowTo().isAfter(created) || c.getBookingWindowTo().equals(created);
            if (ok) contracts.add(c);
        }
        if (contracts.size() == 0) throw new Exception("No valid contract");

        List<Price> prices = new ArrayList<>();
        for (Contract c : contracts) for (Price p : c.getPrices()) {
            boolean ok = true;
            ok &= p.getProduct().equals(getProduct());

            if (ok) prices.add(p);
        }
        if (prices.size() == 0) throw new Exception("No valid price in selectable contracts");

        // valoramos con cada uno de ellos y nos quedamos con el precio más económico
        double value = Double.MAX_VALUE;
        Partner provider = null;
        long noches = DAYS.between(getStart(), getFinish());
        for (Price p : prices) {
            double v = 0;
            v += getUnits() * (p.getPricePerUnit() + noches * p.getPricePerUnitAndDay());
            v += getAdults() * (p.getPricePerAdult() + noches * p.getPricePerAdultAndDay());
            v += getChildren() * (p.getPricePerChild() + noches * p.getPricePerChildAndDay());
            if (v < value) {
                value = v;
            }
        }

        return value;
    }

    @Override
    public Partner findBestProvider(EntityManager em) throws Throwable {
        // seleccionamos los contratos válidos
        List<Contract> contracts = new ArrayList<>();
        for (Contract c : (List<Contract>) em.createQuery("select x from " + Contract.class.getName() + " x").getResultList()) {
            boolean ok = true;
            ok &= ContractType.PURCHASE.equals(c.getType());
            ok &= c.getTargets().size() == 0 || c.getTargets().contains(getBooking().getAgency());
            ok &= c.getValidFrom().isBefore(getStart());
            ok &= c.getValidTo().isAfter(getFinish());
            LocalDate created = (getAudit() != null && getAudit().getCreated() != null)?getAudit().getCreated().toLocalDate():LocalDate.now();
            ok &= c.getBookingWindowFrom() == null || c.getBookingWindowFrom().isBefore(created) || c.getBookingWindowFrom().equals(created);
            ok &= c.getBookingWindowTo() == null || c.getBookingWindowTo().isAfter(created) || c.getBookingWindowTo().equals(created);
            if (ok) contracts.add(c);
        }
        if (contracts.size() == 0) throw new Exception("No valid contract");

        List<Price> prices = new ArrayList<>();
        for (Contract c : contracts) for (Price p : c.getPrices()) {
            boolean ok = true;
            ok &= p.getProduct().equals(getProduct());

            if (ok) prices.add(p);
        }
        if (prices.size() == 0) throw new Exception("No valid price in selectable contracts");

        // valoramos con cada uno de ellos y nos quedamos con el precio más económico
        double value = Double.MAX_VALUE;
        Partner provider = null;
        long noches = DAYS.between(getStart(), getFinish());
        for (Price p : prices) {
            double v = 0;
            v += getUnits() * (p.getPricePerUnit() + noches * p.getPricePerUnitAndDay());
            v += getAdults() * (p.getPricePerAdult() + noches * p.getPricePerAdultAndDay());
            v += getChildren() * (p.getPricePerChild() + noches * p.getPricePerChildAndDay());
            if (v < value) {
                value = v;
                provider = p.getContract().getSupplier();
            }
        }

        return provider;

    }


    public Map<String,Object> getData() {
        Map<String, Object> d = super.getData();

        d.put("id", getId());
        d.put("locator", getBooking().getId());
        d.put("leadName", getBooking().getLeadName());
        d.put("agency", getBooking().getAgency().getName());
        d.put("agencyReference", getBooking().getAgencyReference());
        d.put("status", (isCancelled())?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        d.put("office", getOffice().getName());

        String c = getComment();
        if (!Strings.isNullOrEmpty(getOperationsComment())) {
            if (c == null) c = "";
            else if (!"".equals(c)) c += " / ";
            c += getOperationsComment();
        }
        d.put("comments", c);

        return d;
    }


    @Subtitle
    public String getSubitle() {
        return super.toString();
    }

    @Override
    public String toString() {
        return (getProduct() != null)?getProduct().getName():"--";
    }
}
