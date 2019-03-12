package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import io.mateu.erp.dispo.Occupancy;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.booking.CancellationTerm;
import io.mateu.erp.model.booking.Passenger;
import io.mateu.erp.model.booking.parts.HotelBooking;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.payments.BookingDueDate;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.ZoneProductRemark;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.tpv.TPVTransaction;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.world.Resort;
import io.mateu.erp.services.HotelAvailabilityStats;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            //num habs x pax por hab - edades

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

            long finalIdAgencia = idAgencia;
            Helper.notransact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    List<Hotel> hoteles = new ArrayList<>();

                    for (String s : Splitter.on(',')
                            .trimResults()
                            .omitEmptyStrings()
                            .split(resorts)) {
                        if (s.startsWith("cou")) {
                            em.find(Country.class, s.substring(4)).getDestinations().forEach(d -> d.getResorts().forEach(z -> z.getProducts().stream().filter(p -> p instanceof Hotel).forEach(p -> hoteles.add((Hotel) p))));
                        } else if (s.startsWith("des")) {
                            em.find(Destination.class, Long.parseLong(s.substring(4))).getResorts().forEach(z -> z.getProducts().stream().filter(p -> p instanceof Hotel).forEach(p -> hoteles.add((Hotel) p)));
                        } else if (s.startsWith("zon")) {
                            em.find(Resort.class, Long.parseLong(s.substring(4))).getProducts().stream().filter(p -> p instanceof Hotel).forEach(p -> hoteles.add((Hotel) p));
                        } else if (s.startsWith("hot")) {
                            hoteles.add(em.find(Hotel.class, Long.parseLong(s.substring(4))));
                        }
                    };

                    double min;
                    double max;



                    System.out.println("" + hoteles.size() + " hoteles encontrados");

                    int numContratos = 0;
                    for (Hotel h : hoteles) {
                        numContratos += h.getContracts().size();
                    }

                    System.out.println("" + numContratos + " contratos encontrados");

                    for (Hotel h : hoteles) idsHoteles.add(h.getId());

                    List<? extends Occupancy> ocups = getOccupancies(occupancies);


                    Agency a = em.find(Agency.class, finalIdAgencia);

                    HotelBooking hb = new HotelBooking();
                    hb.setAgency(a);

                    HotelBookingLine l;
                    hb.getLines().add(l = new HotelBookingLine());
                    l.setBooking(hb);
                    l.setStart(io.mateu.erp.dispo.Helper.toDate(checkIn));
                    l.setEnd(io.mateu.erp.dispo.Helper.toDate(checkOut));
                    hb.setStart(l.getStart());
                    hb.setEnd(l.getEnd());
                    l.setActive(true);
                    hb.setActive(true);


                    for (Hotel h : hoteles) {

                        AvailableHotel ah = new AvailableHotel();
                        ah.setHotelId("hot-" + h.getId());
                        ah.setHotelName(h.getName());
                        ah.setLatitude(h.getLat());
                        ah.setLongitude(h.getLon());
                        ah.setHotelCategoryId(h.getCategoryId());
                        ah.setHotelCategoryName(h.getCategoryName());
                        ah.setStars(h.getStars());
                        ah.setKeys(h.getKeys());
                        ah.setAddress("" + h.getAddress() + ", " + h.getResort().getName() + ", " + h.getResort().getDestination().getName() + " - " + h.getResort().getDestination().getCountry().getName());
                        if (h.getDataSheet() != null && h.getDataSheet().getMainImage() != null) ah.setMainImage(h.getDataSheet().getMainImage().toFileLocator().getUrl());
                        rs.getHotels().add(ah);

                        hb.setHotel(h);

                        List<HotelContract> contratosValidos = new ArrayList<>();
                        for (HotelContract c : h.getContracts()) {
                            if (c.isValidForSale(a, l.getStart(), l.getEnd())) {
                                contratosValidos.add(c);
                            }
                        }

                        if (contratosValidos.size() > 0) {

                            Collections.sort(contratosValidos, (c1, c2) -> {
                                int peso1 = getPeso(c1);
                                int peso2 = getPeso(c2);
                                return peso1 - peso2;
                            });

                            boolean allok = true;
                            double minTotal = 0;
                            double offerValueForMinTotal = 0;

                            for (Occupancy o : ocups) {
                                allok = false;

                                int adultos = o.getPaxPerRoom();
                                int ninos = 0;
                                int bebes = 0;
                                if (o.getAges() != null) {
                                    for (int e : o.getAges()) {
                                        if (e < h.getChildStartAge()) bebes++;
                                        else if (h.getJuniorStartAge() > 0 && e < h.getJuniorStartAge()) ninos++;
                                        else if (h.getAdultStartAge() > 0 && e < h.getAdultStartAge()) ninos++;
                                    }
                                    ninos = ninos / o.getNumberOfRooms();
                                    bebes = bebes / o.getNumberOfRooms();
                                    //todo: repartir mejor
                                }
                                adultos -= ninos + bebes;

                                double minValue = 0;
                                double offerValueForMinValue = 0;

                                for (Room r : h.getRooms()) {
                                    if (r.fits(adultos, ninos, bebes)) {

                                        l.setRoom(r);
                                        l.setRooms(o.getNumberOfRooms());
                                        l.setAdultsPerRoom(adultos);
                                        l.setChildrenPerRoom(ninos);
                                        l.setAges(o.getAges());

                                        for (Board b : h.getBoards()) {
                                            l.setBoard(b);

                                            for (HotelContract c : contratosValidos) {

                                                l.setContract(c);
                                                l.setInventory(c.getInventory());

                                                l.check();
                                                l.price();

                                                allok = l.isAvailable() && l.isValued();

                                                if (allok) if (minValue == 0 || minValue > l.getValue()) {
                                                    minValue = l.getValue();
                                                    offerValueForMinValue = l.getOffersValue();
                                                }

                                            }

                                        }

                                    }
                                    if (allok) break;
                                }
                                if (allok) {
                                    minTotal += minValue;
                                    offerValueForMinTotal += offerValueForMinValue;
                                }
                            }
                            if (allok) {

                                if (minTotal > 0) {
                                    ah.setBestDeal(new BestDeal());
                                    ah.getBestDeal().setRetailPrice(new Amount(a.getCurrency().getIsoCode(), Helper.roundEuros(minTotal)));
                                    if (offerValueForMinTotal != 0) {
                                        ah.getBestDeal().setOffer(true);
                                        ah.getBestDeal().setBeforeOfferPrice(new Amount(a.getCurrency().getIsoCode(), Helper.roundEuros(minTotal - offerValueForMinTotal)));
                                    }
                                }

                            }

                        }
                    }

                }

                private int getPeso(HotelContract c) {
                    int peso = 0;
                    if (ContractType.SALE.equals(c.getType())) peso += 10000;
                    if (c.getAgencies().size() > 0) peso += 1000;
                    if (c.getAgencyGroups().size() > 0) peso += 500;
                    if (c.getMarkets().size() > 0) peso += 100;
                    return peso;
                }
            });


        }

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
            hb.setAgency(em.find(Agency.class, finalIdAgencia));
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
                l.setAdultsPerRoom(adults);
                l.setChildrenPerRoom(children);
                l.setAges(o.getAges());

                l.setStart(LocalDate.parse("" + rq.getCheckin(), dfx));
                l.setEnd(LocalDate.parse("" + rq.getCheckout(), dfx));

                List<HotelContract> contratosValidos = new ArrayList<>();
                for (HotelContract c : h.getContracts()) {
                    if (c.isValidForSale(hb.getAgency(), l.getStart(), l.getEnd())) {
                        contratosValidos.add(c);
                    }
                }


                if (contratosValidos.size() > 0) for (Room r : h.getRooms()) {
                    if (r.fits(adults + juniors, children, infants)) {

                        l.setRoom(r);


                        Option op = new Option();
                        op.setAllotment(3);
                        if (r.getPhoto() != null) op.setImage(r.getPhoto().toFileLocator().getUrl());
                        op.setRoomId(r.getCode());
                        op.setRoomName(r.getType().getName().get(rq.getLanguage()));
                        if (r.getDescription() != null) op.setRoomDescription(r.getDescription().get(rq.getLanguage()));

                        for (Board b : h.getBoards()) {

                            l.setBoard(b);


                            for (HotelContract c : contratosValidos) {

                                l.setContract(c);
                                l.setInventory(c.getInventory());

                                l.check();
                                if (l.isAvailable()) {

                                    l.price();

                                    if (l.isValued()) {

                                        BoardPrice bp = new BoardPrice();
                                        bp.setBoardBasisId(b.getCode());
                                        bp.setBoardBasisName(b.getType().getName().get(rq.getLanguage()));

                                        bp.setNonRefundable(false);
                                        bp.setOffer(false);
                                        bp.setOfferText("");
                                        bp.setOnRequest(false);
                                        bp.setOnRequestText("");
                                        bp.setRateClass("");
                                        bp.setRetailPrice(new Amount(hb.getAgency().getCurrency().getIsoCode(), l.getValue()));
                                        String k = "" + hb.getAgency().getId() + "-" + hb.getHotel().getId() + "-" + hb.getStart().format(dfx) + "-" + hb.getEnd().format(dfx)+ "-" + l.getRoom().getId() + "-" + l.getBoard().getId() + "-" + l.getContract().getId() + "-" + l.getInventory().getId() + "-" + l.getRooms() + "-" + l.getAdultsPerRoom() + "-" + l.getChildrenPerRoom() + "-";
                                        if (l.getAges() != null) for (int pos = 0; pos < l.getAges().length; pos++) {
                                            if (pos > 0) k += ",";
                                            k += l.getAges()[pos];
                                        }
                                        bp.setKey(BaseEncoding.base64().encode(k.getBytes()));
                                        bp.setOffer(l.getOffersValue() != 0);
                                        bp.setOfferText(l.getAppliedOffers());

                                        op.getPrices().add(bp);
                                        break;
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
                hb.setAgency(em.find(Agency.class, finalIdAgencia));

                DateTimeFormatter dfx = DateTimeFormatter.ofPattern("yyyyMMdd");


                String[] rks = rq.getRatekeys().split("[\\,\\;\\- ]");

                List<Supplement> supls = new ArrayList<>();


                for (String rk : rks) {

                    String k = new String(BaseEncoding.base64().decode(rk));
                    String[] tks = k.split("-");




                    HotelBookingLine l;
                    hb.getLines().add(l = new HotelBookingLine());
                    l.setBooking(hb);

                    //String k = "" + hb.getAgency().getId() + "-" + hb.getHotel().getId() + "-" + l.getRoom().getId() + "-" + l.getBoard().getId() + "-" + l.getContract().getId() + "-" + l.getInventory().getId() + "-" + l.getRooms() + "-" + l.getAdultsPerRoom() + "-" + l.getChildrenPerRoom() + "-";

                    int pos = 0;
                    hb.setAgency(em.find(Agency.class, Long.parseLong(tks[pos++])));
                    hb.setHotel(em.find(Hotel.class, Long.parseLong(tks[pos++])));
                    l.setStart(LocalDate.parse(tks[pos++], dfx));
                    l.setEnd(LocalDate.parse(tks[pos++], dfx));
                    l.setRoom(em.find(Room.class, Long.parseLong(tks[pos++])));
                    l.setBoard(em.find(Board.class, Long.parseLong(tks[pos++])));
                    l.setContract(em.find(HotelContract.class, Long.parseLong(tks[pos++])));
                    l.setInventory(em.find(Inventory.class, Long.parseLong(tks[pos++])));
                    l.setRooms(Integer.parseInt(tks[pos++]));
                    l.setAdultsPerRoom(Integer.parseInt(tks[pos++]));
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


                    if (l.getContract() != null) l.getContract().getTerms().getSupplements().stream().filter(s -> s.isOptional()).filter(s -> s.getExtra() != null).filter(s -> !supls.contains(s)).forEach(s -> supls.add(s));


                    RateKey rkx;
                    rs.getRateKeys().add(rkx = new RateKey());
                    rkx.setKey(rk);
                    rkx.setPaxPerRoom(l.getAdultsPerRoom() + l.getChildrenPerRoom());
                    rkx.setRoomName(l.getRoom().getType().getName().get(rq.getLanguage()));
                    rkx.setRequestPaymentData(false);


                }

                rs.setAvailableServices(new ArrayList<>());
                supls.forEach(s -> {
                    Service x;
                    rs.getAvailableServices().add(x = new Service());
                    x.setId("" + s.getExtra().getId());
                    x.setDescription("" + s.getExtra().getDescription());
                    x.setRetailPrice(new Amount("EUR", s.getValue()));
                });

                hb.createCharges(em);
                hb.summarize(em);

                {
                    Remark rmk;
                    rs.getRemarks().add(rmk = new Remark());
                    rmk.setType("WARNING");
                    rmk.setText("Esto es una reserva de prueba");
                }

                if (!Strings.isNullOrEmpty(hb.getHotel().getResort().getDestination().getPaymentRemarks())) {
                    Remark rmk;
                    rs.getRemarks().add(rmk = new Remark());
                    rmk.setType("WARNING");
                    rmk.setText(hb.getHotel().getResort().getDestination().getPaymentRemarks());
                }

                for (ZoneProductRemark r : (List<ZoneProductRemark>)Helper.selectObjects("select x from " + ZoneProductRemark.class.getName() + " x where x.active = true")) {
                    if ((r.isActive())
                            && (r.getCountry() == null || r.getCountry().equals(hb.getHotel().getResort().getDestination().getCountry()))
                            && (r.getDestination() == null || r.getDestination().equals(hb.getHotel().getResort().getDestination()))
                            && (r.getResort() == null || r.getResort().equals(hb.getHotel().getResort()))
                            && (r.getProductType() == null || r.getProductType().equals(hb.getHotel().getType()))
                            && (r.getStart() == null || !r.getStart().isAfter(hb.getEnd()))
                            && (r.getEnd() == null || !r.getEnd().isBefore(hb.getStart()))
                            ) {
                        Remark rmk;
                        rs.getRemarks().add(rmk = new Remark());
                        rmk.setType("WARNING");
                        rmk.setText(r.getText().get(rq.getLanguage()));
                    }
                }

                int pos = 1;
                for (Charge l : hb.getCharges()) {
                    PriceLine pl;
                    rs.getPrices().add(pl = new PriceLine());
                    pl.setId("" + pos++);
                    pl.setType(l.getBillingConcept().getCode());
                    pl.setDescription(l.getText());
                    pl.setRetailPrice(new Amount(l.getCurrency().getIsoCode(), l.getTotal()));
                }

                for (BookingDueDate dd : hb.getDueDates()) {
                    if (!dd.isPaid()) {
                        PaymentLine pl;
                        rs.getPaymentLines().add(pl = new PaymentLine());
                        pl.setDate(Integer.parseInt(dd.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
                        pl.setPaymentMethod("WEB");
                        pl.setAmount(new Amount(dd.getCurrency().getIsoCode(), dd.getAmount()));
                    }
                }


                for (CancellationTerm t : hb.getCancellationTerms()) {
                    CancellationCost cc;
                    rs.getCancellationCosts().add(cc = new CancellationCost());
                    cc.setRetail(new Amount(hb.getAgency().getCurrency().getIsoCode(), t.getAmount()));
                    cc.setGMTtime(t.getDate().toString());
                }


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
            rs.setStatusCode(500);
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
        rs.setMsg("Booking confirmed");

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

                User user = em.find(User.class, u.getLogin());

                hb.setAudit(new Audit(user));
                hb.setConfirmed(true);
                hb.setAgency(em.find(Agency.class, finalIdAgencia));
                hb.setCurrency(hb.getAgency().getCurrency());
                hb.setAgencyReference(rq.getBookingReference());
                if (hb.getAgencyReference() == null) hb.setAgencyReference("");
                hb.setSpecialRequests(rq.getCommentsToProvider());
                hb.setEmail(rq.getEmail());
                hb.setLeadName(rq.getLeadName());
                hb.setPrivateComments(rq.getPrivateComments());
                hb.setPos(em.find(AuthToken.class, token).getPos());


                hb.setExpiryDate(LocalDateTime.now().plusHours(2)); // por defecto caduca a las 2 horas

                //todo: falta el cupon,


                for (BookingKey bk : rq.getRateKeys()) {
                    String rk = bk.getRateKey();

                    String k = new String(BaseEncoding.base64().decode(rk));
                    String[] tks = k.split("-");

                    HotelBookingLine l;
                    hb.getLines().add(l = new HotelBookingLine());
                    l.setBooking(hb);

                    //String k = "" + hb.getAgency().getId() + "-" + hb.getHotel().getId() + "-" + l.getRoom().getId() + "-" + l.getBoard().getId() + "-" + l.getContract().getId() + "-" + l.getInventory().getId() + "-" + l.getRooms() + "-" + l.getAdultsPerRoom() + "-" + l.getChildrenPerRoom() + "-";

                    int pos = 0;
                    hb.setAgency(em.find(Agency.class, Long.parseLong(tks[pos++])));
                    hb.setHotel(em.find(Hotel.class, Long.parseLong(tks[pos++])));
                    l.setStart(LocalDate.parse(tks[pos++], dfx));
                    l.setEnd(LocalDate.parse(tks[pos++], dfx));
                    l.setRoom(em.find(Room.class, Long.parseLong(tks[pos++])));
                    l.setBoard(em.find(Board.class, Long.parseLong(tks[pos++])));
                    l.setContract(em.find(HotelContract.class, Long.parseLong(tks[pos++])));
                    l.setInventory(em.find(Inventory.class, Long.parseLong(tks[pos++])));
                    l.setRooms(Integer.parseInt(tks[pos++]));
                    l.setAdultsPerRoom(Integer.parseInt(tks[pos++]));
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


                for (BookingKey bk : rq.getRateKeys()) {
                    if (bk.getOccupancy() != null) for (PaxDetails pd : bk.getOccupancy()) {
                        Passenger p;
                        hb.getPassengers().add(p = new Passenger());
                        p.setBooking(hb);
                        p.setAge(pd.getAge());
                        p.setFirstName(pd.getName());
                        p.setSurname(pd.getSurname());

                        if (Strings.isNullOrEmpty(p.getFirstName())) p.setFirstName("N/A");
                        if (Strings.isNullOrEmpty(p.getSurname())) p.setSurname("N/A");
                    }
                }


                if (hb.getPos().getTpv() != null) {
                    TPVTransaction t;
                    hb.getTPVTransactions().add(t = new TPVTransaction());
                    t.setBooking(hb);
                    t.setTpv(hb.getPos().getTpv());
                    t.setSubject("Booking " + hb.getLeadName());
                    t.setValue(Helper.roundEuros(hb.getTotalValue() / 2));
                    t.setLanguage("es");
                    t.setCurrency(hb.getCurrency());
                }


                em.persist(hb);

            });


            rs.setBookingId("" + hb.getId());
            if (hb.getTPVTransactions().size() > 0) Helper.notransact(em -> {
                rs.setPaymentUrl(hb.getTPVTransactions().get(0).getBoton(em));
            });
            else rs.setPaymentUrl("");
            //rs.setAvailableServices(""); // todo: añadir servicios adicionales que podemos reservar


            long t = System.currentTimeMillis();

            String msg = "Booking confirmed with id " + rs.getBookingId() + ". It consumed " + (t - t0) + " ms in the server.";

            System.out.println(msg);

            rs.setMsg(msg);


            if (!Strings.isNullOrEmpty(hb.getEmail())) {
                try {
                    Helper.transact(em -> {
                        em.find(HotelBooking.class, hb.getId()).sendBooked(em, null, null);
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }


        } catch (Throwable throwable) {
            rs.setStatusCode(500);
            rs.setMsg("" + throwable.getClass().getName() + ":" + throwable.getMessage());
            throwable.printStackTrace();
        }




        return rs;
    }


    @Override
    public GetAvailableHotelsRS getFilteredHotels(String token, String language, String resorts, int checkIn, int checkOut, String occupancies, String categories, double minPrice, double maxPrice) throws Throwable {
        GetAvailableHotelsRS rs = getAvailableHotels(token, language, resorts, checkIn, checkOut, occupancies, true);
        if (!Strings.isNullOrEmpty(categories)) {
            List<String> catIds = Lists.newArrayList(categories.split(","));
            List<Integer> stars = new ArrayList<>();
            catIds.forEach(s -> stars.add(Integer.parseInt(s)));
            rs.setHotels(rs.getHotels().stream().filter(h -> stars.contains(h.getStars()) || stars.contains(h.getKeys())).collect(Collectors.toList()));
        }
        if (minPrice != 0) {
            rs.setHotels(rs.getHotels().stream().filter(h -> h.getBestDeal() != null && h.getBestDeal().getRetailPrice() != null && h.getBestDeal().getRetailPrice().getValue() >= minPrice).collect(Collectors.toList()));
        }
        if (maxPrice != 0) {
            rs.setHotels(rs.getHotels().stream().filter(h -> h.getBestDeal() != null && h.getBestDeal().getRetailPrice() != null && h.getBestDeal().getRetailPrice().getValue() <= maxPrice).collect(Collectors.toList()));
        }
        return rs;
    }

}
