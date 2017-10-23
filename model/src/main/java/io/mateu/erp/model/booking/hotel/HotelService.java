package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.dispo.DispoRQ;
import io.mateu.erp.dispo.HotelAvailabilityRunner;
import io.mateu.erp.dispo.ModeloDispo;
import io.mateu.erp.dispo.Occupancy;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.generic.PriceLine;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.AvailableHotel;
import org.easytravelapi.hotel.BoardPrice;
import org.easytravelapi.hotel.Option;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
@Getter
@Setter
public class HotelService extends Service implements WithTriggers {


    @Tab("Service")
    @ManyToOne
    @Required
    private Hotel hotel;

    @OneToMany(mappedBy = "service")
    @OwnedList
    private List<HotelServiceLine> lines = new ArrayList<>();



    @Override
    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = toMap();
            m.put("hotel", getHotel().getName());
            List<Map<String, Object>> ls = new ArrayList<>();
            for (HotelServiceLine l : getLines()) {
                Map<String, Object> x;
                ls.add(x = new HashMap<>());
                x.put("numberofrooms", l.getNumberOfRooms());
                x.put("paxperroom", l.getPaxPerRoom());
                if (l.getStart() != null) x.put("start", l.getStart().format(DateTimeFormatter.ISO_DATE));
                if (l.getEnd() != null) x.put("finish", l.getEnd().format(DateTimeFormatter.ISO_DATE));
                if (l.getAges() != null) x.put("ages", Arrays.toString(l.getAges()));
                if (l.getBoardType() != null && l.getBoardType().getName() != null) x.put("board", l.getBoardType().getName().getEs());
                if (l.getRoomType() != null && l.getRoomType().getName() != null) x.put("room", l.getRoomType().getName().getEs());
                x.put("active", "" + l.isActive());
            }
            for (int i = 0; i < ls.size(); i++) m.put("line_" + i, ls.get(i));

            m.put("cancelled", "" + isCancelled());
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public double rate(EntityManager em, boolean sale, Actor supplier, PrintWriter report) throws Throwable {
        AvailableHotel ah = new HotelAvailabilityRunner().check(getHotel(), getBooking().getAgency().getId(), new ModeloDispo() {
        }, createDispoRQ());

        double value = 0;
        if (ah != null) {
            for (Option o : ah.getOptions()) {
                if (matches(o)) {
                    for (BoardPrice p : o.getPrices()) { // solo 1 precio devuelto, que será según los regímenes elegidos en la reserva
                        value = p.getNetPrice().getValue();
                    }
                }
            }
        } else {
            throw new Exception("It is not possible to valuate this service");
        }
        return value;
    }

    private boolean matches(Option o) {
        boolean matches = true;
        int pos = 0;
        for (HotelServiceLine l : getLines()) {
            matches &= l.getRoomType().getCode().equals(o.getDistribution().get(pos).getRoomId());
        }
        matches &= getLines().size() == o.getDistribution().size();
        return matches;
    }

    private DispoRQ createDispoRQ() {
        List<Occupancy> ocs = new ArrayList<>();
        for (HotelServiceLine l : getLines()) {
            ocs.add(new Occupancy(l.getNumberOfRooms(), l.getPaxPerRoom(), l.getAges(), l.getBoardType().getCode()));
        }
        DispoRQ rq = new DispoRQ(io.mateu.erp.dispo.Helper.toInt(getStart()), io.mateu.erp.dispo.Helper.toInt(getFinish()), ocs, false);
        return rq;
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
        boolean algunaLineaActiva = false;
        for (HotelServiceLine l : getLines()) {
            if (l.getStart() != null && (s == null || l.getStart().isBefore(s))) s = l.getStart();
            if (l.getEnd() != null && (f == null || l.getEnd().isAfter(f))) f = l.getEnd();
            algunaLineaActiva |= l.isActive();
        }
        setStart(s);
        setFinish(f);
        setCancelled(!algunaLineaActiva);

        super.afterSet(em, isNew);

    }


    @Action(name = "Price")
    public static void price(UserData user, @Selection List<Data> _selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data d : _selection) {
                    HotelService s = em.find(HotelService.class, d.get("_id"));
                    s.price(em, user);
                }
            }
        });
    }

    @Override
    public String toString() {
        return "" + ((getHotel() != null)?getHotel().getName():"no hotel") + " " + ((getBooking() != null)?getBooking().getLeadName():"no booking");
    }
}
