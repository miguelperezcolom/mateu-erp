package io.mateu.erp.model.booking;


import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.product.Tariff;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.ExcursionShift;
import io.mateu.mdd.core.annotations.DependsOn;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Entity@Getter@Setter
public class QuotationRequestExcursion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private QuotationRequest rq;

    private boolean active = true;

    @NotNull
    @ManyToOne
    private Excursion excursion;

    @NotNull
    @ManyToOne
    private Variant variant;

    @NotNull
    @ManyToOne
    private Tariff tariff;


    public DataProvider getVariantDataProvider() {
        return new ListDataProvider(excursion != null?excursion.getVariants():new ArrayList());
    }


    @NotNull
    @ManyToOne
    private ExcursionShift shift;

    public DataProvider getShiftDataProvider() {
        return new ListDataProvider(excursion != null?excursion.getShifts():new ArrayList());
    }


    @NotNull
    private LocalDate date;

    private int adults;

    public void setAdults(int adults) {
        this.adults = adults;
        updateTotal();
    }

    private int children;

    public void setChildren(int children) {
        this.children = children;
        updateTotal();
    }

    private boolean saleOverrided;

    public void setSaleOverrided(boolean saleOverrided) {
        this.saleOverrided = saleOverrided;
        updateTotal();
    }

    private double pricePerExcursion;

    public void setPricePerExcursion(double pricePerExcursion) {
        this.pricePerExcursion = pricePerExcursion;
        updateTotal();
    }

    @DependsOn("saleOverrided")
    public boolean isPricePerExcursionVisible() {
        return saleOverrided;
    }

    private double pricePerAdult;

    public void setPricePerAdult(double pricePerAdult) {
        this.pricePerAdult = pricePerAdult;
        updateTotal();
    }

    @DependsOn("saleOverrided")
    public boolean isPricePerAdultVisible() {
        return saleOverrided;
    }

    private double pricePerChild;

    public void setPricePerChild(double pricePerChild) {
        this.pricePerChild = pricePerChild;
        updateTotal();
    }

    @DependsOn("saleOverrided")
    public boolean isPricePerChildVisible() {
        return saleOverrided;
    }

    @Output
    private double totalSale;


    private boolean costOverrided;

    public void setCostOverrided(boolean costOverrided) {
        this.costOverrided = costOverrided;
        updateTotal();
    }

    private double costPerExcursion;

    public void setCostPerExcursion(double costPerExcursion) {
        this.costPerExcursion = costPerExcursion;
        updateTotal();
    }

    @DependsOn("costOverrided")
    public boolean isCostPerExcursionVisible() {
        return saleOverrided;
    }


    private double costPerAdult;

    public void setCostPerAdult(double costPerAdult) {
        this.costPerAdult = costPerAdult;
        updateTotal();
    }

    @DependsOn("costOverrided")
    public boolean isCostPerAdultVisible() {
        return saleOverrided;
    }

    private double costPerChild;

    public void setCostPerChild(double costPerChild) {
        this.costPerChild = costPerChild;
        updateTotal();
    }

    @DependsOn("costOverrided")
    public boolean isCostPerChildVisible() {
        return saleOverrided;
    }


    @Output
    private double totalCost;


    public void setTotalSale(double totalSale) {
        this.totalSale = totalSale;
        if (rq != null) {
            rq.updateTotal();
        }
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
        if (rq != null) {
            rq.updateTotal();
        }
    }


    private void updateTotal() {
        if (isSaleOverrided()) setTotalSale(Helper.roundEuros(pricePerExcursion + adults * pricePerAdult + children * pricePerChild));
        if (isCostOverrided()) setTotalCost(Helper.roundEuros(costPerExcursion + adults * costPerAdult + children * costPerChild));
    }


    public String toHtml() {
        String h = "<table style='border-spacing: 0px; border-top: 1px dashed grey; border-bottom: 1px dashed grey;'>";
        h += "<tr><th width='150px'>Date:</th><td>" + date + "</td></tr>";
        if (excursion != null) h += "<tr><th>Excursion:</th><td>" + excursion.getName() + "</td></tr>";
        if (variant != null) h += "<tr><th>Variant:</th><td>" + variant.getName() + "</td></tr>";
        h += "<tr><th>Adults:</th><td>" + adults + "</td></tr>";
        h += "<tr><th>Children:</th><td>" + children + "</td></tr>";
        h += "<tr><th>Total sale:</th><td>" + totalSale + "</td></tr>";
        h += "<tr><th>Total cost:</th><td>" + totalCost + "</td></tr>";
        h += "<tr><th>Total markup:</th><td>" + Helper.roundEuros(totalSale - totalCost) + "</td></tr>";
        h += "</table>";
        return h;
    }

    public String toSimpleString() {
        String s = "Excursion line " + getId() + "";
        return s;
    }

    @Override
    public String toString() {
        return id > 0?"Line " + id:"New line";
    }

    @PrePersist@PreUpdate
    public void pre() {
        if (getRq().isAlreadyConfirmed()) throw new Error("This quotation request has already been related to a File. It can not be modified");
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (id != 0 && obj != null && obj instanceof QuotationRequestLine && id == ((QuotationRequestLine) obj).getId());
    }

    public Element toXml() {
        DecimalFormat pf = new DecimalFormat("#####0.00");
        DecimalFormat nf = new DecimalFormat("##,###,###,###,##0.00");

        Element el = new Element("line");

        if (active) el.setAttribute("active", "");

        el.setAttribute("excursion", excursion.getName());
        el.setAttribute("variant", variant.getName().getEs());
        el.setAttribute("shift", shift.getName());

        if (date != null) el.setAttribute("date", date.format(DateTimeFormatter.ISO_DATE));

        el.setAttribute("adults", "" + adults);
        el.setAttribute("children", "" + children);

        el.setAttribute("total", nf.format(totalSale));

        return el;
    }

    public QuotationRequestExcursion createDuplicate(QuotationRequest rq) {
        QuotationRequestExcursion c = new QuotationRequestExcursion();
        c.setRq(rq);
        c.setActive(active);
        c.setAdults(adults);
        c.setChildren(children);
        c.setCostOverrided(costOverrided);
        c.setCostPerAdult(costPerAdult);
        c.setCostPerChild(costPerChild);
        c.setCostPerExcursion(costPerExcursion);
        c.setDate(date);
        c.setExcursion(excursion);
        c.setPricePerAdult(pricePerAdult);
        c.setPricePerChild(pricePerChild);
        c.setPricePerExcursion(pricePerExcursion);
        c.setSaleOverrided(saleOverrided);
        c.setShift(shift);
        c.setVariant(variant);
        return c;
    }
}
