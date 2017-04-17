package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.booking.generic.PriceDetail;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.product.generic.Product;
import io.mateu.erp.model.product.transfer.Vehicle;
import io.mateu.ui.mdd.server.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 31/1/17.
 */
@Entity
@Getter
@Setter
public abstract class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @Ignored
    private Audit audit;

    @ManyToOne
    @Required

    @SearchFilter(field = "id")
    @SearchFilter(field = "agencyReference")
    @SearchFilter(field = "agency")
    @ListColumn(field = "id")
    @ListColumn(field = "agencyReference")
    @ListColumn(field = "agency")
    private Booking booking;

    @StartsLine
    @ListColumn
    private boolean cancelled;

    @ListColumn
    private boolean noShow;


    @TextArea
    private String comment;

    private boolean alreadyInvoiced;

    @Required
    @ManyToOne
    private Office office;

    @Required
    @ManyToOne
    private PointOfSale pos;


    @StartsLine
    @ManyToOne
    private Actor preferredProvider;

    private boolean valueOverrided;

    private double overridedValue;

    @Output
    @ListColumn
    private boolean valued;


    private boolean readyToSend;
    private boolean orderSent;
    private boolean orderConfirmed;


    @Ignored
    @SearchFilter
    @ListColumn
    private LocalDate start;

    @Ignored
    @ListColumn
    private LocalDate finish;

    @Ignored
    private int units;

    @Ignored
    private int adults;

    @Ignored
    private int children;

    @Ignored
    private int[] ages;

    @Output
    @ListColumn
    private double total;

    @Ignored
    @OneToMany
    private List<PriceDetail> priceBreakdown = new ArrayList<>();


    @Action(name = "Price")
    public void price(EntityManager em) {
        setValued(false);
        setTotal(0);
        if (isValueOverrided()) {
            setTotal(getOverridedValue());
            setValued(true);
        }
        else {
            try {
                setTotal(rate(em));
                setValued(true);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    protected abstract double rate(EntityManager em) throws Throwable;


    @Override
    public String toString() {
        String s = "";
        if (getAudit() != null) s += getAudit();
        return s;
    }
}
