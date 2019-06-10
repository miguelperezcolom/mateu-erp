package io.mateu.erp.model.biz;

import io.mateu.erp.model.financials.Currency;
import io.mateu.mdd.core.annotations.KPI;
import io.mateu.mdd.core.annotations.SameLine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Coupon {

    @Id
    private String code;

    public void setCode(String code) {
        this.code = code != null?code.toUpperCase().trim():null;
    }

    private String name;

    private LocalDate bookingWindowStart;

    @SameLine
    private LocalDate bookingWindowEnd;


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
