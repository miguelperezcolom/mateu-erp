package io.mateu.erp.model.financials;

import io.mateu.erp.model.config.AppConfig;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;

import javax.persistence.Embeddable;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Embeddable
@Getter@Setter
public class Amount {

    private transient List<AmountChangeListener> listeners = new ArrayList<>();

    private double value;

    @ManyToOne
    @NotNull
    private Currency currency;

    private LocalDateTime date = LocalDateTime.now();

    private double officeChangeRate;

    private double officeValue;

    private double nucChangeRate;

    private double nucValue;


    public Amount() {

    }

    public Amount(Amount source) throws Throwable {
        this(FastMoney.of(source.getValue(), source.getCurrency().getIsoCode()));
    }

    public Amount(EntityManager em, double value, Currency currency) throws Throwable {
        this.value = value;
        this.currency = currency;
        
        Currency nucc = AppConfig.get(em).getNucCurrency();

        if (!getCurrency().equals(nucc)) {
            boolean found = false;
            for (CurrencyExchange e : currency.getExchanges()) if (e.getTo().equals(nucc)) {
                found = true;
                nucChangeRate = e.getRate();
                nucValue = Math.round(100d * getValue() * nucChangeRate) / 100d;
            }
            if (!found) throw new Exception("Sorry. No change from " + getCurrency().getName() + " to " + nucc.getName());
        } else {
            nucChangeRate  = 1;
            nucValue = getValue();
        }
    }

    public Amount(FastMoney value) throws Throwable {
        io.mateu.mdd.core.util.Helper.notransact(em -> {

            setCurrency(em.find(Currency.class, value.getCurrency().getCurrencyCode()));
            setValue(value.getNumber().doubleValue());

            Currency nucc = AppConfig.get(em).getNucCurrency();

            if (!getCurrency().equals(nucc)) {
                boolean found = false;
                for (CurrencyExchange e : currency.getExchanges()) if (e.getTo().equals(nucc)) {
                    found = true;
                    nucChangeRate = e.getRate();
                    nucValue = Math.round(100d * getValue() * nucChangeRate) / 100d;
                }
                if (!found) throw new Exception("Sorry. No change from " + getCurrency().getName() + " to " + nucc.getName());
            } else {
                nucChangeRate  = 1;
                nucValue = getValue();
            }

        });
    }


    @Override
    public String toString() {
        return (currency != null?currency.getIsoCode():"No currency") + " " + value;
    }

    public void setValue(double value) {
        this.value = value;
        if (this.value != value) for (AmountChangeListener l : listeners) l.changed();
    }



}
