package io.mateu.erp.model.invoicing;


import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.WizardPage;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class IssuedInvoice extends Invoice {

    @ManyToOne
    @NotNull
    @Output
    private InvoiceSerial serial;

    @KPI
    private boolean sent;

    @KPI
    private boolean paid;



    public IssuedInvoice() {
        super();
    }

    public IssuedInvoice(User u, Collection<Charge> charges, boolean proforma, FinancialAgent issuer, FinancialAgent recipient, String invoiceNumber) throws Throwable {
        super(u, charges, proforma, issuer, recipient, invoiceNumber);
    }


    @Override
    public String getXslfo(EntityManager em)  {
        return AppConfig.get(em).getXslfoForIssuedInvoice();
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
