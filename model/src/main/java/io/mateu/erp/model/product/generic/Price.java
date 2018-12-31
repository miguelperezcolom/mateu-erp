package io.mateu.erp.model.product.generic;

import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.product.tour.Tour;
import io.mateu.erp.model.product.tour.TourDuration;
import io.mateu.erp.model.product.tour.TourPriceZone;
import io.mateu.mdd.core.annotations.SameLine;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
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
    @NotNull
    private Contract contract;

    @ManyToOne
    private GenericProduct product;

    @ManyToOne
    private Extra extra;

    @Column(name = "_order")
    private int order;

    private boolean active = true;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    @SameLine
    private LocalDate end;

    @ManyToOne
    private BillingConcept billingConcept;

    @NotEmpty
    private String description;

    private LocalDate bookingWindowStart;

    @SameLine
    private LocalDate bookingWindowEnd;

    private double percent;

    private double pricePerUnit;
    @SameLine
    private double pricePerAdult;
    @SameLine
    private double pricePerChild;
    private double pricePerUnitAndDay;
    @SameLine
    private double pricePerAdultAndDay;
    @SameLine
    private double pricePerChildAndDay;

    private boolean finalPrice;

    @ManyToOne
    private Tour tour;
    @SameLine
    private TourDuration tourDuration;
    @SameLine
    @ManyToOne
    private TourPriceZone tourPriceZone;

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
        e.setAttribute("pricePerUnit", "" + pricePerUnit);
        e.setAttribute("pricePerAdult", "" + pricePerAdult);
        e.setAttribute("pricePerChild", "" + pricePerChild);
        e.setAttribute("pricePerUnitAndDay", "" + pricePerUnitAndDay);
        e.setAttribute("pricePerAdultAndDay", "" + pricePerAdultAndDay);
        e.setAttribute("pricePerChildAndDay", "" + pricePerChildAndDay);


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
        if (pricePerUnit != 0) {
            if (!"".equals(s)) s += ", ";
            s += pricePerUnit + " per unit";
        }
        if (pricePerAdult != 0) {
            if (!"".equals(s)) s += ", ";
            s += pricePerAdult + " per adult";
        }
        if (pricePerChild != 0) {
            if (!"".equals(s)) s += ", ";
            s += pricePerChild + " per child";
        }
        if (pricePerUnitAndDay != 0) {
            if (!"".equals(s)) s += ", ";
            s += pricePerUnitAndDay + " per unit and day";
        }
        if (pricePerAdultAndDay != 0) {
            if (!"".equals(s)) s += ", ";
            s += pricePerAdultAndDay + " per adult and day";
        }
        if (pricePerChildAndDay != 0) {
            if (!"".equals(s)) s += ", ";
            s += pricePerChildAndDay + " per child and day";
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
