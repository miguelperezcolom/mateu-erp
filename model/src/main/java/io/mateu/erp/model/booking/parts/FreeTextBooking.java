package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.ValidationStatus;
import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.annotations.TextArea;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter@Setter
public class FreeTextBooking extends Booking {

    @NotNull
    @ManyToOne
    @Position(8)
    private Office office;


    @TextArea
    @NotEmpty
    @Position(15)
    private String serviceDescription;

    @NotNull
    @ManyToOne
    @Position(16)
    private ProductLine productLine;

    @NotNull
    @ManyToOne
    @Position(17)
    private Partner provider;

    @Position(18)
    private FastMoney serviceCost;

    @SameLine
    @ManyToOne
    @Position(19)
    private BillingConcept billingConcept;




    public FreeTextBooking() {
        setIcons(FontAwesome.EDIT.getHtml());
    }


    @Override
    public void validate() throws Exception {
        if (!isValueOverrided()) throw new Exception("Price must be overrided for free text bookings.");
        setValidationStatus(ValidationStatus.VALID);
        setAvailable(true);
    }

    @Override
    protected void generateServices(EntityManager em) {
        FreeTextService s = null;
        if (getServices().size() > 0) {
            s = (FreeTextService) getServices().get(0);
        }
        if (s == null) {
            getServices().add(s = new FreeTextService());
            s.setBooking(this);
            s.setAudit(new Audit(getAudit().getModifiedBy()));
        }
        s.setOffice(office);
        s.setFinish(getEnd());
        s.setStart(getStart());
        s.setText(serviceDescription);
        s.setDeliveryDate(getStart());
        s.setReturnDate(getEnd());
        s.setPreferredProvider(getProvider());
        s.setOverridedCostValue(getServiceCost().getNumber().doubleValue());
        s.setCostOverrided(true);
        em.merge(s);
    }

    @Override
    public void priceServices() throws Throwable {
        throw new Exception("Free text needs price to be overrided");
    }
}
