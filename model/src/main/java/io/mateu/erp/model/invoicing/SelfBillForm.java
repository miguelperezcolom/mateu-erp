package io.mateu.erp.model.invoicing;

import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.ColumnWidth;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SelfBillForm {
    @Ignored
    private final Set<IssuedInvoice> selection;

    public SelfBillForm(Set<IssuedInvoice> selection) {
        this.selection = selection;
        for (IssuedInvoice i : selection) {
            SelectedInvoice a;
            invoicesToBePushedBack.add(a = new SelectedInvoice());
            a.setInvoiceId(i.getId());
            a.setInvoiceNumber(i.getNumber());
            a.setInvoiceDate(i.getIssueDate());
            a.setIssuer(i.getIssuer().getName());
            a.setRecipient(i.getRecipient().getName());
            a.setAmount(i.getTotal());
            a.setCurrency(i.getCurrency().getIsoCode());
        }
    }

    private String postscript;

    private List<SelectedInvoice> invoicesToBePushedBack = new ArrayList<>();

    @Getter@Setter
    public class SelectedInvoice {
        private long invoiceId;
        private String invoiceNumber;
        private LocalDate invoiceDate;
        private String recipient;
        private String issuer;
        private double amount;
        @ColumnWidth(100)
        private String currency;
    }

    @Action
    public void createSelfBill() throws Throwable {

        Helper.transact(em -> {

            for (SelectedInvoice si : invoicesToBePushedBack) {

                IssuedInvoice i = em.find(IssuedInvoice.class, si.getInvoiceId());

                List<BookingCharge> cargosAFacturar = new ArrayList<>();

                for (AbstractInvoiceLine al : i.getLines()) {
                    if (al instanceof BookingInvoiceLine) {
                        BookingInvoiceLine bil = (BookingInvoiceLine) al;

                        cargosAFacturar.add(crearCargo(bil.getCharge(), -1d));

                        crearCargo(bil.getCharge(), 1d);
                    }
                }

                IssuedInvoice sbi = new IssuedInvoice(MDD.getCurrentUser(), cargosAFacturar, false, i.getIssuer(), i.getRecipient(), i.getAgency().getCompany().getSelfBillingSerial().createInvoiceNumber());
                em.persist(sbi);

            }

        });

    }

    private BookingCharge crearCargo(BookingCharge c, double factor) {

        BookingCharge nc = c instanceof ExtraBookingCharge?new ExtraBookingCharge():new BookingCharge();

        nc.setBooking(c.getBooking());
        if (c instanceof ExtraBookingCharge) nc.getBooking().getExtraCharges().add((ExtraBookingCharge) nc);
        else nc.getBooking().getServiceCharges().add(nc);

        nc.setAgency(c.getAgency());
        nc.setCurrency(c.getCurrency());
        nc.setBillingConcept(c.getBillingConcept());
        nc.setText(c.getText());
        nc.setOffice(c.getOffice());
        nc.setType(c.getType());
        nc.setExtra(c.isExtra());
        nc.setTotal(Helper.roundEuros(factor * c.getTotal()));
        nc.setAudit(new Audit(MDD.getCurrentUser()));
        nc.setCurrencyExchange(c.getCurrencyExchange());
        nc.setServiceDate(c.getServiceDate());
        nc.setValueInNucs(Helper.roundEuros(factor * c.getValueInNucs()));

        return nc;
    }
}