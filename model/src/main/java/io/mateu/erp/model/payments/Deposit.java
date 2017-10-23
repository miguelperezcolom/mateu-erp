package io.mateu.erp.model.payments;

import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.FinancialAgent;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Deposit extends Account {

    @ManyToOne
    private FinancialAgent agent;

}

