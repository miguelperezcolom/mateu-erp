package io.mateu.erp.model.payments;

import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.partners.Partner;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter@Setter
public class DueDate {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private FinancialAgent agent;

    @ManyToOne
    private Partner partner;

    @NotNull
    private DueDateType type;

    @NotNull
    private LocalDate date;

    @ManyToOne
    @NotNull
    private Currency currency;


    private double amount;

    private boolean paid;

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof DueDate && id == ((DueDate) obj).getId());
    }

    @Override
    public String toString() {
        return "" + type.name() + " " + date + " " + currency.getIsoCode() + " " + amount + " " + (paid?"Paid":"Pending");
    }
}
