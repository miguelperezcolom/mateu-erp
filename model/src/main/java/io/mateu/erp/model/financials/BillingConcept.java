package io.mateu.erp.model.financials;

import io.mateu.ui.mdd.server.annotations.QLForCombo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

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
    @NotNull
    private String code;

    @NotNull
    private String name;

    @NotNull
    private LocalizationRule localizationRule;


}
