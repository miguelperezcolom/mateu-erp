package io.mateu.erp.model.biz;

import io.mateu.erp.model.financials.Currency;
import io.mateu.mdd.core.annotations.KPI;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Coupon {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @KPI
    private String code;


    private double percentDiscount;

    private double maxAmount;

    private double discount;

    @ManyToOne
    @NotNull
    private Currency currency;

    private double finalPrice;

    private int units;


    @KPI
    private int used;

    @KPI
    private int available;

    @KPI
    private double totalDiscountUsed;
}
