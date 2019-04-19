package io.mateu.erp.model.booking;


import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Entity@Getter@Setter
public class QuotationRequestHotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private QuotationRequest rq;

    private boolean active = true;

    @NotNull
    @ManyToOne
    private Hotel hotel;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotel")
    private List<QuotationRequestHotelLine> lines = new ArrayList<>();

    @Output
    private double totalSale;


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


    public void updateTotal() {
        double v = 0;
        double c = 0;
        for (QuotationRequestHotelLine line : lines) {
            v += line.getTotalSale();
            c += line.getTotalCost();
        }

        setTotalSale(Helper.roundEuros(v));
        setTotalCost(Helper.roundEuros(c));
    }



    public String toHtml() {
        String h = "<table style='border-spacing: 0px; border-top: 1px dashed grey; border-bottom: 1px dashed grey;'>";
        if (hotel != null) h += "<tr><th>Hotel:</th><td>" + hotel.getName() + "</td></tr>";
        for (QuotationRequestHotelLine line : lines) {
            h += "<tr><th width='150px'>Dates:</th><td>From " + line.getStart() + " to " + line.getEnd() + "</td></tr>";
            h += "<tr><th>Nr of rooms:</th><td>" + line.getNumberOfRooms() + "</td></tr>";
            if (line.getRoom() != null) h += "<tr><th>Room:</th><td>" + line.getRoom().getName() + "</td></tr>";
            if (line.getBoard() != null) h += "<tr><th>Board:</th><td>" + line.getBoard().getName() + "</td></tr>";
            h += "<tr><th>Adults per room:</th><td>" + line.getAdultsPerRoom() + "</td></tr>";
            h += "<tr><th>Children per room:</th><td>" + line.getChildrenPerRoom() + "</td></tr>";
        }
        h += "<tr><th>Total sale:</th><td>" + totalSale + "</td></tr>";
        h += "<tr><th>Total cost:</th><td>" + totalCost + "</td></tr>";
        h += "<tr><th>Total markup:</th><td>" + Helper.roundEuros(totalSale - totalCost) + "</td></tr>";
        h += "</table>";
        return h;
    }

    public String toSimpleString() {
        String s = "Hotel line " + getId() + "";
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

        el.setAttribute("hotel", hotel.getName() != null?hotel.getName():"--");

        Element els;
        el.addContent(els = new Element("lines"));

        for (QuotationRequestHotelLine line : lines) {
            els.addContent(line.toXml());
        }

        el.setAttribute("total", nf.format(totalSale));

        return el;
    }

}
