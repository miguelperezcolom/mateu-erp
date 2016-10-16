package io.mateu.erp.model.financials;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * holder for currencies
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Table(name = "MA_CURRENCY")
@Getter@Setter
public class Currency {

    @Id
    @Column(name = "CURISOCODE")
    private String isoCode;

    @Column(name = "CURISO4217CODE")
    private String iso4217Code;

    @Column(name = "CURNAME")
    private String name;

    @Column(name = "CURDECIMALS")
    private int decimals;

}
