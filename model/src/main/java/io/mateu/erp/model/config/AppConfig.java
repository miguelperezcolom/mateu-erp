package io.mateu.erp.model.config;

import io.mateu.ui.mdd.server.annotations.StartsLine;
import io.mateu.ui.mdd.server.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 19/3/17.
 */
@Entity
@Getter
@Setter
public class AppConfig {

    @Id
    private long id;

    @StartsLine
    private String adminEmailSmtpHost;

    private String adminEmailUser;

    private String adminEmailPassword;


    @StartsLine
    @TextArea
    private String xslfoForList;

    @TextArea
    private String xslfoForContract;

    @TextArea
    private String xslfoForVoucher;

    @TextArea
    private String xslfoForIssuedInvoice;

    @TextArea
    private String xslfoForWorld;

    @TextArea
    private String xslfoForObject;


    public static AppConfig get(EntityManager em) {
        return em.find(AppConfig.class, 1l);
    }

}
