package io.mateu.erp.model.product.generic;

import com.vaadin.data.provider.DataProvider;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.product.Tariff;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.tour.Tour;
import io.mateu.erp.model.product.tour.TourDuration;
import io.mateu.erp.model.product.tour.ExcursionPriceZone;
import io.mateu.mdd.core.annotations.DependsOn;
import io.mateu.mdd.core.annotations.Keep;
import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by miguel on 31/1/17.
 */
@Entity(name = "GenericContractPrice")
@Getter
@Setter
public class Price implements Comparable<Price> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull@Keep
    private Contract contract;

    @ManyToOne
    private GenericProduct product;

    @ManyToOne
    private Variant variant;

    @DependsOn("product")
    public DataProvider getVariantDataProvider() throws Throwable {
        return new JPQLListDataProvider(
                "select x from " + GenericProduct.class.getName() + " y inner join y.variants x " +
                        ((getProduct() != null)?" where y.id = " + getProduct().getId():""));
    }

    @ManyToOne
    private Extra extra;

    @Column(name = "_order")
    private int order;

    private boolean active = true;

    @ManyToOne
    private BillingConcept billingConcept;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    @SameLine
    private LocalDate end;

    private String description;

    private LocalDate bookingWindowStart;

    @SameLine
    private LocalDate bookingWindowEnd;

    @ManyToOne@NotNull
    private Tariff tariff;

    private int minPax;

    private double percent;

    private double unitPrice;
    @SameLine
    private double paxPrice;
    @SameLine
    private double infantPrice;
    @SameLine
    private double childPrice;
    @SameLine
    private double juniorPrice;
    @SameLine
    private double adultPrice;
    @SameLine
    private double seniorPrice;

    private double unitPricePerDay;
    @SameLine
    private double paxPricePerDay;
    @SameLine
    private double infantPricePerDay;
    @SameLine
    private double childPricePerDay;
    @SameLine
    private double juniorPricePerDay;
    @SameLine
    private double adultPricePerDay;
    @SameLine
    private double seniorPricePerDay;

    private boolean finalPrice;

    @ManyToOne
    private Tour tour;
    private TourDuration tourDuration;
    @ManyToOne
    private ExcursionPriceZone tourPriceZone;

    private int fromTourPax;
    @SameLine
    private int toTourPax;



    public Element toXml() {
        Element e = new Element("price");

        if (extra != null) e.addContent(new Element("extra").setAttribute("id", "" + extra.getId()).setAttribute("name", extra.getName().toString()));

        e.setAttribute("order", "" + order);

        if (active) e.setAttribute("active", "");
        if (finalPrice) e.setAttribute("finalPrice", "");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (start != null) e.setAttribute("start", start.format(dtf));
        if (end != null) e.setAttribute("end", end.format(dtf));

        if (billingConcept != null) e.addContent(new Element("billingConcept").setAttribute("id", "" + billingConcept.getCode()).setAttribute("name", billingConcept.getName()));

        if (description != null) e.setAttribute("description", description);

        if (bookingWindowStart != null) e.setAttribute("bwstart", bookingWindowStart.format(dtf));
        if (bookingWindowEnd != null) e.setAttribute("bwend", bookingWindowEnd.format(dtf));

        e.setAttribute("percent", "" + percent);
        if (tariff != null) e.setAttribute("tariff", "" + tariff);

        e.setAttribute("unitPrice", "" + unitPrice);
        e.setAttribute("infantPrice", "" + infantPrice);
        e.setAttribute("childPrice", "" + childPrice);
        e.setAttribute("juniorPrice", "" + juniorPrice);
        e.setAttribute("adultPrice", "" + adultPrice);
        e.setAttribute("seniorPrice", "" + seniorPrice);
        e.setAttribute("unitPricePerDay", "" + unitPricePerDay);
        e.setAttribute("infantPricePerDay", "" + infantPricePerDay);
        e.setAttribute("childPricePerDay", "" + childPricePerDay);
        e.setAttribute("juniorPricePerDay", "" + juniorPricePerDay);
        e.setAttribute("adultPricePerDay", "" + adultPricePerDay);
        e.setAttribute("seniorPricePerDay", "" + seniorPricePerDay);


        if (tour != null) e.addContent(new Element("tour").setAttribute("id", "" + tour.getId()).setAttribute("name", tour.getName()));
        if (tourDuration != null) e.setAttribute("tourDuration", "" + tourDuration);
        if (tourPriceZone != null) e.addContent(new Element("tourPriceZone").setAttribute("id", "" + tourPriceZone.getId()).setAttribute("name", tourPriceZone.getName()));
        e.setAttribute("fromTourPax", "" + fromTourPax);
        e.setAttribute("toTourPax", "" + toTourPax);


        e.setAttribute("pdftext", toString());

        return e;
    }


    @Override
    public String toString() {
        String s = "";

        s += (finalPrice)?"Final":"Add";

        if (description != null) {
            if (!"".equals(s)) s += " ";
            s += description;
        }

        if (extra != null) {
            if (!"".equals(s)) s += ", ";
            s += "for " + extra.getName().toString();
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (start != null) {
            if (!"".equals(s)) s += ", ";
            s += "from " + start.format(dtf);
        }
        if (end != null) {
            if (!"".equals(s)) s += ", ";
            s += "to " + end.format(dtf);
        }


        if (bookingWindowStart != null) {
            if (!"".equals(s)) s += ", ";
            s += "booked from " + bookingWindowStart.format(dtf);
        }
        if (bookingWindowEnd != null) {
            if (!"".equals(s)) s += ", ";
            s += "booked to " + bookingWindowEnd.format(dtf);
        }


        if (percent != 0) {
            if (!"".equals(s)) s += ", ";
            s += percent + "%";
        }

        if (unitPrice != 0) {
            if (!"".equals(s)) s += ", ";
            s += unitPrice + " per unit";
        }
        if (infantPrice != 0) {
            if (!"".equals(s)) s += ", ";
            s += infantPrice + " per infant";
        }
        if (childPrice != 0) {
            if (!"".equals(s)) s += ", ";
            s += childPrice + " per child";
        }
        if (juniorPrice != 0) {
            if (!"".equals(s)) s += ", ";
            s += juniorPrice + " per junior";
        }
        if (adultPrice != 0) {
            if (!"".equals(s)) s += ", ";
            s += adultPrice + " per adult";
        }
        if (seniorPrice != 0) {
            if (!"".equals(s)) s += ", ";
            s += seniorPrice + " per senior";
        }

        if (unitPricePerDay != 0) {
            if (!"".equals(s)) s += ", ";
            s += unitPricePerDay + " per unit/day";
        }
        if (infantPricePerDay != 0) {
            if (!"".equals(s)) s += ", ";
            s += infantPricePerDay + " per infant/day";
        }
        if (childPricePerDay != 0) {
            if (!"".equals(s)) s += ", ";
            s += childPricePerDay + " per child/day";
        }
        if (juniorPricePerDay != 0) {
            if (!"".equals(s)) s += ", ";
            s += juniorPricePerDay + " per junior/day";
        }
        if (adultPricePerDay != 0) {
            if (!"".equals(s)) s += ", ";
            s += adultPricePerDay + " per adult/day";
        }
        if (seniorPricePerDay != 0) {
            if (!"".equals(s)) s += ", ";
            s += seniorPricePerDay + " per senior/day";
        }


        if (tour != null) {
            if (!"".equals(s)) s += ", ";
            s += "for " + tour.getName() + " tour";
        }
        if (tourDuration != null) {
            if (!"".equals(s)) s += ", ";
            s += "for " + tourDuration + " tours";
        }
        if (tourPriceZone != null) {
            if (!"".equals(s)) s += ", ";
            s += "for tours from " + tourPriceZone.getName();
        }
        if (fromTourPax != 0) {
            if (!"".equals(s)) s += ", ";
            s += "for tours from " + fromTourPax + " pax";
        }
        if (toTourPax != 0) {
            if (!"".equals(s)) s += ", ";
            s += "for tours up to " + toTourPax + " pax";
        }
        return s;
    }

    @Override
    public int compareTo(@org.jetbrains.annotations.NotNull Price o) {
        int r = 0;
        if (o == null) return 1;
        r = getOrder() - o.getOrder();
        if (r == 0) {
            r = ((getProduct() != null)?getProduct().getName():"").compareTo(((o.getProduct() != null)?o.getProduct().getName():""));
        }
        if (r == 0) {
            r = ((getExtra() != null)?getExtra().getName().toString():"").compareTo(((o.getExtra() != null)?o.getExtra().getName().toString():""));
        }
        return r;
    }
}
