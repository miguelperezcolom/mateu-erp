package io.mateu.erp.model.booking;


import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.Room;
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

@Entity@Getter@Setter
public class QuotationRequestGeneric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private QuotationRequest rq;

    private boolean active = true;

    @NotNull
    @ManyToOne
    private GenericProduct product;

    @NotNull
    @ManyToOne
    private Variant variant;

    @Column(name = "_start")
    @NotNull
    private LocalDate start;

    @Column(name = "_end")
    @NotNull
    private LocalDate end;

    private int units;

    public void setUnits(int units) {
        this.units = units;
        updateTotal();
    }

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

    private double pricePerUnit;

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        updateTotal();
    }

    @DependsOn("saleOverrided")
    public boolean isPricePerUnitVisible() {
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

    private double costPerUnit;

    public void setCostPerUnit(double costPerUnit) {
        this.costPerUnit = costPerUnit;
        updateTotal();
    }

    @DependsOn("costOverrided")
    public boolean isCostPerUnitVisible() {
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
        if (isSaleOverrided()) setTotalSale(Helper.roundEuros(units * pricePerUnit + adults * pricePerAdult + children * pricePerChild));
        if (isCostOverrided()) setTotalCost(Helper.roundEuros(units * costPerUnit + adults * costPerAdult + children * costPerChild));
    }



    @Override
    public String toString() {
        String h = "<table style='border-spacing: 0px; border-top: 1px dashed grey; border-bottom: 1px dashed grey;'>";
        h += "<tr><th width='150px'>Dates:</th><td>From " + start + " to " + end + "</td></tr>";
        h += "<tr><th>Product:</th><td>" + product.getName() + "</td></tr>";
        h += "<tr><th>Variant:</th><td>" + variant.getName() + "</td></tr>";
        h += "<tr><th>Units:</th><td>" + units + "</td></tr>";
        h += "<tr><th>Adults:</th><td>" + adults + "</td></tr>";
        h += "<tr><th>Children:</th><td>" + children + "</td></tr>";
        h += "<tr><th>Total sale:</th><td>" + totalSale + "</td></tr>";
        h += "<tr><th>Total cost:</th><td>" + totalCost + "</td></tr>";
        h += "<tr><th>Total markup:</th><td>" + Helper.roundEuros(totalSale - totalCost) + "</td></tr>";
        h += "</table>";
        return h;
    }

    public String toSimpleString() {
        String s = "Generic line " + getId() + "";
        return s;
    }


    @PrePersist@PreUpdate
    public void pre() {
        if (getRq().isAlreadyConfirmed()) throw new Error("This quotation request has already been related to a File. It can not be modified");
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof QuotationRequestLine && id == ((QuotationRequestLine) obj).getId());
    }

    public Element toXml() {
        DecimalFormat pf = new DecimalFormat("#####0.00");
        DecimalFormat nf = new DecimalFormat("##,###,###,###,##0.00");

        Element el = new Element("line");

        if (active) el.setAttribute("active", "");

        el.setAttribute("product", product.getName());
        el.setAttribute("variant", variant.getName().getEs());

        if (start != null) el.setAttribute("start", start.format(DateTimeFormatter.ISO_DATE));
        if (end != null) el.setAttribute("end", end.format(DateTimeFormatter.ISO_DATE));

        el.setAttribute("units", "" + units);
        el.setAttribute("adults", "" + adults);
        el.setAttribute("children", "" + children);

        el.setAttribute("total", nf.format(totalSale));

        return el;
    }
}
