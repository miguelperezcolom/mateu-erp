package io.mateu.erp.model.organization;

import com.google.common.base.Strings;
import com.vaadin.ui.Button;
import io.mateu.erp.model.financials.CommissionAplicationBasis;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.partners.CommissionAgent;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.world.Resort;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.NotInList;
import io.mateu.mdd.core.annotations.Section;
import io.mateu.mdd.core.model.util.EmailHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * wheer sale is effectively done (e.g. Hotel xxx, ....) for commission calculation and statistics
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class SalesPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    @NotNull@ManyToOne
    private Office office;

    @ManyToOne
    private TransferPoint pickupPoint;

    private CommissionAgent comissionAgent;

    @NotNull
    private CommissionAplicationBasis basis = CommissionAplicationBasis.MARKUP;

    private double percent;




    @Override
    public String toString() {
        return getName();
    }
}
