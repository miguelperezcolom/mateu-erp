package io.mateu.erp.model.invoicing;

import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.TextArea;
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

    @Embedded
    @Output
    private Audit audit;

    @NotNull
    private ChargeType type;

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


    @ManyToOne
    @NotNull
    private Office office;

    @NotNull
    @ManyToOne
    private BillingConcept billingConcept;

    @TextArea
    private String text;

    @NotNull
    @ManyToOne
    private Currency currency;

    private double base;

    @ManyToOne
    private VAT vat;

    private double vatPercent;

    @Output
    private double beforeTaxes;

    private double total;

    @NotNull
    @ManyToOne
    private Currency officeCurrency;

    private double totalInOfficeCurrency;

    private double officeCurrencyExchangeRate;

    @NotNull
    @ManyToOne
    private Currency accountingCurrency;

    private double totalInAccountingCurrency;

    private double accountingCurrencyExchangeRate;




    @PrePersist@PreUpdate
    public void validate() throws Exception {
        if (getBooking() == null && getPurchaseOrder() == null) throw  new Exception("It must be related to a booking or to a purchase order");
    }


}
