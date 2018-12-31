package io.mateu.erp.model.config;

import io.mateu.erp.model.financials.Currency;
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
    private String xslfoForIssuedInvoice;

    @TextArea
    private String xslfoForReceivedInvoice;

    @TextArea
    private String xslfoForWorld;

    @TextArea
    private String xslfoForTransfersList;

    @TextArea
    private String xslfoForPurchaseOrder;

    @TextArea
    private String purchaseOrderTemplate;

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


    @Section("Currency")
    @NotNull
    @ManyToOne
    private Currency nucCurrency;

    @ManyToOne(cascade = CascadeType.ALL)
    private Resource invoiceWatermark;


    //@Tab("Currency exchange")
    //@Convert(converter = CurrencyExchangeConverter.class)
    //private CurrencyExchange currencyExchange = new CurrencyExchange();

    public static AppConfig get(EntityManager em) {
        return em.find(AppConfig.class, 1l);
    }

}
