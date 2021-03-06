package io.mateu.erp.model.financials;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.invoicing.InvoiceSerial;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.payments.MethodOfPayment;
import io.mateu.erp.model.payments.Payment;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for a admin agent, tipically associated to a VAT ID (e.g. a customer, a supplier, ourselves)
 *
 * Sometimes the same admin agent will act as customer and as supplier. This is interesting to manage a balance of payments
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class FinancialAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Tab("Info")
    @ListColumn
    private String name;

    private String businessName;

    private String address;

    private String postalCode;

    private String city;

    private String state;

    private String country;

    private String telephone;

    private String fax;

    private String email;

    @TextArea
    private String comments;


    @Tab("Taxes")
    private String vatIdentificationNumber;

    @ManyToOne
    private VAT vat;

    private boolean EU;

    @Tab("As customer")

    private boolean directSale;

    @ManyToOne
    private InvoiceSerial invoiceSerial;

    @NotNull
    private AutomaticInvoiceBasis automaticInvoiceBasis = AutomaticInvoiceBasis.NONE;

    private boolean lasMonthDay;

    private boolean fortnight;

    @NotNull
    private InvoiceGrouping invoiceGrouping = InvoiceGrouping.BOOKING;

    @ManyToOne
    private PaymentTerms customerPaymentTerms;

    private String invoicingEmail;

    private String invoicingEmailCC;

    @NotNull
    private InvoiceSending invoiceSending = InvoiceSending.EMAIL;

    private boolean invoicePerKey;

    @Tab("As provider")
    private boolean specialRegime;

    private boolean invoicesBeforeCheckinAllowed;

    @ManyToOne
    private RetentionTerms retentionTerms;

    @ManyToOne
    private MethodOfPayment methodOfPayment;

    private boolean paymentsBlocked;

    private String cc;

    private String IBAN;

    private String SWIFT;


    @Tab("Credit")
    @NotNull
    @ListColumn
    private RiskType riskType = RiskType.PREPAYMENT;

    @ManyToMany
    private List<CreditLimit> creditLimits = new ArrayList<>();

    @Tab("Voxel")
    private String voxelId;

    @ListColumn
    @KPI
    private double invoiced;

    @ListColumn
    @KPI
    private double balance;


    @Ignored
    private LocalDateTime triggerUpdate;


    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (id != 0 && obj != null && obj instanceof FinancialAgent && id == ((FinancialAgent) obj).getId());
    }




    @PostUpdate@PostPersist
    public void post() {

        if (triggerUpdate != null) {

            WorkflowEngine.add(() -> {

                System.out.println("FinancialAgent " + getId() + ".post().run()");
                try {
                    Helper.transact(em -> {
                        FinancialAgent b = em.find(FinancialAgent.class, getId());

                        Double l = (Double) em.createQuery("select sum(x.balance) from " + Payment.class.getName() + " x where x.agent.id = " + b.getId()).getSingleResult();

                        if (l != null) {
                            b.setBalance(l);
                        }

                        b.setTriggerUpdate(null);

                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            });

        }

    }

}
