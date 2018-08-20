package io.mateu.erp.model.invoicing;


import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Indelible;
import io.mateu.mdd.core.annotations.NewNotAllowed;
import io.mateu.mdd.core.app.ActionType;
import io.mateu.mdd.core.app.MDDLink;
import io.mateu.mdd.core.data.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class ReceivedInvoice extends Invoice {

    @Action("Enter invoices")
    public static MDDLink enter(EnterInvoicesWizard wizard) {
        return new MDDLink(Invoice.class, ActionType.OPENLIST, new Data());
    }

}
