package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.generic.GenericServiceExtra;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class GenericBooking extends Booking {

    @Position(13)
    private int units;

    @NotNull
    @ManyToOne
    @Position(14)
    private Office office;

    @ManyToOne@NotNull
    @Position(15)
    private GenericProduct product;



    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @Position(16)
    private List<GenericBookingExtra> extras = new ArrayList<>();


    public GenericBooking() {
        setIcons(FontAwesome.GIFT.getHtml());
    }


    @Override
    public void validate() throws Exception {

    }

    @Override
    protected void generateServices(EntityManager em) {
        GenericService s = null;
        if (getServices().size() > 0) {
            s = (GenericService) getServices().get(0);
        }
        if (s == null) {
            getServices().add(s = new GenericService());
            s.setBooking(this);
            s.setAudit(new Audit(em.find(User.class, MDD.getUserData().getLogin())));
        }
        s.setOffice(office);
        s.setFinish(getEnd());
        s.setStart(getStart());
        s.setProduct(getProduct());
        for (GenericBookingExtra e : getExtras()) s.getExtras().add(new GenericServiceExtra(s, e));
        s.setDeliveryDate(getStart());
        s.setReturnDate(getEnd());
        em.merge(s);
    }

    @Override
    public void priceServices() {

    }
}
