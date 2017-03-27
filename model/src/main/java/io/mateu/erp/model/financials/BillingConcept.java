package io.mateu.erp.model.financials;

import io.mateu.ui.mdd.server.annotations.QLForCombo;
import io.mateu.ui.mdd.server.annotations.Required;
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
@QLForCombo(ql = "select x.code, x.name from BillingConcept x order by x.name")
public class BillingConcept {

    @Id
    @Required
    private String code;

    @Required
    private String name;

    @Required
    private LocalizationRule localizationRule;


}
