package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.invoicing.Invoice;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Account origin;

    @ManyToOne
    private Account destination;

    @ManyToOne
    private FinancialAgent agent;

    @OneToMany(mappedBy = "payment")
    private List<AbstractPaymentAllocation> breakdown = new ArrayList<>();

    @ManyToOne
    private Currency currency;

    private double value;

    @ManyToOne
    private Payment cost;

}
