package io.mateu.erp.model.payments;

import io.mateu.erp.model.financials.Currency;
import io.mateu.mdd.core.annotations.NotWhenEditing;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class PaymentLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ManyToOne@NotNull
    private Payment payment;

    @ManyToOne
    @NotNull
    private MethodOfPayment methodOfPayment;


    @ManyToOne
    @NotNull
    private Currency currency;


    /*
    positivo es a nuestro favor (cobro)
    negativo es en nuestra contra (pago)
     */
    private double value;

    private double transactionCost;

    private String transactionId;


    @Output
    private double currencyExchange;

    @Output
    private double currencyExchangeCost;

    @Output
    private double valueInNucs;


    @PreUpdate@PrePersist
    public void pre() throws Throwable {
        if (currencyExchange == 0) setCurrencyExchange(getCurrency().getExchangeRateToNucs());
        setValueInNucs(Helper.roundEuros(getValue() * getCurrencyExchange()));
    }


    @PostPersist@PostUpdate@PostRemove
    public void post() {
        WorkflowEngine.add(() -> {
            try {
                Helper.transact(em -> {
                    PaymentLine p = em.find(PaymentLine.class, getId());
                    p.getPayment().setMarkedForUpdate(true);
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

}
