package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.ui.mdd.server.annotations.Output;
import io.mateu.ui.mdd.server.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Charge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private ChargeType type;

    @NotNull
    private Currency currency;

    @NotNull
    @ManyToOne
    private BillingConcept billingConcept;

    @TextArea
    private String text;

    private double base;

    @ManyToOne
    private VAT vat;

    private double vatPercent;

    @Output
    private double beforeTaxes;

    private double total;

    @ManyToOne
    private Booking booking;

    @ManyToOne
    private Service service;

    @ManyToOne
    private HotelContract hotelContract;

    @ManyToOne
    private PurchaseOrder purchaseOrder;


    @ManyToOne
    private Invoice invoice;


    @PrePersist@PreUpdate
    public void validate() throws Exception {
        if (getBooking() == null && getPurchaseOrder() == null) throw  new Exception("It must be related to a booking or to a purchase order");
    }


}
