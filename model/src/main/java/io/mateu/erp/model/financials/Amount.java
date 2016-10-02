package io.mateu.erp.model.financials;

import javax.persistence.Embeddable;
import java.util.Date;

/**
 * Created by miguel on 1/10/16.
 */
@Embeddable
public class Amount {

    private long value;

    private int decimals;

    private long nucs;

    private Currency currency;

    private double change;

    private Date date;

}
