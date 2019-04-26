package io.mateu.erp.model.booking;


import com.google.common.base.Strings;
import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.mdd.core.annotations.NotInlineEditable;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.TextArea;
import io.mateu.mdd.core.annotations.UseLinkToListView;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotel")@NotInlineEditable
    private List<QuotationRequestHotelLine> lines = new ArrayList<>();

    public String getLinesHtml() {
        String h = "<div class='lines'>";
        for (QuotationRequestHotelLine l : lines) {
            h += "<div class='line" + (l.isActive() ? "" : " cancelled") + "'>";
            h += l.toHtml();
            h += "</div>";
        }
        h += "</div>";

        return h;
    }

    private double adultTaxPerNight;

    public void setAdultTaxPerNight(double adultTaxPerNight) {
        this.adultTaxPerNight = adultTaxPerNight;
        updateTotal();
    }

    public void setChildTaxPerNight(double childTaxPerNight) {
        this.childTaxPerNight = childTaxPerNight;
        updateTotal();
    }

    private double childTaxPerNight;

    private HotelMeal firstService;

    private HotelMeal lastService;

    @TextArea
    private String specialRequests;

    @Output
    private double totalSale;

    @Output
    private double totalCost;

    @Output
    private double totalTax;

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

        int totalPax = 0;
        int totalAds = 0;
        int totalChs = 0;
        int totalAdNights = 0;
        int totalChNights = 0;

        for (QuotationRequestHotelLine line : lines) if (line.isActive()) {
            v += line.getTotalSale();
            c += line.getTotalCost();

            int n = line.getStart() != null && line.getEnd() != null?(int) (DAYS.between(line.getStart(), line.getEnd()) -1):0;

            totalAds += line.getNumberOfRooms() * line.getAdultsPerRoom();
            totalChs += line.getNumberOfRooms() * line.getChildrenPerRoom();
            totalAdNights += line.getNumberOfRooms() * line.getAdultsPerRoom() * n;
            totalChNights += line.getNumberOfRooms() * line.getChildrenPerRoom() * n;
            totalPax = totalAds + totalChs;
        }

        double t = Helper.roundEuros(adultTaxPerNight * totalAdNights + childTaxPerNight * totalChNights);

        v += t;
        c += t;

        setTotalSale(Helper.roundEuros(v));
        setTotalCost(Helper.roundEuros(c));
        setTotalTax(t);
    }



    public String toHtml() {
        String h = "<table style='border-spacing: 0px; border-top: 1px dashed grey; border-bottom: 1px dashed grey;'>";
        if (hotel != null) h += "<tr><th>Hotel:</th><td>" + hotel.getName() + "</td></tr>";
        h += "<tr><th></th><td>Taxes: " + adultTaxPerNight + "/" + childTaxPerNight + " per night, " + (firstService != null?firstService.name():"..") + " - " + (lastService != null?lastService.name():"..") + "</td></tr>";
        if (!Strings.isNullOrEmpty(specialRequests)) h += "<tr><th></th><td>" + specialRequests + "</td></tr>";
        int totalPax = 0;
        int totalAds = 0;
        int totalChs = 0;
        int pos = 1;
        for (QuotationRequestHotelLine line : lines) {
            h += "<tr><th width='150px'>Line " + pos++ + ":</th><td>From " + line.getStart() + " to " + line.getEnd() + "</td></tr>";
            h += "<tr><th></th><td>" + line.getNumberOfRooms();
            if (line.getRoom() != null) h += " x " + line.getRoom().getName() + "";
            h +=  "</td></tr>";
            if (line.getBoard() != null) h += "<tr><th></th><td>" + line.getBoard().getName() + "</td></tr>";
            h += "<tr><th></th><td>" + line.getAdultsPerRoom() + " ad  + " + line.getChildrenPerRoom() + " ch per room</td></tr>";
            totalAds += line.getNumberOfRooms() * line.getAdultsPerRoom();
            totalChs += line.getNumberOfRooms() * line.getChildrenPerRoom();
            totalPax = totalAds + totalChs;
        }
        h += "<tr><th>Totals:</th><td>Pax: " + totalPax + " (" + totalAds + " ads + " + totalChs + " chs), Tax: " + totalTax + ", Sale: " + totalSale + ", Cost: " + totalCost + ", Markup: " + Helper.roundEuros(totalSale - totalCost) + "</td></tr>";
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

        if (firstService != null) el.setAttribute("firstService", firstService.name());
        if (lastService != null) el.setAttribute("lastService", lastService.name());

        if (adultTaxPerNight != 0) el.setAttribute("adultTaxPerNight", nf.format(adultTaxPerNight));
        if (childTaxPerNight != 0) el.setAttribute("childTaxPerNight", nf.format(childTaxPerNight));

        if (!Strings.isNullOrEmpty(specialRequests)) el.setAttribute("specialRequests", specialRequests);


        Element els;
        el.addContent(els = new Element("lines"));

        int totalPax = 0;
        int totalAds = 0;
        int totalChs = 0;
        int totalAdNights = 0;
        int totalChNights = 0;
        for (QuotationRequestHotelLine line : lines) if (line.isActive()) {
            els.addContent(line.toXml());

            int n = line.getStart() != null && line.getEnd() != null?(int) (DAYS.between(line.getStart(), line.getEnd()) -1):0;

            totalAds += line.getNumberOfRooms() * line.getAdultsPerRoom();
            totalChs += line.getNumberOfRooms() * line.getChildrenPerRoom();
            totalAdNights += line.getNumberOfRooms() * line.getAdultsPerRoom() * n;
            totalChNights += line.getNumberOfRooms() * line.getChildrenPerRoom() * n;
            totalPax = totalAds + totalChs;
        }

        el.setAttribute("totalAds", "" + totalAds);
        el.setAttribute("totalChs", "" + totalChs);
        el.setAttribute("totalAdNights", "" + totalAdNights);
        el.setAttribute("totalChNights", "" + totalChNights);
        el.setAttribute("totalAdTax", nf.format(Helper.roundEuros(adultTaxPerNight * totalAdNights)));
        el.setAttribute("totalChTax", nf.format(Helper.roundEuros(childTaxPerNight * totalChNights)));
        el.setAttribute("totalTax", nf.format(totalTax));

        el.setAttribute("total", nf.format(totalSale));

        return el;
    }

}
