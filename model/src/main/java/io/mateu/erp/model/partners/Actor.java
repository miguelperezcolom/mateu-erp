package io.mateu.erp.model.partners;

import com.google.common.base.Strings;
import io.mateu.erp.dispo.interfaces.common.IActor;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.revenue.HandlingFee;
import io.mateu.erp.model.revenue.Markup;
import io.mateu.erp.model.thirdParties.Integration;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersByEmailTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.ui.mdd.server.annotations.*;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for customers (e.g. a touroperator, a travel agency, ...)
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter
@Setter
public class Actor implements IActor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Tab("General")
    @NotNull
    @SearchFilter
    @ListColumn
    private String name;

    private boolean active = true;

    private ActorStatus status;

    private boolean agency;

    @SameLine
    private boolean provider;

    @SameLine
    private boolean hotelChain;

    @ManyToOne
    private ActorGroup group;

    @ManyToOne
    private Actor headQuater;

    @ManyToOne
    private Market market;

    @ManyToOne
    @NotNull
    private Currency currency;

    @ManyToOne
    private FinancialAgent financialAGent;


    @ListColumn
    private String email;

    @ListColumn
    private String comments;

    @Tab("Deprecated")
    @ListColumn
    private String businessName;

    private String address;

    private String vatIdentificationNumber;

    @Tab("Product filters")
    private boolean onRequestAllowed;

    private boolean PVPAllowed;

    private boolean thridPartyAllowed;

    @Tab("Revenue")
    @ManyToOne
    private Markup markup;

    @ManyToOne
    private HandlingFee handlingFee;

    @Tab("As supplier")
    private String payableByInVoucher;

    private double extraMarkupPercent;


    @Tab("Cancellation rules")
    @OneToMany(mappedBy = "actor")
    @OwnedList
    private List<CancellationRule> cancellationRules = new ArrayList<>();

    @Tab("Invoicing")
    private boolean exportableToinvoicingApp;
    private String idInInvoicingApp;
    private boolean shuttleTransfersInOwnInvoice;
    private boolean oneLinePerBooking;


    @Tab("Orders sending")
    private PurchaseOrderSendingMethod ordersSendingMethod;
    private String sendOrdersTo;

    private boolean automaticOrderSending;
    private boolean automaticOrderConfirmation;


    @Tab("Integrations")
    @ManyToMany
    private List<Integration> integrations = new ArrayList<>();

    @Override
    public String toString() {
        return getName();
    }

    public Element toXml() {
        Element xml = new Element("actor");
        xml.setAttribute("id", "" + getId());
        xml.setAttribute("name", getName());
        if (getBusinessName() != null) xml.setAttribute("bussinessName", getBusinessName());
        if (getAddress() != null) xml.setAttribute("address", getBusinessName());
        if (getVatIdentificationNumber() != null) xml.setAttribute("vatIdentificationNumber", getVatIdentificationNumber());
        if (getEmail() != null) xml.setAttribute("email", getEmail());
        if (getComments() != null) xml.setAttribute("comments", getComments());

        return xml;
    }

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
}
