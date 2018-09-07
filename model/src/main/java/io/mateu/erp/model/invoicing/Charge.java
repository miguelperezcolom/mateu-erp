package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.File;
import io.mateu.erp.model.financials.Amount;
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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;

@Entity
@Getter
@Setter
public class Charge {

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
    @Output
    private File file;

    public boolean isFileVisible() {
        return file != null;
    }

    @ManyToOne
    @Output
    private Booking booking;

    public boolean isBookingVisible() {
        return booking != null;
    }


    @ManyToOne
    @Output
    private Service service;

    public boolean isServiceVisible() {
        return booking != null;
    }

    @ManyToOne
    @Output
    private HotelContract hotelContract;

    public boolean isHotelContractVisible() {
        return hotelContract != null;
    }


    @ManyToOne
    @Output
    private PurchaseOrder purchaseOrder;

    public boolean isPurchaseOrderVisible() {
        return purchaseOrder != null;
    }


    @ManyToOne
    @Output
    private Invoice invoice;


    @ManyToOne
    @Output
    private Office office;

    public boolean isOfficeVisible() {
        return office != null;
    }

    @NotNull
    @ManyToOne
    @Output
    private BillingConcept billingConcept;

    @TextArea
    @Output
    private String text;


    @ManyToOne
    @Output
    private VAT vat;

    @Output
    @SameLine
    private double vatPercent;

    @Output
    @SameLine
    private double vatValue;

    @Output
    @SameLine
    private double beforeTaxes;

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
    private Amount total;




    @PrePersist@PreUpdate
    public void validate() throws Exception {
        if (this.getFile() == null && getPurchaseOrder() == null) throw  new Exception("It must be related to a file or to a purchase order");
    }


    public void applyTaxes() {
        setVat(null);
        setBeforeTaxes(getTotal().getNucValue());
        setVatValue(0);
        setVatPercent(0);
    }


    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("##,###,###,###,###.00");
        return "<div style='text-align:right;width:100px;display:inline-block;margin-right:10px;'>" + df.format(total.getValue()) + "</div><div style='display: inline-block;'>" + text + "</div>";
    }
}
