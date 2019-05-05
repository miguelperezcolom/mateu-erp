package io.mateu.erp.model.organization;

import io.mateu.erp.model.accounting.AccountingPlan;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.invoicing.InvoiceSerial;
import io.mateu.mdd.core.annotations.Section;
import io.mateu.mdd.core.model.common.Resource;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @NotNull
    @ManyToOne
    private FinancialAgent financialAgent;

    @NotNull
    @ManyToOne
    private AccountingPlan accountingPlan;

    @ManyToOne(cascade = CascadeType.ALL)
    private Resource logo;

    @ManyToOne@NotNull
    private InvoiceSerial billingSerial;


    @ManyToOne@NotNull
    private InvoiceSerial selfBillingSerial;



    @Section("Payment data")
    private String bankName;
    private String bankAddress;
    private String recipient;
    private String accountNumber;
    private String iban;
    private String swift;




    @Override
    public String toString() {
        return getName();
    }


    public Element toXml() {
        Element xml = new Element("company");
        xml.setAttribute("id", "" + getId());
        xml.setAttribute("name", getName());
        if (getLogo() != null) {
            try {
                xml.setAttribute("logo", "file:" + getLogo().toFileLocator().getTmpPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (getFinancialAgent() != null) {
            if (getFinancialAgent().getBusinessName() != null) xml.setAttribute("businessName", getFinancialAgent().getBusinessName());
            if (getFinancialAgent().getAddress() != null) xml.setAttribute("address", getFinancialAgent().getAddress());
            if (getFinancialAgent().getCity() != null) xml.setAttribute("resort", getFinancialAgent().getCity());
            if (getFinancialAgent().getPostalCode() != null) xml.setAttribute("postalCode", getFinancialAgent().getPostalCode());
            if (getFinancialAgent().getState() != null) xml.setAttribute("state", getFinancialAgent().getState());
            if (getFinancialAgent().getCountry() != null) xml.setAttribute("country", getFinancialAgent().getCountry());
            if (getFinancialAgent().getVatIdentificationNumber() != null) xml.setAttribute("vatIdentificationNumber", getFinancialAgent().getVatIdentificationNumber());
            if (getFinancialAgent().getEmail() != null) xml.setAttribute("email", getFinancialAgent().getEmail());
            if (getFinancialAgent().getTelephone() != null) xml.setAttribute("telephone", getFinancialAgent().getTelephone());
        }

        return xml;
    }
}
