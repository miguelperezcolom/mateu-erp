package io.mateu.erp.model.invoicing.lists;

import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.invoicing.Invoice;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.util.EmailHelper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class InvoiceResult {
    @Output
    private String msg;
    @Ignored
    private List<IssuedInvoice> invoices = new ArrayList<>();

    @IFrame
    @FullWidth
    private URL pdf;

    @Action(order = 1, icon = VaadinIcons.ENVELOPE)
    public void sendProformas(EntityManager em, String email, @TextArea String postscript) {
        try {

            if (!Strings.isNullOrEmpty(email)) {

                URL pdf = Invoice.createPdf(invoices);
                EmailHelper.sendEmail(email, "Proformas", postscript, false, pdf);

            } else {
                Map<FinancialAgent, List<Invoice>> invoicesByAgent = new HashMap<>();
                invoices.forEach(i -> {
                    List<Invoice> l = invoicesByAgent.get(i.getRecipient());
                    if (l == null) {
                        invoicesByAgent.put(i.getRecipient(), l = new ArrayList<>());
                    }
                    l.add(i);
                });

                for (FinancialAgent a : invoicesByAgent.keySet()) {
                    URL pdf = Invoice.createPdf(invoicesByAgent.get(a));
                    EmailHelper.sendEmail(a.getInvoicingEmail(), "Proformas", postscript, false, pdf);
                }
            }


        } catch (Throwable throwable) {
            MDD.alert(throwable);
        }

    }

    @Action(order = 2)
    public void createInvoicesAndSend(EntityManager em, String email, @TextArea String postscript) {
        invoices.forEach(i -> {
            i.setNumber(i.getAgency().getCompany().getBillingSerial().createInvoiceNumber());
            i.setSerial(i.getAgency().getCompany().getBillingSerial());
            em.persist(i);
        });

        try {

            if (!Strings.isNullOrEmpty(email)) {

                URL pdf = Invoice.createPdf(invoices);
                EmailHelper.sendEmail(email, "Invoices", postscript, false, pdf);

            } else {
                Map<FinancialAgent, List<Invoice>> invoicesByAgent = new HashMap<>();
                invoices.forEach(i -> {
                    List<Invoice> l = invoicesByAgent.get(i.getRecipient());
                    if (l == null) {
                        invoicesByAgent.put(i.getRecipient(), l = new ArrayList<>());
                    }
                    l.add(i);
                });

                for (FinancialAgent a : invoicesByAgent.keySet()) {
                    URL pdf = Invoice.createPdf(invoicesByAgent.get(a));
                    EmailHelper.sendEmail(a.getInvoicingEmail(), "Invoices", postscript, false, pdf);
                }
            }


        } catch (Throwable throwable) {
            MDD.alert(throwable);
        }

    }
}
