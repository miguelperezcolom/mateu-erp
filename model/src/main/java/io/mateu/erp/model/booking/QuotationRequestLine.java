package io.mateu.erp.model.booking;

import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity@Getter@Setter
public class QuotationRequestLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne@NotNull
    private QuotationRequest rq;

    private boolean active = true;

    @ManyToOne@NotNull
    private Office office;

    @TextArea
    private String text;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    private double units;

    private double price;

    @ManyToOne
    private Provider provider;

    private double cost;

    @ManyToOne@NotNull
    private BillingConcept billingConcept;

    @ManyToOne@NotNull
    private ProductLine productLine;

    @Ignored
    private double totalSale;

    @Ignored
    private double totalCost;


    public void setUnits(double units) {
        this.units = units;
        updateTotal();
    }

    public void setPrice(double price) {
        this.price = price;
        updateTotal();
    }

    public void setCost(double cost) {
        this.cost = cost;
        updateTotal();
    }

    private void updateTotal() {
        setTotalSale(Helper.roundEuros(units * price));
        setTotalCost(Helper.roundEuros(units * cost));
    }

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


    @Override
    public String toString() {
        String h = "<table style='border-spacing: 0px; border-top: 1px dashed grey; border-bottom: 1px dashed grey;'>";
        h += "<tr><th width='150px'>Dates:</th><td>From " + start + " to " + end + "</td></tr>";
        h += "<tr><th>Nr of units:</th><td>" + units + "</td></tr>";
        h += "<tr><th>Text:</th><td>" + text + "</td></tr>";
        h += "<tr><th>Price:</th><td>" + price + "</td></tr>";
        h += "<tr><th>Provider:</th><td>" + (provider != null?provider.getName():"---") + "</td></tr>";
        h += "<tr><th>Cost:</th><td>" + cost + "</td></tr>";
        h += "<tr><th>Total sale:</th><td>" + totalSale + "</td></tr>";
        h += "<tr><th>Total cost:</th><td>" + totalCost + "</td></tr>";
        h += "<tr><th>Total balance:</th><td>" + Helper.roundEuros(totalSale - totalCost) + "</td></tr>";
        h += "</table>";
        return h;
    }

    public String toSimpleString() {
        String s = text;
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

        if (text != null) el.setAttribute("text", text);

        if (start != null) el.setAttribute("start", start.format(DateTimeFormatter.ISO_DATE));
        if (end != null) el.setAttribute("end", end.format(DateTimeFormatter.ISO_DATE));
        el.setAttribute("units", "" + units);
        el.setAttribute("price", "" + price);
        el.setAttribute("total", nf.format(totalSale));

        return el;
    }
}
