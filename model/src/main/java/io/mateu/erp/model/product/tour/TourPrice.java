package io.mateu.erp.model.product.tour;

import com.vaadin.data.provider.DataProvider;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.product.Tariff;
import io.mateu.erp.model.product.Variant;
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

@Entity
@Getter@Setter
public class TourPrice implements Comparable<TourPrice> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    @Keep
    private Contract contract;

    @ManyToOne
    @NotNull
    @Keep
    private Tour tour;


    @ManyToOne
    @NotNull
    @Keep
    private Variant variant;

    @DependsOn("tour")
    public DataProvider getVariantDataProvider() throws Throwable {
        return new JPQLListDataProvider(
                "select x from " + Tour.class.getName() + " y inner join y.variants x " +
                        ((getTour() != null)?" where y.id = " + getTour().getId():""));
    }

    @ManyToOne
    private ExcursionPriceZone pickupZone;

    @ManyToOne
    private TourExtra extra;

    @ManyToOne
    private BillingConcept billingConcept;

    private String description;

    @ManyToOne@NotNull
    private Tariff tariff;


    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")@SameLine
    private LocalDate end;

    private LocalDate bookingWindowStart;

    @SameLine
    private LocalDate bookingWindowEnd;


    private int minPax;


    private double infantPrice;

    @SameLine
    private double childPrice;

    @SameLine
    private double juniorPrice;

    @SameLine
    private double adultPrice;

    @SameLine
    private double seniorPrice;


    @Column(name = "_order")
    private int order;

    private boolean finalPrice;

    private boolean active = true;

    public Element toXml() {
        Element e = new Element("price");

        if (tour != null) e.addContent(new Element("tour").setAttribute("id", "" + tour.getId()).setAttribute("name", tour.getName()));
        if (variant != null) e.addContent(new Element("variant").setAttribute("id", "" + variant.getId()).setAttribute("name", variant.getName().toString()));
        if (pickupZone != null) e.addContent(new Element("resort").setAttribute("id", "" + pickupZone.getId()).setAttribute("name", pickupZone.getName()));
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

        if (tariff != null) e.setAttribute("tariff", "" + tariff);

        e.setAttribute("minPax", "" + minPax);


        e.setAttribute("infantPrice", "" + infantPrice);
        e.setAttribute("childPrice", "" + childPrice);
        e.setAttribute("juniorPrice", "" + juniorPrice);
        e.setAttribute("adultPrice", "" + adultPrice);
        e.setAttribute("seniorPrice", "" + seniorPrice);

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

        if (minPax != 0) {
            if (!"".equals(s)) s += ", ";
            s += "for groups with pax >= " + minPax + "";
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


        if (tour != null) {
            if (!"".equals(s)) s += ", ";
            s += "for " + tour.getName() + " tour";
        }
        if (variant != null) {
            if (!"".equals(s)) s += ", ";
            s += "for " + variant.getName().toString() + " variant";
        }
        if (pickupZone != null) {
            if (!"".equals(s)) s += ", ";
            s += "for tours from " + pickupZone.getName();
        }
        return s;
    }

    @Override
    public int compareTo(@org.jetbrains.annotations.NotNull TourPrice o) {
        int r = 0;
        if (o == null) return 1;
        r = getOrder() - o.getOrder();
        if (r == 0) {
            r = ((getTour() != null)?getTour().getName():"").compareTo(((o.getTour() != null)?o.getTour().getName():""));
        }
        if (r == 0) {
            r = ((getVariant() != null)?getVariant().getName().toString():"").compareTo(((o.getVariant() != null)?o.getVariant().getName().toString():""));
        }
        if (r == 0) {
            r = ((getExtra() != null)?getExtra().getName().toString():"").compareTo(((o.getExtra() != null)?o.getExtra().getName().toString():""));
        }
        return r;
    }

}
