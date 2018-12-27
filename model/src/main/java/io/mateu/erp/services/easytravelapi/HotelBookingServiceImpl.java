package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import io.mateu.erp.dispo.*;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.parts.HotelBooking;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.product.hotel.Board;
import io.mateu.erp.model.product.hotel.Inventory;
import io.mateu.erp.model.product.hotel.Room;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Zone;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.services.HotelAvailabilityStats;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import org.easytravelapi.HotelBookingService;
import org.easytravelapi.common.*;
import org.easytravelapi.hotel.*;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 27/7/17.
 */
public class HotelBookingServiceImpl implements HotelBookingService {

    private void recordStats(GetAvailableHotelsRS rs, long checkin, long checkout) {

        double totalPrice = 0;
        double avgPrice = 0;
        double minPrice = 0;
        double maxPrice = 0;
        long pricesNo = 0;
        long hotelsNo = rs.getHotels().size();


        for (AvailableHotel h : rs.getHotels()) if (h.getBestDeal() != null) {

            double price = h.getBestDeal().getRetailPrice().getValue();

            avgPrice = (avgPrice * pricesNo + price) / (pricesNo + 1);
            if (minPrice > price) minPrice = price;
            if (maxPrice < price) maxPrice = price;

            pricesNo++;
        }

        HotelAvailabilityStats.add(avgPrice, minPrice, maxPrice, pricesNo, hotelsNo, checkin, checkout, checkout - checkin);
    }

    public static List<? extends Occupancy> getOccupancies(String occupancies) {
        List<Occupancy> l = new ArrayList<>();
        for (String s : Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(occupancies.toLowerCase())) {

            //1x4-4-8

            //System.out.println("occ=" + s);


            String[] sas = s.split("-");
            List<Integer> edades = new ArrayList<Integer>();
            if (sas.length > 1) {
                for (int i = 1; i < sas.length; i++) {
                    int edad = Integer.parseInt(sas[i]);
                    if (edad < 20) {
                        edades.add(edad);
                    }
                }
            }

            int[] edadesx = new int[edades.size()];
            for (int i = 0; i < edadesx.length; i++) edadesx[i] = edades.get(i);
            l.add(new Occupancy(Integer.parseInt(s.split("x")[0]), Integer.parseInt(s.split("x")[1].split("-")[0]), edadesx));

        }
        return l;
    }

