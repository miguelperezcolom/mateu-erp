package io.mateu.erp.model.invoicing;


import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Indelible;
import io.mateu.mdd.core.annotations.NewNotAllowed;
import io.mateu.mdd.core.app.ActionType;
import io.mateu.mdd.core.app.MDDLink;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.interfaces.WizardPage;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class IssuedInvoice extends Invoice {


    @Action
    public static WizardPage issue() {
        return new IssueInvoicesParametersPage();
    }

}
