package io.mateu.erp.model.partners;

import com.google.common.base.Strings;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.workflow.SendPurchaseOrdersByEmailTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.mdd.core.annotations.*;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity@Getter@Setter
public class Provider {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Section("General")
    @NotNull
    @ListColumn
    private String name;

    @NotNull
    @ListColumn
    @ColumnWidth(100)
    private ProviderStatus status;

    @ManyToOne
    @NotNull
    @ListColumn
    @ColumnWidth(100)
    @NoChart
    private Currency currency;

    @ManyToOne
    private FinancialAgent financialAgent;


    private String fullAddress;

    private String telephone;

    private String email;

    private String comments;

    @Section("Invoicing")
    private boolean exportableToinvoicingApp;
    private String idInInvoicingApp;


    @Section("As supplier")
    private String payableByInVoucher;

    private double extraMarkupPercent;


    @Section("Orders sending")
    private PurchaseOrderSendingMethod ordersSendingMethod;
    private String sendOrdersTo;

    private boolean automaticOrderSending;
    private boolean automaticOrderConfirmation;


    public SendPurchaseOrdersTask createTask(EntityManager em, PurchaseOrder purchaseOrder) throws Throwable {
        return createTask(em, getSendOrdersTo(), purchaseOrder.getOffice().getEmailCC());
    }

    public SendPurchaseOrdersByEmailTask createTask(EntityManager em, String toEmail, String cc) throws Throwable {
        if (Strings.isNullOrEmpty(toEmail)) throw new Exception("Email address is missing");
        SendPurchaseOrdersByEmailTask t = new SendPurchaseOrdersByEmailTask();
        t.setTo(toEmail);
        t.setCc(cc);
        t.setMethod(PurchaseOrderSendingMethod.EMAIL);
        return t;
    }

    @Override
    public String toString() {
        return getName();
    }


    @Action
    public void ratesPdf() {
        //todo: crear el tarifario
    }


    public Element toXml() {
        Element xml = new Element("actor");
        xml.setAttribute("id", "" + getId());
        xml.setAttribute("name", getName());
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
        if (getEmail() != null) xml.setAttribute("email", getEmail());
        if (getComments() != null) xml.setAttribute("comments", getComments());

        return xml;
    }


}
