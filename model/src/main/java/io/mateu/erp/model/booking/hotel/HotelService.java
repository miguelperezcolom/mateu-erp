package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.generic.PriceLine;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class HotelService extends Service implements WithTriggers {


    @ManyToOne
    private Hotel hotel;

    @OneToMany(mappedBy = "service")
    private List<HotelServiceLine> lines = new ArrayList<>();



    @Override
    public String createSignature() {
        return null;
    }

    @Override
    public double rate(EntityManager em, boolean sale, Actor supplier, PrintWriter report) throws Throwable {
        return 0;
    }

    @Override
    public void beforeSet(EntityManager entityManager, boolean b) throws Throwable {

    }

    @Override
    public void beforeDelete(EntityManager entityManager) throws Throwable {

    }

    @Override
    public void afterDelete(EntityManager entityManager) throws Throwable {

    }

    @Override
    public void afterSet(EntityManager em, boolean isNew) throws Throwable {
        LocalDate s = null, f = null;
        for (HotelServiceLine l : getLines()) {
            if (l.getStart() != null && (s == null || l.getStart().isBefore(s))) s = l.getStart();
            if (l.getEnd() != null && (f == null || l.getEnd().isAfter(f))) f = l.getEnd();
        }
        setStart(s);
        setFinish(f);

        super.afterSet(em, isNew);

    }
}
