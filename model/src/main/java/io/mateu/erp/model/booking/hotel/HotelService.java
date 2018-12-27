package io.mateu.erp.model.booking.hotel;

import com.google.common.base.Strings;
import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.dispo.*;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.booking.File;
import io.mateu.erp.model.booking.ServiceType;
import io.mateu.erp.model.booking.parts.HotelBooking;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.Allocation;
import org.easytravelapi.hotel.AvailableHotel;
import org.easytravelapi.hotel.BoardPrice;
import org.easytravelapi.hotel.Option;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class HotelService extends Service {

    @ManyToOne
    @NotNull
    @SearchFilter
    @ListColumn
    @Position(9)
    @Output
    private Hotel hotel;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    @Position(10)
    @Output
    private List<HotelServiceLine> lines = new ArrayList<>();

    public String getLinesHtml() {

        String h = "<div class='lines'>";
        for (HotelServiceLine l : lines) {
            h += "<div class='line" + (l.isActive() ? "" : " cancelled") + "'>";
            h += l.toString();
            h += "</div>";
        }
        h += "</div>";

        return h;
    }


    public HotelService() {
        setServiceType(ServiceType.HOTEL);
        setIcons(FontAwesome.HOTEL.getHtml());
    }

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
                x.put("adultsperroom", l.getAdultsPerRoom());
                x.put("childrenperroom", l.getChildrenPerRoom());
                if (l.getStart() != null) x.put("start", l.getStart().format(DateTimeFormatter.ISO_DATE));
                if (l.getEnd() != null) x.put("finish", l.getEnd().format(DateTimeFormatter.ISO_DATE));
                if (l.getAges() != null) x.put("ages", Arrays.toString(l.getAges()));
                if (l.getBoard() != null && l.getBoard().getName() != null) x.put("board", l.getBoard().getName());
                if (l.getRoom() != null && l.getRoom().getName() != null) x.put("room", l.getRoom().getName());
                x.put("active", "" + l.isActive());
            }
            for (int i = 0; i < ls.size(); i++) m.put("line_" + i, ls.get(i));

            m.put("cancelled", "" + !isActive());
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public double rate(EntityManager em, boolean sale, Partner supplier, PrintWriter report) throws Throwable {
        /*
        AvailableHotel ah = new HotelAvailabilityRunner().check(this.getBooking().getAgency(), getHotel(), this.getBooking().getAgency().getId(), 1, new ModeloDispo() {
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
        */
        return 0;
    }

    private boolean matches(Option o) {
        boolean matches = true;
        /*
        int pos = 0;
        for (HotelServiceLine l : getLines()) {
            matches &= l.getRoom().getCode().equals(o.getDistribution().get(pos).getRoomId());
        }
        matches &= getLines().size() == o.getDistribution().size();
        */
        return matches;
    }

    private DispoRQ createDispoRQ() {
        List<Occupancy> ocs = new ArrayList<>();
        /*
        for (HotelServiceLine l : getLines()) {
            ocs.add(new Occupancy(l.getNumberOfRooms(), l.getAdultsPerRoom() + l.getChildrenPerRoom(), l.getAges(), l.getBoard().getCode()));
        }
        */
        DispoRQ rq = new DispoRQ(LocalDate.now(), io.mateu.erp.dispo.Helper.toInt(getStart()), io.mateu.erp.dispo.Helper.toInt(getFinish()), ocs, false);
        return rq;
    }


    public void afterSet() throws Throwable {

        EntityManager em = io.mateu.mdd.core.util.Helper.getEMFromThreadLocal();

        LocalDate s = null, f = null;
        boolean algunaLineaActiva = false;
        for (HotelServiceLine l : getLines()) {
            if (l.getStart() != null && (s == null || l.getStart().isBefore(s))) s = l.getStart();
            if (l.getEnd() != null && (f == null || l.getEnd().isAfter(f))) f = l.getEnd();
            algunaLineaActiva |= l.isActive();
        }
        setStart(s);
        setFinish(f);
        setActive(algunaLineaActiva);
    }

    @Override
    protected String getDescription() {
        return "Stay at " + ((getHotel() != null)?getHotel().getName():"");
    }


    /*
    @Action
    public static void price(UserData user, @Selection List<Data> _selection) throws Throwable {
        io.mateu.mdd.core.util.Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data d : _selection) {
                    HotelService s = em.find(HotelService.class, d.get("_id"));
                    s.price(em, user);
                }
            }
        });
    }
    */

    @Action("Look for available")
    public static Pagina1 book() throws Throwable {
        return new Pagina1();
    }

    public static long createFromKey(UserData user, KeyValue k, String agencyReference, String leadName, String comments) throws Throwable {
        long[] id = {-1};

        if (Strings.isNullOrEmpty(leadName)) throw new Exception("Lead name is mandatory");

        io.mateu.mdd.core.util.Helper.transact((JPATransaction) (em) -> {

            io.mateu.erp.model.authentication.User u = em.find(io.mateu.erp.model.authentication.User.class, user.getLogin());

            Partner agencia = em.find(Partner.class, k.getAgencyId());
            Hotel hotel = em.find(Hotel.class, k.getHotelId());
            Office oficina = hotel.getOffice();
            PointOfSale pos = em.find(PointOfSale.class, k.getPointOfSaleId());

            HotelBooking b = new HotelBooking();
            b.setAgency(agencia);
            b.setCurrency(agencia.getCurrency());
            b.setAgencyReference(agencyReference);
            b.setAudit(new Audit(u));
            b.setLeadName(leadName);
            em.persist(b);

            HotelService s = new HotelService();
            em.persist(s);
            b.getServices().add(s);
            s.setBooking(b);
            s.setHotel(hotel);
            s.setOffice(oficina);
            //s.setPos(pos);
            s.setAudit(new Audit(u));
            //s.setComment(comments);


            for (Allocation a : k.getAllocation()) {
                HotelServiceLine l;
                s.getLines().add(l = new HotelServiceLine(s, null)); // todo: esto tiene que crear una reserva, no un servicio
                em.persist(l);
                l.setService(s);

                l.setActive(true);
                l.setAges(a.getAges());
                l.setBoard(em.find(Board.class, Long.parseLong(k.getBoardPrice().getBoardBasisId())));
                l.setEnd(io.mateu.erp.dispo.Helper.toDate(k.getCheckOut()));
                l.setStart(io.mateu.erp.dispo.Helper.toDate(k.getCheckIn()));
                l.setNumberOfRooms(a.getNumberOfRooms());
                l.setAdultsPerRoom(a.getPaxPerRoom());
                l.setChildrenPerRoom(0);
                //l.setRoom(em.find(Room.class, Long.parseLong(a.getRoomId())));

            }

            em.flush();

            id[0] = s.getId();

        });


        return id[0];
    }

    @Override
    public String toString() {
        return "" + ((getHotel() != null)?getHotel().getName():"no hotel") + " " + ((this.getBooking() != null)? this.getBooking().getLeadName():"no file");
    }


    @Override
    public Partner findBestProvider(EntityManager em) throws Throwable {
        {

            Partner p = null;
            for (HotelContract c : hotel.getContracts()) {
                if (ContractType.PURCHASE.equals(c.getType())) {
                    p = c.getSupplier();
                    if (p != null) break;
                }
            }

            return p;
        }
    }


    @Override
    public Element toXml() {
        Element xml = super.toXml();

        xml.setAttribute("type", "hotel");

        xml.setAttribute("header", "" + hotel.getHotelType().getName() + " " + hotel.getName() + " " + hotel.getCategoryName());

        Element els;
        xml.addContent(els = new Element("lines"));

        int pos = 0;
        for (HotelServiceLine l : lines) {
            Element el;
            els.addContent(el = new Element("line"));
            el.setAttribute("units", "" + l.getNumberOfRooms());
            el.setAttribute("adultsperroom", "" + l.getAdultsPerRoom());
            el.setAttribute("childrenperroom", "" + l.getChildrenPerRoom());
            if (l.getAges() != null) el.setAttribute("ages", Arrays.toString(l.getAges()));
            el.setAttribute("start", "" + l.getStart());
            el.setAttribute("end", "" + l.getEnd());
            el.setAttribute("nights", "" + DAYS.between(l.getStart(), l.getEnd().minusDays(1)));
            el.setAttribute("room", "" + l.getRoom().getType());
            el.setAttribute("board", "" + l.getBoard().getType());

            if (pos++ > 2) break;
        }

        return xml;
    }

    public Element getSupplierXml() {
        Element xml = new Element("supplier");
        if (hotel != null) {
            if (hotel.getName() != null) xml.setAttribute("name", "" + hotel.getHotelType().getName() + " " + hotel.getName() + " " + hotel.getCategoryName());
            if (hotel.getAddress() != null) xml.setAttribute("address", hotel.getAddress() + " - " + hotel.getZone().getName() + " - " + hotel.getZone().getDestination() + " - " + hotel.getZip() + " - " + hotel.getZone().getDestination().getCountry());
            if (hotel.getTelephone() != null) xml.setAttribute("telephone", hotel.getTelephone());
            if (hotel.getEmail() != null) xml.setAttribute("email", hotel.getEmail());
            if (hotel.getLat() != null && hotel.getLon() != null) xml.setAttribute("gps", hotel.getLat() + ", " + hotel.getLon());

        }
        return xml;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> d = super.getData();

        d.put("type", "hotel");

        int totalpax = 0;
        for (HotelServiceLine l: lines) totalpax += l.getNumberOfRooms() * (l.getAdultsPerRoom() + l.getChildrenPerRoom());
        d.put("totalpax", totalpax);

        List<Map<String, Object>> ls;
        d.put("lines", ls = new ArrayList<>());
        for (HotelServiceLine l: lines) {
            Map<String, Object> dx = new HashMap<>();
            dx.put("rooms", l.getNumberOfRooms());
            dx.put("room", l.getRoom().getType().getName().toString());
            dx.put("board", l.getBoard().getType().getName().toString());
            dx.put("pax", l.getNumberOfRooms() * (l.getAdultsPerRoom() + l.getChildrenPerRoom()));
            dx.put("adults", l.getNumberOfRooms() * (l.getAdultsPerRoom()));
            dx.put("children", l.getNumberOfRooms() * (l.getChildrenPerRoom()));
            dx.put("ages", l.getAges() != null?Arrays.toString(l.getAges()):"");
            dx.put("start", l.getStart().toString());
            dx.put("end", l.getEnd().toString());
            dx.put("nights", DAYS.between(l.getStart(), l.getEnd().minusDays(1)));
            ls.add(dx);
        }



        return d;
    }
}