    @Override
    public GetAvailableHotelsRS getAvailableHotels(String token, String language, String resorts, int checkIn, int checkOut, String occupancies, boolean includeStaticInfo) throws Throwable {
        GetAvailableHotelsRS rs = new GetAvailableHotelsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        long idPos = Long.parseLong(System.getProperty("idpos", "1"));

        System.out.println("available hotels. token = " + token);

        LocalDate formalizationDate = LocalDate.now();

        long idAgencia = 0;
        long idHotel = 0;
        String login = "";
        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(token)));
            if (!Strings.isNullOrEmpty(creds.getAgentId())) idAgencia = Long.parseLong(creds.getAgentId());
            if (!Strings.isNullOrEmpty(creds.getHotelId())) idHotel = Long.parseLong(creds.getHotelId());
            //rq.setLanguage(creds.getLan());
            login = creds.getLogin();
            //rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long t0 = System.currentTimeMillis();

        List<Long> idsHoteles = new ArrayList<>();

        if (idHotel > 0) idsHoteles.add(idHotel);
        else {

            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    List<Hotel> hoteles = new ArrayList<>();

                    for (String s : Splitter.on(',')
                            .trimResults()
                            .omitEmptyStrings()
                            .split(resorts)) {
                        if (s.startsWith("cou")) {
                            em.find(Country.class, s.substring(4)).getDestinations().forEach(d -> d.getZones().forEach(z -> hoteles.addAll(z.getHotels())));
                        } else if (s.startsWith("des")) {
                            em.find(Destination.class, Long.parseLong(s.substring(4))).getZones().forEach(z -> hoteles.addAll(z.getHotels()));
                        } else if (s.startsWith("zon")) {
                            hoteles.addAll(em.find(Zone.class, Long.parseLong(s.substring(4))).getHotels());
                        } else if (s.startsWith("hot")) {
                            hoteles.add(em.find(Hotel.class, Long.parseLong(s.substring(4))));
                        }
                    };



                    System.out.println("" + hoteles.size() + " hoteles encontrados");

                    int numContratos = 0;
                    for (Hotel h : hoteles) {
                        numContratos += h.getContracts().size();
                    }

                    System.out.println("" + numContratos + " contratos encontrados");

                    for (Hotel h : hoteles) idsHoteles.add(h.getId());
                }
            });


        }

        long finalIdAgencia = idAgencia;
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = new ArrayList<>();
                for (long idHotel : idsHoteles) {
                    hoteles.add(em.find(Hotel.class, idHotel));
                }

                Partner a = em.find(Partner.class, finalIdAgencia);

                //System.out.println("" + hoteles.size() + " hoteles encontrados");

                ModeloDispo modelo = new ModeloDispo() {
                    @Override
                    public IHotelContract getHotelContract(long id) {
                        return em.find(HotelContract.class, id);
                    }
                };

                DispoRQ rq = new DispoRQ(formalizationDate, checkIn, checkOut, getOccupancies(occupancies), includeStaticInfo);


                for (Hotel h : hoteles) {
                    AvailableHotel ah = new HotelAvailabilityRunner().check(a, h, finalIdAgencia, idPos, modelo, rq);
                    if (ah != null) {
                        rs.getHotels().add(ah);
                        ah.setAddress("" + h.getAddress() + ", " + h.getZone().getName() + ", " + h.getZone().getDestination().getName() + " - " + h.getZone().getDestination().getCountry().getName());
                        if (h.getDataSheet() != null && h.getDataSheet().getMainImage() != null) ah.setMainImage(h.getDataSheet().getMainImage().toFileLocator().getUrl());
                    }
                }


                //System.out.println(Helper.toJson(dispo));

            }
        });

        long t = System.currentTimeMillis();

        recordStats(rs, io.mateu.erp.dispo.Helper.toDate(checkIn).toEpochDay(), io.mateu.erp.dispo.Helper.toDate(checkOut).toEpochDay());

        long t1 = System.currentTimeMillis();

        String msg = "" + rs.getHotels().size() + " hotels returned. It consumed " + (t - t0) + " ms in the server. Stats took " + (t1 - t) + " ms.";

        System.out.println(msg);

        rs.setMsg(msg);

        return rs;
    }

    @Override
    public GetHotelRatesRS getRates(String token, GetHotelRatesRQ rq) throws Throwable {
        GetHotelRatesRS rs = new GetHotelRatesRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        DateTimeFormatter dfx = DateTimeFormatter.ofPattern("yyyyMMdd");

        long idPos = Long.parseLong(System.getProperty("idpos", "1"));

        LocalDate formalizationDate = LocalDate.now();

        long idAgencia = 0;
        long idHotel = 0;
        String login = "";
        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(token)));
            if (!Strings.isNullOrEmpty(creds.getAgentId())) idAgencia = Long.parseLong(creds.getAgentId());
            if (!Strings.isNullOrEmpty(creds.getHotelId())) idHotel = Long.parseLong(creds.getHotelId());
            //rq.setLanguage(creds.getLan());
            login = creds.getLogin();
            //rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long t0 = System.currentTimeMillis();


        long finalIdAgencia = idAgencia;
        Helper.notransact(em -> {
            Hotel h = em.find(Hotel.class, Long.parseLong(rq.getHotelId().substring("hot-".length())));


            HotelBooking hb = new HotelBooking();
            hb.setAgency(em.find(Partner.class, finalIdAgencia));
            hb.setHotel(h);

            HotelBookingLine l;
            hb.getLines().add(l = new HotelBookingLine());
            l.setBooking(hb);

            for (Occupancy o : getOccupancies(rq.getOccupancies())) {

                Allocation a;
                rs.getRates().add(a = new Allocation());
                a.setNumberOfRooms(o.getNumberOfRooms());
                a.setPaxPerRoom(o.getPaxPerRoom());
                a.setAges(o.getAges());

                int infants = 0;
                int children = 0;
                int juniors = 0;
                int adults = 0;
                if (o.getAges() != null) for (int i = 0; i < o.getAges().length; i++) {
                    if (o.getAges()[i] < h.getChildStartAge()) infants++;
                    else if (o.getAges()[i] < h.getJuniorStartAge()) children++;
                    else if (o.getAges()[i] < h.getAdultStartAge()) {
                        if (h.getJuniorStartAge() > 0) juniors++;
                        else children++;
                    }
                }
                infants = infants / o.getNumberOfRooms();
                children = children / o.getNumberOfRooms();
                juniors = juniors / o.getNumberOfRooms(); // todo: repartir mejor (ir distribuyendo los bebes, niños, juniors)

                adults = o.getPaxPerRoom() - juniors - children - infants;


                l.setRooms(o.getNumberOfRooms());
                l.setAdultsPerRoon(adults);
                l.setChildrenPerRoom(children);
                l.setAges(o.getAges());

                l.setStart(LocalDate.parse("" + rq.getCheckin(), dfx));
                l.setEnd(LocalDate.parse("" + rq.getCheckout(), dfx));


                for (Room r : h.getRooms()) {
                    if (r.fits(adults + juniors, children, infants)) {

                        l.setRoom(r);


                        Option op = new Option();
                        op.setAllotment(3);
                        if (r.getPhoto() != null) op.setImage(r.getPhoto().toFileLocator().getUrl());
                        op.setRoomId(r.getCode());
                        op.setRoomName(r.getType().getName().get(rq.getLanguage()));
                        op.setRoomDescription(r.getDescription().get(rq.getLanguage()));

                        for (Board b : h.getBoards()) {

                            l.setBoard(b);


                            BoardPrice bp = new BoardPrice();
                            bp.setBoardBasisId(b.getCode());
                            bp.setBoardBasisName(b.getType().getName().get(rq.getLanguage()));


                            for (HotelContract c : h.getContracts()) {

                                l.setContract(c);
                                l.setInventory(c.getInventory());

                                l.check();
                                if (l.isAvailable()) {

                                    l.price();

                                    if (l.isValued()) {

                                        bp.setNonRefundable(false);
                                        bp.setOffer(false);
                                        bp.setOfferText("");
                                        bp.setOnRequest(false);
                                        bp.setOnRequestText("");
                                        bp.setRateClass("");
                                        bp.setRetailPrice(new Amount(hb.getAgency().getCurrency().getIsoCode(), l.getValue()));
                                        String k = "" + hb.getAgency().getId() + "-" + hb.getHotel().getId() + "-" + hb.getStart().format(dfx) + "-" + hb.getEnd().format(dfx)+ "-" + l.getRoom().getId() + "-" + l.getBoard().getId() + "-" + l.getContract().getId() + "-" + l.getInventory().getId() + "-" + l.getRooms() + "-" + l.getAdultsPerRoon() + "-" + l.getChildrenPerRoom() + "-";
                                        if (l.getAges() != null) for (int pos = 0; pos < l.getAges().length; pos++) {
                                            if (pos > 0) k += ",";
                                            k += l.getAges()[pos];
                                        }
                                        bp.setKey(BaseEncoding.base64().encode(k.getBytes()));

                                        op.getPrices().add(bp);
                                    }

                                }
                            }

                        }

                        if (op.getPrices().size() > 0) {
                            a.getOptions().add(op);
                        }
                    }
                }

            }


        });





        long t = System.currentTimeMillis();

        //recordStats(rs, io.mateu.erp.dispo.Helper.toDate(rq.getCheckin()).toEpochDay(), io.mateu.erp.dispo.Helper.toDate(rq.getCheckout()).toEpochDay());

        long t1 = System.currentTimeMillis();

        String msg = "" + rs.getRates().size() + " rates returned. It consumed " + (t - t0) + " ms in the server. Stats took " + (t1 - t) + " ms.";

        System.out.println(msg);

        rs.setMsg(msg);

        return rs;
    }

    @Override
    public GetHotelPriceDetailsRS getHotelPriceDetails(String token, GetHotelPriceDetailsRQ rq) throws Throwable {
        GetHotelPriceDetailsRS rs = new GetHotelPriceDetailsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Price details");

        long idPos = Long.parseLong(System.getProperty("idpos", "1"));

        long idAgencia = 0;
        String login = "";
        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(token)));
            if (!Strings.isNullOrEmpty(creds.getAgentId())) idAgencia = Long.parseLong(creds.getAgentId());
            login = creds.getLogin();
            //rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long t0 = System.currentTimeMillis();

        long finalIdAgencia = idAgencia;
        try {
            Helper.notransact((JPATransaction) (em) -> {



                HotelBooking hb = new HotelBooking();
                hb.setAgency(em.find(Partner.class, finalIdAgencia));

                DateTimeFormatter dfx = DateTimeFormatter.ofPattern("yyyyMMdd");


                String[] rks = rq.getRatekeys().split("[\\,\\;\\- ]");


                for (String rk : rks) {

                    String k = new String(BaseEncoding.base64().decode(rk));
                    String[] tks = k.split("-");




                    HotelBookingLine l;
                    hb.getLines().add(l = new HotelBookingLine());
                    l.setBooking(hb);

                    //String k = "" + hb.getAgency().getId() + "-" + hb.getHotel().getId() + "-" + l.getRoom().getId() + "-" + l.getBoard().getId() + "-" + l.getContract().getId() + "-" + l.getInventory().getId() + "-" + l.getRooms() + "-" + l.getAdultsPerRoon() + "-" + l.getChildrenPerRoom() + "-";

                    int pos = 0;
                    hb.setAgency(em.find(Partner.class, Long.parseLong(tks[pos++])));
                    hb.setHotel(em.find(Hotel.class, Long.parseLong(tks[pos++])));
                    l.setStart(LocalDate.parse(tks[pos++], dfx));
                    l.setEnd(LocalDate.parse(tks[pos++], dfx));
                    l.setRoom(em.find(Room.class, Long.parseLong(tks[pos++])));
                    l.setBoard(em.find(Board.class, Long.parseLong(tks[pos++])));
                    l.setContract(em.find(HotelContract.class, Long.parseLong(tks[pos++])));
                    l.setInventory(em.find(Inventory.class, Long.parseLong(tks[pos++])));
                    l.setRooms(Integer.parseInt(tks[pos++]));
                    l.setAdultsPerRoon(Integer.parseInt(tks[pos++]));
                    l.setChildrenPerRoom(Integer.parseInt(tks[pos++]));
                    if (tks.length > pos) {
                        String tkx = tks[pos++];
                        int[] ages = null;
                        if (tks.length > 8 && !Strings.isNullOrEmpty(tkx)) {
                            String[] ags = tkx.split(",");
                            ages = new int[ags.length];
                            for (int i = 0; i < ags.length; i++) {
                                if (!Strings.isNullOrEmpty(ags[i])) ages[i] = Integer.parseInt(ags[i]);
                            }
                        }
                        l.setAges(ages);
                    }



                    l.check();
                    l.price();


                    RateKey rkx;
                    rs.getRateKeys().add(rkx = new RateKey());
                    rkx.setKey(rk);
                    rkx.setPaxPerRoom(l.getAdultsPerRoon() + l.getChildrenPerRoom());
                    rkx.setRoomName(l.getRoom().getType().getName().get(rq.getLanguage()));
                    rkx.setRequestPaymentData(false);


                }

                hb.summarize(em);

                CancellationCost cc;
                rs.getCancellationCosts().add(cc = new CancellationCost());
                cc.setRetail(new Amount(hb.getAgency().getCurrency().getIsoCode(), Helper.roundEuros(hb.getTotalValue() * 0.3d)));
                cc.setGMTtime(hb.getEnd().minusDays(7).toString());

                Remark rmk;
                rs.getRemarks().add(rmk = new Remark());
                rmk.setType("WARNING");
                rmk.setText("Esto es una reserva de prueba");

                int pos = 1;
                for (Charge l : hb.getCharges()) {
                    PriceLine pl;
                    rs.getPrices().add(pl = new PriceLine());
                    pl.setId("" + pos++);
                    pl.setType(l.getBillingConcept().getCode());
                    pl.setDescription(l.getText());
                    pl.setRetailPrice(new Amount(l.getTotal().getCurrency().getIsoCode(), l.getTotal().getValue()));
                }

                {
                    PaymentLine pl;
                    rs.getPaymentLines().add(pl = new PaymentLine());
                    pl.setDate(Integer.parseInt(hb.getStart().minusDays(7).format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
                    pl.setPaymentMethod("WEB");
                    pl.setAmount(new Amount(hb.getAgency().getCurrency().getIsoCode(), Helper.roundEuros(hb.getTotalValue() * 0.5d)));

                    rs.getPaymentLines().add(pl = new PaymentLine());
                    pl.setDate(Integer.parseInt(hb.getStart().minusDays(0).format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
                    pl.setPaymentMethod("HOTEL");
                    pl.setAmount(new Amount(hb.getAgency().getCurrency().getIsoCode(), Helper.roundEuros(hb.getTotalValue() * 0.5d)));
                }


                //rs.setAvailableServices(); //todo: comprobar si esto sobra

                rs.setTotal(new BestDeal());
                rs.getTotal().setRetailPrice(new Amount(hb.getAgency().getCurrency().getIsoCode(), hb.getTotalValue()));
                rs.setStatus(hb.isAvailable()?"OK":"ON REQUEST");
                rs.setCouponMsg("");

            });
            long t = System.currentTimeMillis();

            String msg = "Price details. It consumed " + (t - t0) + " ms in the server.";

            System.out.println(msg);

            rs.setMsg(msg);

        } catch (Throwable throwable) {
            rs.setStatusCode(200);
            rs.setMsg("" + throwable.getClass().getName() + ":" + throwable.getMessage());
            throwable.printStackTrace();
        }




        return rs;
    }

    @Override
    public BookHotelRS bookHotel(String token, BookHotelRQ rq) {

        BookHotelRS rs = new BookHotelRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("File confirmed");

        long idPos = Long.parseLong(System.getProperty("idpos", "1"));

        long idAgencia = 0;
        final UserData u = new UserData();
        String login = "";
        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(token)));
            idAgencia = Long.parseLong(creds.getAgentId());
            //rq.setLanguage(creds.getLan());
            login = creds.getLogin();
            u.setLogin(login);
            //rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long t0 = System.currentTimeMillis();

        long finalIdAgencia = idAgencia;
        try {

            HotelBooking hb = new HotelBooking();

            Helper.transact((JPATransaction) (em) -> {

                DateTimeFormatter dfx = DateTimeFormatter.ofPattern("yyyyMMdd");

                hb.setAgency(em.find(Partner.class, finalIdAgencia));
                hb.setAgencyReference(rq.getBookingReference());
                hb.setSpecialRequests(rq.getCommentsToProvider());
                hb.setEmail(rq.getEmail());
                hb.setLeadName(rq.getLeadName());
                hb.setPrivateComments(rq.getPrivateComments());
                hb.setPos(em.find(AuthToken.class, token).getPos());

                //todo: faltan los pasajeros, el cupon,


                for (BookingKey bk : rq.getRateKeys()) {
                    String rk = bk.getRateKey();

                    String k = new String(BaseEncoding.base64().decode(rk));
                    String[] tks = k.split("-");


                    HotelBookingLine l;
                    hb.getLines().add(l = new HotelBookingLine());
                    l.setBooking(hb);

                    //String k = "" + hb.getAgency().getId() + "-" + hb.getHotel().getId() + "-" + l.getRoom().getId() + "-" + l.getBoard().getId() + "-" + l.getContract().getId() + "-" + l.getInventory().getId() + "-" + l.getRooms() + "-" + l.getAdultsPerRoon() + "-" + l.getChildrenPerRoom() + "-";

                    int pos = 0;
                    hb.setAgency(em.find(Partner.class, Long.parseLong(tks[pos++])));
                    hb.setHotel(em.find(Hotel.class, Long.parseLong(tks[pos++])));
                    l.setStart(LocalDate.parse(tks[pos++], dfx));
                    l.setEnd(LocalDate.parse(tks[pos++], dfx));
                    l.setRoom(em.find(Room.class, Long.parseLong(tks[pos++])));
                    l.setBoard(em.find(Board.class, Long.parseLong(tks[pos++])));
                    l.setContract(em.find(HotelContract.class, Long.parseLong(tks[pos++])));
                    l.setInventory(em.find(Inventory.class, Long.parseLong(tks[pos++])));
                    l.setRooms(Integer.parseInt(tks[pos++]));
                    l.setAdultsPerRoon(Integer.parseInt(tks[pos++]));
                    l.setChildrenPerRoom(Integer.parseInt(tks[pos++]));
                    if (tks.length > pos) {
                        String tkx = tks[pos++];
                        int[] ages = null;
                        if (tks.length > 8 && !Strings.isNullOrEmpty(tkx)) {
                            String[] ags = tkx.split(",");
                            ages = new int[ags.length];
                            for (int i = 0; i < ags.length; i++) {
                                if (!Strings.isNullOrEmpty(ags[i])) ages[i] = Integer.parseInt(ags[i]);
                            }
                        }
                        l.setAges(ages);
                    }



                    l.check();
                    l.price();
                }

                hb.summarize(em);

                em.persist(hb);


            });


            rs.setBookingId("" + hb.getId());
            rs.setPaymentUrl(""); //todo: añadir url pago
            //rs.setAvailableServices(""); // todo: añadir servicios adicionales que podemos reservar


            long t = System.currentTimeMillis();

            String msg = "Booking confirmed with id " + rs.getBookingId() + ". It consumed " + (t - t0) + " ms in the server.";

            System.out.println(msg);

            rs.setMsg(msg);

        } catch (Throwable throwable) {
            rs.setStatusCode(500);
            rs.setMsg("" + throwable.getClass().getName() + ":" + throwable.getMessage());
            throwable.printStackTrace();
        }




        return rs;
    }

    @Override
    public GetAvailableHotelsRS getFilteredHotels(String s, String s1, String s2, int i, int i1, String s3, List<String> list, String s4, String s5) throws Throwable {
        return null;
    }

}
