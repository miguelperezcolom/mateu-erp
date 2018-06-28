package io.mateu.erp.model.invoicing;

import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.annotations.Action;
import io.mateu.ui.mdd.server.annotations.Indelible;
import io.mateu.ui.mdd.server.annotations.NewNotAllowed;
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class IssuedInvoice extends Invoice {


    @Action(name = "Issue")
    public static MDDLink issue(IssueInvoicesWizard wizard) {
        return new MDDLink(Invoice.class, ActionType.OPENLIST, new Data());
    }

}
