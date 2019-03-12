package io.mateu.erp.model.booking;

import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.partners.Provider;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity@Getter@Setter
public class QuotationRequestLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne@NotNull
    private QuotationRequest rq;

    private boolean active = true;

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

    @Ignored
    private double total;

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
        setTotal(Helper.roundEuros(units * price));
        setTotalCost(Helper.roundEuros(units * cost));
    }

    public void setTotal(double total) {
        this.total = total;
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
        h += "<tr><th>Total sale:</th><td>" + total + "</td></tr>";
        h += "<tr><th>Total cost:</th><td>" + Helper.roundEuros(units * cost) + "</td></tr>";
        h += "<tr><th>Total balance:</th><td>" + Helper.roundEuros(units * (price - cost)) + "</td></tr>";
        h += "</table>";
        return h;
    }

    public String toSimpleString() {
        String s = text;
        return s;
    }
}
