package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.taxes.VAT;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter@Setter
public class ChargeLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Invoice invoice;

    private String subject;

    private double quantity;

    private double price;

    private double discountPercent;

    private double total;

    @ManyToOne
    private BillingConcept billingConcept;

    @ManyToOne
    private VAT vat;

    private double base;

    private double taxes;

    @ManyToOne
    private Service service;

    @ManyToOne
    private Booking booking;

    @ManyToOne
    private Currency currency;
}
