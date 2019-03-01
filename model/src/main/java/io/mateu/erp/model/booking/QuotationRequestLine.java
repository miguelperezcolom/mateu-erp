package io.mateu.erp.model.booking;

import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity@Getter@Setter
public class QuotationRequestLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne@NotNull
    private QuotationRequest rq;


    @TextArea
    private String text;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    private double units;

    private double price;

    @ManyToOne
    private Partner provider;

    @Ignored
    private double total;


    public void setUnits(double units) {
        this.units = units;
        updateTotal();
    }

    public void setPrice(double price) {
        this.price = price;
        updateTotal();
    }

    private void updateTotal() {
        setTotal(Helper.roundEuros(units * price));
    }

    public void setTotal(double total) {
        this.total = total;
        if (rq != null) {
            rq.updateTotal();
        }
    }
}
