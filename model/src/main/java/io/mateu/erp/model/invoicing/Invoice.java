package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.financials.Amount;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.financials.RebateSettlement;
import io.mateu.erp.model.taxes.VATSettlement;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
@NewNotAllowed
public abstract class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @Output
    private Audit audit;

    @NotNull
    @Output
    private InvoiceType type;

    @ManyToOne@NotNull
    @Output
    private InvoiceSerial serial;

    @NotEmpty
    @Output
    @ListColumn
    private String number;

    @NotNull
    @Output
    @ListColumn
    private LocalDate issueDate;

    @NotNull
    @Output
    private LocalDate taxDate;


    @ManyToOne
    @NotNull
    @Output
    @ListColumn
    private FinancialAgent issuer;

    @ManyToOne
    @NotNull
    @Output
    @ListColumn
    private FinancialAgent recipient;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @Output
    private List<AbstractInvoiceLine> lines = new ArrayList<>();


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column=@Column(name="total_value"))
            , @AttributeOverride(name="date", column=@Column(name="total_date"))
            , @AttributeOverride(name="officeChangeRate", column=@Column(name="total_offchangerate"))
            , @AttributeOverride(name="officeValue", column=@Column(name="total_offvalue"))
            , @AttributeOverride(name="nucChangeRate", column=@Column(name="total_nuchangerate"))
            , @AttributeOverride(name="nucValue", column=@Column(name="total_nucvalue"))
    })
    @AssociationOverrides({
            @AssociationOverride(name="currency", joinColumns = @JoinColumn(name = "total_currency"))
    })
    @KPI
    @NotWhenCreating
    @NotNull
    @ListColumn
    private Amount total;

    @KPI
    @ListColumn
    private boolean valid = true;

    @KPI
    @ListColumn
    private boolean paid;


    @Output
    private double retainedPercent;

    @Output
    private double retainedTotal;


    @ManyToOne
    @Output
    private RebateSettlement rebateSettlement;

    @ManyToOne
    @Output
    private VATSettlement vatSettlement;


    public Invoice(User u, Collection<? extends Charge> charges) throws Throwable {
        this(u, charges, false);
    }


    public Invoice(User u, Collection<? extends Charge> charges, boolean proforma) throws Throwable {

        if (charges == null || charges.size() == 0) throw new Exception("Can not create invoices from an empty list of charges");


        boolean inicializar = true;


        double total = 0;

        for (Charge c : charges) {

            if (inicializar) {

                setType((ChargeType.SALE.equals(c.getType()))?InvoiceType.ISSUED:InvoiceType.RECEIVED);

                setAudit(new Audit(u));

                setTotal(new Amount(FastMoney.of(0, c.getTotal().getCurrency().getIsoCode())));

                setIssueDate(LocalDate.now());

                if (c.getPartner().getFinancialAgent() == null) throw new Exception("If you want to create proformas or invoices you must set the financial agent for the agency " + c.getPartner().getName());
                if (c.getPartner().getCompany() == null) throw new Exception("If you want to create proformas or invoices you must set the company for the agency " + c.getPartner().getName());
                if (c.getPartner().getCompany().getFinancialAgent() == null) throw new Exception("If you want to create proformas or invoices you must set the financial agent for the company " + c.getPartner().getCompany().getName());

                setRecipient((ChargeType.SALE.equals(c.getType()))?c.getPartner().getFinancialAgent():c.getPartner().getCompany().getFinancialAgent());
                setIssuer((ChargeType.PURCHASE.equals(c.getType()))?c.getPartner().getFinancialAgent():c.getPartner().getCompany().getFinancialAgent());

                setTaxDate(LocalDate.now());

                inicializar = false;
            }


            if (c instanceof BookingCharge) getLines().add(new BookingInvoiceLine(this, (BookingCharge) c));
            else if (c instanceof PurchaseCharge) getLines().add(new PurchaseOrderInvoiceLine(this, (PurchaseCharge) c));
            if (!proforma) {
                c.setInvoice(this);
            }

            total += c.getTotal().getValue();
        }


        setTotal(new Amount(FastMoney.of(total, getTotal().getCurrency().getIsoCode())));

        setRetainedPercent(0);

    }

    public Invoice() {

    }


    public Element toXml() {
        return new Element("invoice");
    }


    @PrePersist
    public void prePersist() {
        if (number == null) number = UUID.randomUUID().toString();
    }


    @Override
    public String toString() {
        return number;
    }




}
