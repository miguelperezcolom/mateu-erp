package io.mateu.erp.model.financials;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * Created by miguel on 1/10/16.
 */
@Embeddable
public class Amount {

    private long value;

    private int decimals;

    private long nucs;

    @ManyToOne
    private Currency currency;

    private double change;

    @Temporal(TIMESTAMP)
    private Date date;

    /*


    @Embedded
    @AttributeOverrides({
      @AttributeOverride(name="level", column=@ListColumn(name="EMPLOYMENT_LEVEL"))
      @AttributeOverride(name="status", column=@ListColumn(name="EMPLOYMENT_STATUS"))})

     */

}
