package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.erp.model.organization.Office;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Position;
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
    @Position(7)
    private Office office;


    @TextArea
    @NotEmpty
    @Position(8)
    private String serviceDescription;



    public FreeTextBooking() {
        setIcons(FontAwesome.EDIT.getHtml());
    }


    @Override
    public void validate() throws Exception {
        if (!isValueOverrided()) throw new Exception("Price must be overrided for free text bookings.");
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
            s.setFile(getFile());
            getFile().getServices().add(s);
            s.setAudit(new Audit(em.find(User.class, MDD.getUserData().getLogin())));
        }
        s.setOffice(office);
        s.setFinish(getEnd());
        s.setStart(getStart());
        s.setText(serviceDescription);
        s.setDeliveryDate(getStart());
        s.setReturnDate(getEnd());
        em.merge(s);
    }

    @Override
    public void priceServices() throws Throwable {
        throw new Exception("Free text needs price to be overrided");
    }
}
