package io.mateu.erp.model.financials;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * Created by miguel on 1/10/16.
 */
@Embeddable
public class Amount {

    @Basic
    private long value;

    @Basic
    private int decimals;

    @Basic
    private long nucs;

    @ManyToOne
    private Currency currency;

    @Basic
    private double change;

    @Temporal(TIMESTAMP)
    private Date date;

    /*


    @Embedded
    @AttributeOverrides({
      @AttributeOverride(name="level", column=@Column(name="EMPLOYMENT_LEVEL"))
      @AttributeOverride(name="status", column=@Column(name="EMPLOYMENT_STATUS"))})

     */

}
