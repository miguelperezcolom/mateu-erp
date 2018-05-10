package io.mateu.erp.model.financials;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * Created by miguel on 1/10/16.
 */
@Embeddable
public class Amount {

    private double value;

    @ManyToOne
    private Currency currency;

    private double changeRate;

    private LocalDateTime date = LocalDateTime.now();


    private double officeValue;

    private double changeRate0;

    private double accountingValue;

    private double changeRate1;

    private double value1;

    private double changeRate2;

    private double value2;

    private double changeRate3;

    private double value3;

    private double changeRate4;

    private double value4;



}
