package io.mateu.erp.model.invoicing;


import com.vaadin.icons.VaadinIcons;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Indelible;
import io.mateu.mdd.core.annotations.KPI;
import io.mateu.mdd.core.annotations.NewNotAllowed;
import io.mateu.mdd.core.app.ActionType;
import io.mateu.mdd.core.app.MDDLink;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.interfaces.WizardPage;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.util.List;

@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class IssuedInvoice extends Invoice {

    @KPI
    private boolean sent;

    @KPI
    private boolean paid;



    public IssuedInvoice() {
        super();
    }


    public IssuedInvoice(User u, List<Charge> charges) throws Throwable {
        super(u, charges);
    }

    public IssuedInvoice(User u, List<Charge> charges, boolean proforma) throws Throwable {
        super(u, charges, proforma);
    }

    @Action
    public static WizardPage issue() {
        return new IssueInvoicesParametersPage();
    }


    @Action(icon = VaadinIcons.ENVELOPE)
    public void send(EntityManager em) {
        setSent(true);
        em.merge(this);
    }

}
