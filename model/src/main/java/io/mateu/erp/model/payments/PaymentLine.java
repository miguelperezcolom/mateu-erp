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

    public void setPayment(Payment payment) {
        this.payment = payment;
        pushUp();
    }

    @ManyToOne
    @NotNull
    private MethodOfPayment methodOfPayment;


    @ManyToOne
    @NotNull
    private Currency currency;

    public void setCurrency(Currency currency) {
        this.currency = currency;
        if (currencyExchange == 0 && currency != null) setCurrencyExchange(currency.getExchangeRateToNucs());
    }

    /*
    positivo es a nuestro favor (cobro)
    negativo es en nuestra contra (pago)
     */
    private double value;

    public void setValue(double value) {
        this.value = value;
        sumUp();
    }

    private double transactionCost;

    private String transactionId;


    @Output
    private double currencyExchange;

    public void setCurrencyExchange(double currencyExchange) {
        this.currencyExchange = currencyExchange;
        sumUp();
    }

    @Output
    private double currencyExchangeCost;

    @Output
    private double valueInNucs;

    public void setValueInNucs(double valueInNucs) {
        this.valueInNucs = valueInNucs;
        pushUp();
    }

    public void pushUp() {
        if (payment != null) {
            payment.updateBalance();
        }
    }

    public void sumUp() {
        setValueInNucs(Helper.roundEuros(getValue() * getCurrencyExchange()));
    }

}
