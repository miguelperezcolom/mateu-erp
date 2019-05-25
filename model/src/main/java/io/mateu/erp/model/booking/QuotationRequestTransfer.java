package io.mateu.erp.model.booking;

import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.mdd.core.annotations.DependsOn;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
public class QuotationRequestTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private QuotationRequest rq;

    private boolean active = true;

    @NotNull
    private LocalDateTime flightDate;

    private String flightNumber;

    private String flightOriginOrDestination;

    @NotNull
    private TransferType transferType;

    @ManyToOne@NotNull
    private TransferPoint origin;

    @ManyToOne@NotNull
    private TransferPoint destination;

    private int pax;

    public void setPax(int pax) {
        this.pax = pax;
        updateTotal();
    }

    private boolean saleOverrided;

    public void setSaleOverrided(boolean saleOverrided) {
        this.saleOverrided = saleOverrided;
        updateTotal();
    }

    private double pricePerTransfer;

    public void setPricePerTransfer(double pricePerTransfer) {
        this.pricePerTransfer = pricePerTransfer;
        updateTotal();
    }

    @DependsOn("saleOverrided")
    public boolean isPricePerTransferVisible() {
        return saleOverrided;
    }

    private double pricePerPax;

    public void setPricePerPax(double pricePerPax) {
        this.pricePerPax = pricePerPax;
        updateTotal();
    }

    @DependsOn("saleOverrided")
    public boolean isPricePerPaxVisible() {
        return saleOverrided;
    }

    @Output
    private double totalSale;


    private boolean costOverrided;

    public void setCostOverrided(boolean costOverrided) {
        this.costOverrided = costOverrided;
        updateTotal();
    }

    @ManyToOne
    private Provider provider;

    @DependsOn("costOverrided")
    public boolean isProviderVisible() {
        return costOverrided;
    }

    private double costPerTransfer;

    public void setCostPerTransfer(double costPerTransfer) {
        this.costPerTransfer = costPerTransfer;
        updateTotal();
    }

    @DependsOn("costOverrided")
    public boolean isCostPerTransferVisible() {
        return costOverrided;
    }


    private double costPerPax;

    public void setCostPerPax(double costPerPax) {
        this.costPerPax = costPerPax;
        updateTotal();
    }

    @DependsOn("costOverrided")
    public boolean isCostPerPaxVisible() {
        return costOverrided;
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
        if (isSaleOverrided()) setTotalSale(Helper.roundEuros(pricePerTransfer + pax * pricePerPax));
        if (isCostOverrided()) setTotalCost(Helper.roundEuros(costPerTransfer + pax * costPerPax));
    }





    public String toHtml() {
        String h = "<table style='border-spacing: 0px; border-top: 1px dashed grey; border-bottom: 1px dashed grey;'>";
        h += "<tr><th width='150px'>Date:</th><td> " + flightDate + "</td></tr>";
        h += "<tr><th>Flight:</th><td> " + flightNumber + " to/from " + flightOriginOrDestination + "</td></tr>";
        if (transferType != null) h += "<tr><th>Transfer type:</th><td>" + transferType.name() + "</td></tr>";
        if (origin != null) h += "<tr><th>Origin:</th><td>" + origin.getName() + "</td></tr>";
        if (destination != null) h += "<tr><th>Destination:</th><td>" + destination.getName() + "</td></tr>";
        h += "<tr><th>Pax:</th><td>" + pax + "</td></tr>";
        h += "<tr><th>Total sale:</th><td>" + totalSale + "</td></tr>";
        h += "<tr><th>Total cost:</th><td>" + totalCost + "</td></tr>";
        h += "<tr><th>Total markup:</th><td>" + Helper.roundEuros(totalSale - totalCost) + "</td></tr>";
        h += "</table>";
        return h;
    }

    public String toSimpleString() {
        String s = "Transfer line " + getId() + "";
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

        el.setAttribute("origin", origin.getName());
        el.setAttribute("destination", destination.getName());
        el.setAttribute("transferType", transferType.name());

        if (flightNumber != null) el.setAttribute("flightNumber", flightNumber);
        if (flightOriginOrDestination != null) el.setAttribute("flightOriginOrDestination", flightOriginOrDestination);

        el.setAttribute("flightDate", flightDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        el.setAttribute("pax", "" + pax);

        el.setAttribute("total", nf.format(totalSale));

        return el;
    }

    public QuotationRequestTransfer createDuplicate(QuotationRequest rq) {
        QuotationRequestTransfer c = new QuotationRequestTransfer();
        c.setRq(rq);
        c.setActive(active);
        c.setCostOverrided(costOverrided);
        c.setCostPerPax(costPerPax);
        c.setCostPerTransfer(costPerTransfer);
        c.setDestination(destination);
        c.setFlightDate(flightDate);
        c.setFlightNumber(flightNumber);
        c.setFlightOriginOrDestination(flightOriginOrDestination);
        c.setOrigin(origin);
        c.setPax(pax);
        c.setPricePerPax(pricePerPax);
        c.setPricePerTransfer(pricePerTransfer);
        c.setProvider(provider);
        c.setSaleOverrided(saleOverrided);
        c.setTransferType(transferType);
        return c;
    }
}
