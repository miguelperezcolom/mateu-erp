package io.mateu.erp.model.product.generic;

import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.product.tour.Tour;
import io.mateu.erp.model.product.tour.TourDuration;
import io.mateu.erp.model.product.tour.TourPriceZone;
import io.mateu.mdd.core.annotations.SameLine;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
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
public class Price {

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

        return e;
    }
}
