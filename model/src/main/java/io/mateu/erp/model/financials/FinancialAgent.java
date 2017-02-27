package io.mateu.erp.model.financials;

import io.mateu.ui.mdd.server.annotations.UseIdToSelect;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * holder for a admin agent, tipically associated to a VAT ID (e.g. a customer, a supplier, ourselves)
 *
 * Sometimes the same admin agent will act as customer and as supplier. This is interesting to manage a balance of payments
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class FinancialAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Override
    public String toString() {
        return getName();
    }
}
