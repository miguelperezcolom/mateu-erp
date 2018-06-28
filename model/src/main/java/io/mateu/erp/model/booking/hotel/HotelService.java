package io.mateu.erp.model.booking.hotel;

import com.google.common.base.Strings;
import io.mateu.erp.dispo.*;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.common.model.authentication.Audit;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.Allocation;
import org.easytravelapi.hotel.AvailableHotel;
import org.easytravelapi.hotel.BoardPrice;
import org.easytravelapi.hotel.Option;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
@Getter
@Setter
public class HotelService extends Service {

    @Tab("Service")
    @ManyToOne
    @NotNull
    private Hotel hotel;

    @ManyToOne
    @Output
    private HotelContract saleContract;

    @ManyToOne
    @Output
    private HotelContract purchaseContract;

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
    public double rate(EntityManager em, boolean sale, Partner supplier, PrintWriter report) throws Throwable {
        AvailableHotel ah = new HotelAvailabilityRunner().check(getBooking().getAgency(), getHotel(), getBooking().getAgency().getId(), 1, new ModeloDispo() {
            @Override
            public IHotelContract getHotelContract(long id) {
                return em.find(HotelContract.class, id);
            }
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
        DispoRQ rq = new DispoRQ(LocalDate.now(), io.mateu.erp.dispo.Helper.toInt(getStart()), io.mateu.erp.dispo.Helper.toInt(getFinish()), ocs, false);
        return rq;
    }


    @PostUpdate@PostPersist
    public void afterSet() throws Throwable {

        EntityManager em = Helper.getEMFromThreadLocal();

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

        afterSetAsService(em);
        
        
        /*

        WorkflowEngine.add(new Runnable() {

            long serviceId = getId();

            @Override
            public void run() {

                try {
                    Helper.transact(new JPATransaction() {
                        @Override
                        public void run(EntityManager em) throws Throwable {

                            HotelService hs = em.find(HotelService.class, serviceId);

                            LocalDate s = null, f = null;
                            boolean algunaLineaActiva = false;
                            for (HotelServiceLine l : hs.getLines()) {
                                if (l.getStart() != null && (s == null || l.getStart().isBefore(s))) s = l.getStart();
                                if (l.getEnd() != null && (f == null || l.getEnd().isAfter(f))) f = l.getEnd();
                                algunaLineaActiva |= l.isActive();
                            }
                            hs.setStart(s);
                            hs.setFinish(f);
                            hs.setCancelled(!algunaLineaActiva);

                            hs.afterSetAsService(em);
                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
        });
        */


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

    @Action(name = "Look for available")
    public static MDDLink book(UserData user, HotelBookingWizard wizard) throws Throwable {
        Pagina2 p2 = (Pagina2)wizard.getPages().get(1);
        Pagina2b p2b = (Pagina2b)wizard.getPages().get(2);
        Pagina3 p3 = (Pagina3)wizard.getPages().get(3);
        long serviceId = createFromKey(user, new KeyValue(p2b.getOption().getKey()), p3.getAgencyReference(), p3.getLeadName(), p3.getComments());
        return new MDDLink(HotelService.class, ActionType.OPENEDITOR, new Data("_id", serviceId));
    }

    public static long createFromKey(UserData user, KeyValue k, String agencyReference, String leadName, String comments) throws Throwable {
        long[] id = {-1};

        if (Strings.isNullOrEmpty(leadName)) throw new Exception("Lead name is mandatory");

        Helper.transact((JPATransaction) (em) -> {

            io.mateu.erp.model.authentication.User u = em.find(io.mateu.erp.model.authentication.User.class, user.getLogin());

            Partner agencia = em.find(Partner.class, k.getAgencyId());
            Hotel hotel = em.find(Hotel.class, k.getHotelId());
            Office oficina = hotel.getOffice();
            PointOfSale pos = em.find(PointOfSale.class, k.getPointOfSaleId());

            Booking b = new Booking();
            b.setAgency(agencia);
            b.setCurrency(agencia.getCurrency());
            b.setAgencyReference(agencyReference);
            b.setAudit(new Audit(u));
            b.setConfirmed(true);
            b.setLeadName(leadName);
            em.persist(b);

            HotelService s = new HotelService();
            em.persist(s);
            b.getServices().add(s);
            s.setBooking(b);
            s.setHotel(hotel);
            s.setOffice(oficina);
            s.setPos(pos);
            s.setAudit(new Audit(u));
            s.setComment(comments);
            if (k.getSaleContractId() > 0) s.setSaleContract(em.find(HotelContract.class, k.getSaleContractId()));
            if (s.getSaleContract() != null) {
                //todo: corregir!!!!
                s.setPurchaseContract(s.getSaleContract());
            }


            for (Allocation a : k.getAllocation()) {
                HotelServiceLine l;
                s.getLines().add(l = new HotelServiceLine());
                em.persist(l);
                l.setService(s);

                l.setActive(true);
                l.setAges(a.getAges());
                l.setBoardType(em.find(BoardType.class, k.getBoardPrice().getBoardBasisId()));
                l.setEnd(io.mateu.erp.dispo.Helper.toDate(k.getCheckOut()));
                l.setStart(io.mateu.erp.dispo.Helper.toDate(k.getCheckIn()));
                l.setNumberOfRooms(a.getNumberOfRooms());
                l.setPaxPerRoom(a.getPaxPerRoom());
                l.setRoomType(em.find(RoomType.class, a.getRoomId()));

            }

            em.flush();

            id[0] = s.getId();

        });


        return id[0];
    }

    @Override
    public String toString() {
        return "" + ((getHotel() != null)?getHotel().getName():"no hotel") + " " + ((getBooking() != null)?getBooking().getLeadName():"no booking");
    }


    @Override
    public Partner findBestProvider(EntityManager em) throws Throwable {
        if (getPurchaseContract() != null) return getPurchaseContract().getSupplier();
        else if (getSaleContract() != null) return getSaleContract().getSupplier();
        else return null;
    }


}
