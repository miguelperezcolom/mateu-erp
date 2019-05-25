package io.mateu.erp.model.invoicing;

import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.organization.Office;
import io.mateu.mdd.core.annotations.KPI;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.TextArea;
import io.mateu.mdd.core.model.authentication.Audit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.time.LocalDate;

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
    @Output
    private ChargeType type;



    @ManyToOne
    private Office office;

    @NotNull
    @ManyToOne
    private BillingConcept billingConcept;

    private LocalDate serviceDate;

    @TextArea
    private String text;


    private double total;

    public void setTotal(double total) {
        this.total = total;
        totalChanged();
    }

    @ManyToOne@NotNull
    private Currency currency;

    public void setCurrency(Currency currency) {
        this.currency = currency;
        if (currency != null) setCurrencyExchange(currency.getExchangeRateToNucs());
    }

    @KPI
    private double currencyExchange;

    public void setCurrencyExchange(double currencyExchange) {
        this.currencyExchange = currencyExchange;
        totalChanged();
    }

    @KPI
    private double valueInNucs;

    public void totalChanged() {
        setValueInNucs(Helper.roundEuros(total * currencyExchange));
    }


    @ManyToOne
    @Output
    private Invoice invoice;


    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("##,###,###,###,###.00");
        return "<div style='text-align:right;width:100px;display:inline-block;margin-right:10px;'>" + df.format(total) + "</div><div style='display: inline-block;'>" + ((text != null)?text:"---") + "</div>";
    }


    public boolean isModifiable() {
        return invoice == null;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (id > 0 && obj != null && obj instanceof Charge && id == ((Charge) obj).getId());
    }

    public String toChangeControlString() {
        return "" + total + " " + billingConcept.getName() + ": " + text;
    }
}
