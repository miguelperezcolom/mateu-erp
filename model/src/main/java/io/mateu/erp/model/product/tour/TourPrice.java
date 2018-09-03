package io.mateu.erp.model.product.tour;

import com.vaadin.data.provider.DataProvider;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.product.hotel.Inventory;
import io.mateu.mdd.core.annotations.DependsOn;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter@Setter
public class TourPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Contract contract;

    @ManyToOne
    @NotNull
    private Tour tour;


    @ManyToOne
    @NotNull
    private TourVariant variant;

    @DependsOn("tour")
    public DataProvider getVariantDataProvider() throws Throwable {
        return new JPQLListDataProvider(
                "select x from " + Tour.class.getName() + " y inner join y.variants x " +
                        ((getTour() != null)?" where y.id = " + getTour().getId():""));
    }

    @ManyToOne
    private TourPriceZone zone;

    @ManyToOne
    private TourExtra extra;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    private LocalDate bookingWindowStart;

    private LocalDate bookingWindowEnd;

    @ManyToOne
    private BillingConcept billingConcept;

    private String description;

    private double pricePerAdult;

    private double pricePerChild;

    private double pricePerVehicle;

    @Column(name = "_order")
    private int order;

    private boolean finalPrice;

    private boolean active = true;

    public Element toXml() {
        Element e = new Element("price");

        if (tour != null) e.addContent(new Element("tour").setAttribute("id", "" + tour.getId()).setAttribute("name", tour.getName()));
        if (variant != null) e.addContent(new Element("variant").setAttribute("id", "" + variant.getId()).setAttribute("name", variant.getName().toString()));
        if (zone != null) e.addContent(new Element("zone").setAttribute("id", "" + zone.getId()).setAttribute("name", zone.getName()));
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

        if (finalPrice) e.setAttribute("finalPrice", "");

        e.setAttribute("pricePerVehicle", "" + pricePerVehicle);
        e.setAttribute("pricePerAdult", "" + pricePerAdult);
        e.setAttribute("pricePerChild", "" + pricePerChild);

        return e;
    }
}
