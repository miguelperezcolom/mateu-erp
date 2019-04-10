package io.mateu.erp.model.invoicing;


import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.payments.InvoicePaymentAllocation;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.WizardPage;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class IssuedInvoice extends Invoice {


    @ManyToOne
    @NotNull
    @Output
    private Agency agency;

    @ManyToOne
    @NotNull
    @Output
    private InvoiceSerial serial;

    @KPI
    private boolean sent;


    public IssuedInvoice() {
        super();
    }

    public IssuedInvoice(User u, Collection<BookingCharge> charges, boolean proforma, FinancialAgent issuer, FinancialAgent recipient, String invoiceNumber) throws Throwable {
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


    @Action(icon = VaadinIcons.ENVELOPE)
    public static void sendSelection(EntityManager em, Set<IssuedInvoice> selection) {
        selection.forEach(i -> {
            i.setSent(true);
            em.merge(i);
        });
    }


    @Action(icon = VaadinIcons.ENVELOPE)
    public static SelfBillForm selfBill(Set<IssuedInvoice> selection) {
        return new SelfBillForm(selection);
    }


    @PostPersist
    public void post() {
        WorkflowEngine.add(() -> {

            try {

                Helper.transact(em -> {

                    IssuedInvoice i = em.find(IssuedInvoice.class, getId());

                    // mover pagos a la factura
                    List<Booking> bookings = new ArrayList<>();
                    i.getLines().forEach(l -> {
                        if (l instanceof BookingInvoiceLine) {
                            BookingInvoiceLine bil = (BookingInvoiceLine) l;
                            if (!bookings.contains(bil.getCharge().getBooking())) bookings.add(bil.getCharge().getBooking());
                        }
                    });
                    /*
                    bookings.forEach(b -> {
                        b.getPayments().forEach(a -> {
                            if (a.getInvoice() == null) {
                                a.setInvoice(i);
                                InvoicePaymentAllocation ipa;
                                i.getPayments().add(ipa = new InvoicePaymentAllocation());
                                ipa.setInvoice(i);
                                ipa.setPayment(a.getPayment());
                                ipa.setValue(a.getValue());
                                ipa.getPayment().getBreakdown().add(ipa);
                            }
                        });
                    });
                    */

                });

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        });
    }

}
