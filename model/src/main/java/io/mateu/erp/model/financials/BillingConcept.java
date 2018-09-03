package io.mateu.erp.model.financials;

import io.mateu.mdd.core.annotations.QLForCombo;
import io.mateu.mdd.core.annotations.SameLine;
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
public class BillingConcept {

    @Id
    @NotNull
    private String code;

    @NotNull
    private String name;

    @NotNull
    private LocalizationRule localizationRule;


    private boolean hotel;

    @SameLine
    private boolean transfer;

    @SameLine
    private boolean generic;

    @SameLine
    private boolean commission;

    @SameLine
    private boolean handlingFee;


    @Override
    public String toString() {
        return getName();
    }
}
