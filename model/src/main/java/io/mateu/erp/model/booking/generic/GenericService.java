package io.mateu.erp.model.booking.generic;

import com.google.common.base.Strings;
import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.ServiceType;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.generic.Contract;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.erp.model.product.generic.Price;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by miguel on 12/4/17.
 */
@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class GenericService extends Service {

    @Tab("Service")
    @NotNull
    @ManyToOne
    @ListColumn@Output
    private GenericProduct product;

    @ManyToOne
    @ListColumn@Output
    private Variant variant;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    @Output
    private List<GenericServiceExtra> extras = new ArrayList<>();



    @NotNull@Output
    private LocalDate deliveryDate;

    @NotNull@Output
    private LocalDate returnDate;


    public GenericService() {
        setServiceType(ServiceType.GENERIC);
        setIcons(FontAwesome.GIFT.getHtml());
    }


    @PrePersist@PreUpdate
    public void pre(){
        setStart(getDeliveryDate());
        setFinish(getReturnDate());
        super.pre();
    }



    @Override
    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = toMap();
            m.put("leadName", getBooking().getLeadName());
            m.put("product", getProduct().getName());
            if (getVariant() != null) m.put("variant", getVariant().getName());
            List<Map<String, Object>> ls = new ArrayList<>();
            for (GenericServiceExtra l : getExtras()) {
                Map<String, Object> x;
                ls.add(x = new HashMap<>());
                x.put("description", l.getExtra().getName().toString());
                x.put("units", l.getUnits());
            }

            m.put("infants", getInfants());
            m.put("children", getChildren());
            m.put("juniors", getJuniors());
            m.put("adults", getAdults());
            m.put("seniors", getSeniors());

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
        return 0;
    }

    @Override
    public double rateCost(EntityManager em, Provider supplier, PrintWriter report) throws Throwable {

        Map<Contract, Double> prices = new HashMap<>();
        Accessor.get(em).getGenericContracts().stream().filter(c ->
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

        AtomicReference<Double> min = new AtomicReference<>((double) 0);
        prices.keySet().stream().min((v0, v1) -> prices.get(v0).compareTo(prices.get(v1))).ifPresent(v -> {
            min.set(Helper.roundEuros(prices.get(v)));
        });

        return min.get();
    }

    @Override
    public Provider findBestProvider(EntityManager em) throws Throwable {
        // seleccionamos los contratos válidos
        List<Contract> contracts = new ArrayList<>();
        for (Contract c : Accessor.get(em).getGenericContracts()) {
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

        List<Price> prices = new ArrayList<>();
        for (Contract c : contracts) for (Price p : c.getPrices()) {
            boolean ok = true;
            ok &= p.getProduct().equals(getProduct());

            if (ok) prices.add(p);
        }
        if (prices.size() == 0) throw new Exception("No valid price in selectable contracts");

        // valoramos con cada uno de ellos y nos quedamos con el precio más económico
        double value = Double.MAX_VALUE;
        Provider provider = null;
        long noches = DAYS.between(getStart(), getFinish());
        for (Price p : prices) {
            double v = 0;

            v += getInfants() * p.getInfantPrice();
            v += getChildren() * p.getChildPrice();
            v += getJuniors() * p.getJuniorPrice();
            v += getAdults() * p.getAdultPrice();
            v += getSeniors() * p.getSeniorPrice();
            v += noches * getInfants() * p.getInfantPricePerDay();
            v += noches * getChildren() * p.getChildPricePerDay();
            v += noches * getJuniors() * p.getJuniorPricePerDay();
            v += noches * getAdults() * p.getAdultPricePerDay();
            v += noches * getSeniors() * p.getSeniorPricePerDay();

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
        d.put("locator", this.getBooking().getId());
        d.put("leadName", this.getBooking().getLeadName());
        d.put("agency", this.getBooking().getAgency().getName());
        d.put("agencyReference", this.getBooking().getAgencyReference());
        d.put("status", (isActive())?"ACTIVE":"CANCELLED");
        if (getAudit().getCreated() != null) d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        d.put("office", getOffice().getName());

        String c = getBooking().getSpecialRequests();
        if (!Strings.isNullOrEmpty(getOperationsComment())) {
            if (c == null) c = "";
            else if (!"".equals(c)) c += " / ";
            c += getOperationsComment();
        }
        d.put("comments", c);

        d.put("product", getProduct().getName());
        d.put("variant", getVariant().getName().getEs());

        return d;
    }

    @Override
    protected String getDescription() {
        return "" + ((getProduct() != null)?getProduct().getName():"");
    }


    public String getSubitle() {
        return super.toString();
    }

    @Override
    public String toString() {
        return (getProduct() != null)?getProduct().getName():"--";
    }

    @Override
    public BillingConcept getBillingConcept(EntityManager em) {
        return AppConfig.get(em).getBillingConceptForOthers();
    }


    @Override
    public Element toXml() {
        Element xml = super.toXml();

        xml.setAttribute("type", "generic");

        xml.setAttribute("header", "" + getDescription());

        if (getProduct() != null) xml.setAttribute("product", getProduct().getName());
        if (getVariant() != null) xml.setAttribute("variant", getVariant().getName().getEs());
        final String[] s = {""};
        if (getProduct().isDateDependant()) {
            if (!"".equalsIgnoreCase(s[0])) s[0] += " / ";
            s[0] += getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (getProduct().isDatesRangeDependant()) {
            if (!"".equalsIgnoreCase(s[0])) s[0] += " / ";
            s[0] += getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            s[0] += " - ";
            s[0] += getFinish().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        xml.setAttribute("units", s[0]);

        return xml;
    }

}
