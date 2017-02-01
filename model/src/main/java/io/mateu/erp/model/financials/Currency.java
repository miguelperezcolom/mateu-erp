package io.mateu.erp.model.financials;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * holder for currencies
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class Currency {

    @Id
    private String isoCode;

    private String iso4217Code;

    private String name;

    private int decimals;

}
