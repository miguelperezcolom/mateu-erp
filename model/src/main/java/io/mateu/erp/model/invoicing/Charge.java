package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.File;
import io.mateu.erp.model.financials.Amount;
import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.erp.model.financials.BillingConcept;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;

@Entity
@Getter
@Setter
public class Charge {

    @KPI
    private transient Amount value;

    public Amount getValue() {
        return total;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @Output
    private Audit audit;

    @NotNull
    @Output
    private ChargeType type;

    @ManyToOne
    @NotNull
    private Partner partner;


    @ManyToOne
    private Office office;

    @NotNull
    @ManyToOne
    private BillingConcept billingConcept;

    @TextArea
    private String text;


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
    private Amount total;


    @Caption("Total")
    @NotInEditor
    private transient double nucs;

    public double getNucs() {
        return total.getNucValue();
    }

    @ManyToOne
    @Output
    private Invoice invoice;


    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("##,###,###,###,###.00");
        return "<div style='text-align:right;width:100px;display:inline-block;margin-right:10px;'>" + ((total != null)?df.format(total.getValue()):"0.0") + "</div><div style='display: inline-block;'>" + ((text != null)?text:"---") + "</div>";
    }


    public boolean isModifiable() {
        return invoice == null;
    }
}
