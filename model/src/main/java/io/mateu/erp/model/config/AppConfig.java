package io.mateu.erp.model.config;

import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.product.DataSheet;
import io.mateu.mdd.core.annotations.Section;
import io.mateu.mdd.core.annotations.TextArea;
import io.mateu.mdd.core.model.common.Resource;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Created by miguel on 19/3/17.
 */
@Entity(name = "ERPAppConfig")
@Getter
@Setter
public class AppConfig extends io.mateu.mdd.core.model.config.AppConfig {

    @TextArea
    private String xslfoForHotelContract;

    @TextArea
    private String xslfoForTransferContract;

    @TextArea
    private String xslfoForGenericContract;

    @TextArea
    private String xslfoForTourContract;

    @TextArea
    private String xslfoForVoucher;

    @TextArea
    private String xslfoForQuotationRequest;

    @TextArea
    private String xslfoForIssuedInvoice;

    @TextArea
    private String xslfoForReceivedInvoice;

    @TextArea
    private String xslfoForPOSSettlement;

    @TextArea
    private String xslfoForWorld;

    @TextArea
    private String xslfoForTransfersList;

    @TextArea
    private String xslfoForPurchaseOrder;

    @TextArea
    private String purchaseOrderTemplate;

    @TextArea
    private String roomingTemplate;

    @TextArea
    private String manifestTemplate;

    @TextArea
    private String bookedEmailTemplate;

    @TextArea
    private String vouchersEmailTemplate;

    @TextArea
    private String paymentEmailTemplate;

    @TextArea
    private String pickupSmsTemplate;

    @TextArea
    private String pickupEmailTemplate;

    @TextArea
    private String pickupSmsTemplateEs;


    @Section("Default billing concepts")
    @ManyToOne
    private BillingConcept billingConceptForHotel;

    @ManyToOne
    private BillingConcept billingConceptForTransfer;

    @ManyToOne
    private BillingConcept billingConceptForExcursion;

    @ManyToOne
    private BillingConcept billingConceptForCircuit;

    @ManyToOne
    private BillingConcept billingConceptForOthers;

    @Section("Currency")
    @NotNull
    @ManyToOne
    private Currency nucCurrency;

    @ManyToOne(cascade = CascadeType.ALL)
    private Resource invoiceWatermark;



    @Section("Transfers")
    @ManyToOne
    private DataSheet shuttleDataSheet;

    @ManyToOne
    private DataSheet privateDataSheet;

    @ManyToOne
    private DataSheet executiveDataSheet;

    @ManyToOne
    private DataSheet incomingDataSheet;


    //@Tab("Currency exchange")
    //@Convert(converter = CurrencyExchangeConverter.class)
    //private CurrencyExchange currencyExchange = new CurrencyExchange();

    public static AppConfig get(EntityManager em) {
        return em.find(AppConfig.class, 1l);
    }

}
