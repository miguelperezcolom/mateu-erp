package io.mateu.erp.tests;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.quonext.quoon.Agent;
import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.multilanguage.Literal;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.population.Populator;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.DiscountOffer;
import io.mateu.erp.model.product.hotel.offer.Per;
import io.mateu.erp.model.product.hotel.offer.Scope;
import io.mateu.erp.model.product.transfer.*;
import io.mateu.erp.model.world.City;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.State;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TestPopulator {

    public static void main(String... args) throws Throwable {

        System.setProperty("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/quotest");


        populateAll();

    }

    public static void populateAll() throws Throwable {

        Helper.transact((JPATransaction) (em) -> {

            int nomappconfigs = em.createQuery("select x from " + AppConfig.class.getName() + " x").getResultList().size();

            if (nomappconfigs == 0) Populator.populate();

        });

        TestPopulator p = new TestPopulator();

        Helper.transact((JPATransaction) (em) -> {

            int numhots = em.createQuery("select x from " + Hotel.class.getName() + " x").getResultList().size();

            if (numhots > 0) throw new Exception("Can not populate with test data if there are already hotels in the database!");

        });


        p.populateActors();

        p.populatePortfolio();

        p.populateTransferProduct();

        p.populateRoomsBoardsAndHotelCategories();

        p.populateHotels();

        p.populateStopSales();

        p.populateInventory();

        p.populateContracts();

        p.populateOffers();

        p.populateBookings();

        p.populateAgents();

        p.populateAuthTokens();


        //p.testContract();

        //p.testJaxb();

        //p.testJson();
    }

    private void populateAuthTokens() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                {
                    AuthToken t = new AuthToken();
                    t.setId("ewogICJhZ2VudElkIiA6ICIzIiwKICAicGFzcyIgOiAidzM4a1dwIiwKICAibGFuIiA6ICJlbiIsCiAgImxvZ2luIiA6ICJERU1PIFhNTCBBR0VOVCIKfQ==");
                    t.setActive(true);
                    t.setUser(em.find(User.class, "admin"));
                    t.setActor(em.find(Actor.class, 3l));
                    em.persist(t);
                }


                {
                    AuthToken t = new AuthToken();
                    t.setId("eyAnY3JlYXRlZCc6ICdXZWQgTm92IDA4IDEyOjA4OjIxIENFVCAyMDE3JywgJ3VzZXJJZCc6ICdhZG1pbicsICdhY3RvcklkJzogMywgJ2hvdGVsSWQnOiAxMn0=");
                    t.setActive(true);
                    t.setUser(em.find(User.class, "admin"));
                    t.setActor(em.find(Actor.class, 3l));
                    t.setHotel(em.find(Hotel.class, 12l));
                    em.persist(t);
                }

            }
        });

    }

    private void populateAgents() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                Agent a = new Agent();
                a.setActive(true);
                a.setDownloadQueue("toQuoON");
                a.setMQHost("mq.quoon.net");
                a.setMQPassword("ramon123");
                a.setMQUser("ramon");
                a.setName("Test agent");
                a.setOffice(em.find(Office.class, 1l));
                a.setProvider(em.find(Actor.class, 4l));
                a.getProvider().setAgent(a);
                a.setUploadQueue("FromQuoOn");
                em.persist(a);
            }
        });

    }

    private void populateTransferProduct() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                Random random = new Random();

                User u = em.find(User.class, Populator.USER_ADMIN);

                final Vehicle taxi = new Vehicle();
                taxi.setMinPax(1);
                taxi.setMaxPax(4);
                taxi.setName("TAXI 1-4");
                em.persist(taxi);

                final Vehicle shuttle = new Vehicle();
                shuttle.setMinPax(1);
                shuttle.setMaxPax(99);
                shuttle.setName("SHUTTLE BUS 1-99");
                em.persist(shuttle);

                Actor islandbus = null;
                Actor nosotros = null;

                for (Actor a : (List<Actor>) em.createQuery("select s from " + Actor.class.getName() + " s").getResultList()) {
                    if (a.getName().toLowerCase().contains("nosotros")) nosotros = a;
                    if (a.getName().toLowerCase().contains("islandbus")) islandbus = a;
                }

                BillingConcept bc = (BillingConcept) em.createQuery("select c from " + BillingConcept.class.getName() + " c").getResultList().get(0);


                for (Country cx : (List<Country>) em.createQuery("select c from " + Country.class.getName() + " c order by c.name").getResultList()) {

                    for (State s : cx.getStates()) {

                        List<Zone> zonas = new ArrayList<>();
                        for (City l : s.getCities()) {
                            Zone z = new Zone();
                            em.persist(z);
                            z.setName(l.getName());
                            z.getCities().add(l);
                            zonas.add(z);
                        }

                        for (TransferType tt : new TransferType[] {TransferType.PRIVATE, TransferType.SHUTTLE}) for (boolean compra : new boolean[] {true, false}) {
                            Contract c = new Contract();
                            em.persist(c);

                            c.setAudit(new Audit(u));
                            c.setPrivateComments("Test");
                            c.setSpecialTerms("--");
                            c.setSupplier((compra)?islandbus:nosotros);
                            c.setTitle("CONTRATO TRASLADOS " + ((compra)?"COMPRA":"VENTA"));
                            c.setType((compra)?ContractType.PURCHASE:ContractType.SALE);
                            c.setTransferType(tt);
                            c.setValidFrom(LocalDate.of(2017, 1, 1));
                            c.setValidTo(LocalDate.of(2020, 12, 31));
                            c.setVATIncluded(true);
                            c.setBillingConcept(bc);

                            for (Zone de : zonas) for (Zone a : zonas) if (!de.equals(a)){
                                Price p = new Price();
                                c.getPrices().add(p);
                                em.persist(p);
                                p.setContract(c);
                                p.setDestination(a);
                                p.setOrigin(de);
                                if (TransferType.PRIVATE.equals(tt)) {
                                    p.setPrice(Helper.roundEuros(30d + 20d * random.nextDouble()));
                                    p.setPricePer(PricePer.SERVICE);
                                    p.setVehicle(taxi);
                                } else { // shuttle
                                    p.setPrice(Helper.roundEuros(5d + 10d * random.nextDouble()));
                                    p.setPricePer(PricePer.PAX);
                                    p.setVehicle(shuttle);
                                }
                            }
                        }

                    }


                }



            }
        });

    }

    private void populatePortfolio() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                String json = CharStreams.toString(new InputStreamReader(
                        this.getClass().getResourceAsStream("portfolio.json"), Charsets.UTF_8));


                Map<String, Object> m = Helper.fromJson(json);


                for (Map<String, Object> dc : (List<Map<String, Object>>) m.get("countries")) {

                    Country c = new Country();
                    c.setIsoCode((String) dc.get("id"));
                    c.setName((String) dc.get("name"));
                    em.persist(c);

                    em.flush();

                    for (Map<String, Object> ds : (List<Map<String, Object>>) dc.get("states")) {

                        State s = new State();
                        s.setName((String) ds.get("name"));
                        c.getStates().add(s);
                        s.setCountry(c);
                        em.persist(s);

                        em.flush();

                        for (Map<String, Object> dl : (List<Map<String, Object>>) ds.get("cities")) {

                            City l = new City();
                            l.setName((String) dl.get("name"));
                            s.getCities().add(l);
                            l.setState(s);
                            em.persist(l);

                            em.flush();

                            for (Map<String, Object> dtp : (List<Map<String, Object>>) dl.get("transferpoints")) {

                                TransferPoint p = new TransferPoint();
                                p.setName((String) dtp.get("name"));
                                p.setInstructions("---");
                                p.setType(TransferPointType.valueOf((String) dtp.get("type")));
                                l.getTransferPoints().add(p);
                                p.setCity(l);
                                em.persist(p);

                                em.flush();

                            }



                        }
                    }

                }


            }
        });


    }

    private void testJson() throws IOException {

        String json = CharStreams.toString(new InputStreamReader(
                this.getClass().getResourceAsStream("portfolio.json"), Charsets.UTF_8));

        Map<String, Object> m = Helper.fromJson(json);


        System.out.println("hecho!");
    }

    private void populateBookings() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                Currency eur = em.find(Currency.class, "EUR");

                Actor agencia = null;
                Actor proveedor = null;


                for (Actor a : (List<Actor>) em.createQuery("select s from " + Actor.class.getName() + " s").getResultList()) {
                    if (a.getName().toLowerCase().contains("muchoviaje")) agencia = a;
                    if (a.getName().toLowerCase().contains("islandbus")) proveedor = a;
                }

                User u = em.find(User.class, Populator.USER_ADMIN);

                TransferPoint ibz = null;
                TransferPoint hotel = null;
                for (TransferPoint tp : (List<TransferPoint>) em.createQuery("select s from " + TransferPoint.class.getName() + " s").getResultList()) {
                    if (tp.getName().contains("IBZ")) ibz = tp;
                    else if (tp.getName().equals("Hotel Hard Rock Ibiza")) hotel = tp;
                }

                Office oficina = (Office) em.createQuery("select s from " + Office.class.getName() + " s").getResultList().get(0);
                PointOfSale pos = (PointOfSale) em.createQuery("select s from " + PointOfSale.class.getName() + " s").getResultList().get(0);


                for (int i = 0; i < 10; i++) {

                    Booking b = new Booking();
                    em.persist(b);
                    b.setAgency(agencia);
                    b.setTelephone("629602085");
                    b.setStart(LocalDate.of(2017, 6, 1));
                    b.setLeadName("Mrs Test " + i);
                    b.setFinish(LocalDate.of(2017, 6, 15));
                    b.setEmail("miguelperezcolom@gmail.com");
                    b.setCurrency(eur);
                    b.setConfirmed(true);
                    b.setAudit(new Audit(u));
                    b.setAgencyReference("AXS" + i);
                    b.setCancelled(false);
                    b.setComments("Test booking created b test populator");
                    b.afterSet(em, true);

                    //ida
                    {
                        TransferService s = new TransferService();
                        em.persist(s);
                        b.getServices().add(s);
                        s.setBooking(b);
                        s.setStart(b.getStart());
                        s.setComment("Incoming transfer test");
                        //s.setOverridedValue();
                        s.setFinish(s.getStart());
                        s.setAudit(new Audit(u));
                        s.setTransferType(TransferType.SHUTTLE);
                        s.setPax(2);
                        s.setFlightTime(s.getStart().atTime(12, 30));
                        s.setFlightOriginOrDestination("MAD");
                        s.setFlightNumber("IB1234");
                        s.setDropoff(hotel);
                        s.setPickup(ibz);
                        s.setOffice(oficina);
                        s.setPos(pos);
                        s.setPrivateComment("VIP");
                        s.setSentToProvider(LocalDateTime.now());
                        s.setAlreadyPurchased(true);
                        s.setAlreadyInvoiced(true);
                        s.afterSet(em, true);
                    }


                    //vuelta
                    {
                        TransferService s = new TransferService();
                        em.persist(s);
                        b.getServices().add(s);
                        s.setBooking(b);
                        s.setStart(b.getFinish());
                        s.setComment("Outgoing transfer test");
                        //s.setOverridedValue();
                        s.setFinish(s.getStart());
                        s.setAudit(new Audit(u));
                        s.setTransferType(TransferType.SHUTTLE);
                        s.setPax(2);
                        s.setFlightTime(s.getStart().atTime(17, 15));
                        s.setFlightOriginOrDestination("MAD");
                        s.setFlightNumber("BA4321");
                        s.setDropoff(ibz);
                        s.setPickup(hotel);
                        s.setOffice(oficina);
                        s.setPos(pos);
                        s.setPrivateComment("VIP");
                        s.setSentToProvider(LocalDateTime.now());
                        s.setAlreadyPurchased(true);
                        s.setAlreadyInvoiced(true);
                        s.afterSet(em, true);
                    }


                }

            }
        });

    }

    private void populateActors() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {


                {
                    Actor a = new Actor();
                    em.persist(a);
                    a.setAddress("Gremi fusters, 11");
                    a.setAutomaticOrderConfirmation(false);
                    a.setAutomaticOrderSending(false);
                    a.setBusinessName("Nosotros SA");
                    a.setComments("For testing only");
                    a.setCurrency(em.find(Currency.class, "EUR"));
                    a.setEmail("miguelperezcolom@gmail.com");
                    a.setExportableToinvoicingApp(false);
                    a.setIdInInvoicingApp(null);
                    a.setName("Nosotros");
                    a.setOrdersSendingMethod(PurchaseOrderSendingMethod.EMAIL);
                    a.setSendOrdersTo("");
                    a.setShuttleTransfersInOwnInvoice(false);
                    a.setVatIdentificationNumber("X16237816321");

                    em.flush();
                }



                {
                    Actor a = new Actor();
                    em.persist(a);
                    a.setAddress("Gremi fusters, 11");
                    a.setAutomaticOrderConfirmation(false);
                    a.setAutomaticOrderSending(false);
                    a.setBusinessName("Islandbus SA");
                    a.setComments("For testing only");
                    a.setCurrency(em.find(Currency.class, "EUR"));
                    a.setEmail("miguelperezcolom@gmail.com");
                    a.setExportableToinvoicingApp(false);
                    a.setIdInInvoicingApp(null);
                    a.setName("Islandbus");
                    a.setOrdersSendingMethod(PurchaseOrderSendingMethod.EMAIL);
                    a.setSendOrdersTo("");
                    a.setShuttleTransfersInOwnInvoice(false);
                    a.setVatIdentificationNumber("A1712386211");
                    a.setAutomaticOrderSending(true);


                    em.flush();
                }

                {
                    Actor a = new Actor();
                    em.persist(a);
                    a.setAddress("Gremi fusters, 11");
                    a.setAutomaticOrderConfirmation(false);
                    a.setAutomaticOrderSending(false);
                    a.setBusinessName("Muchoviajes SA");
                    a.setComments("For testing only");
                    a.setCurrency(em.find(Currency.class, "EUR"));
                    a.setEmail("miguelperezcolom@gmail.com");
                    a.setExportableToinvoicingApp(false);
                    a.setIdInInvoicingApp(null);
                    a.setName("Muchoviaje");
                    a.setOrdersSendingMethod(PurchaseOrderSendingMethod.EMAIL);
                    a.setSendOrdersTo("");
                    a.setShuttleTransfersInOwnInvoice(false);
                    a.setVatIdentificationNumber("A16237816321");

                    em.flush();
                }


                {
                    Actor a = new Actor();
                    em.persist(a);
                    a.setAddress("Gremi fusters, 11");
                    a.setAutomaticOrderConfirmation(false);
                    a.setAutomaticOrderSending(false);
                    a.setBusinessName("Barceló hoteles SA");
                    a.setComments("For testing only");
                    a.setCurrency(em.find(Currency.class, "EUR"));
                    a.setEmail("miguelperezcolom@gmail.com");
                    a.setExportableToinvoicingApp(false);
                    a.setIdInInvoicingApp(null);
                    a.setName("Barceló Hoteles");
                    a.setOrdersSendingMethod(PurchaseOrderSendingMethod.QUOONAGENT);
                    a.setSendOrdersTo("");
                    a.setShuttleTransfersInOwnInvoice(false);
                    a.setVatIdentificationNumber("A1623787777");
                    a.setAutomaticOrderSending(true);

                    em.flush();
                }

                {
                    Actor a = new Actor();
                    em.persist(a);
                    a.setAddress("Gremi fusters, 11");
                    a.setAutomaticOrderConfirmation(false);
                    a.setAutomaticOrderSending(false);
                    a.setBusinessName("Tourico International");
                    a.setComments("For testing only");
                    a.setCurrency(em.find(Currency.class, "EUR"));
                    a.setEmail("miguelperezcolom@gmail.com");
                    a.setExportableToinvoicingApp(false);
                    a.setIdInInvoicingApp(null);
                    a.setName("Tourico");
                    a.setOrdersSendingMethod(PurchaseOrderSendingMethod.EMAIL);
                    a.setSendOrdersTo("");
                    a.setShuttleTransfersInOwnInvoice(false);
                    a.setVatIdentificationNumber("A1623787999");
                    a.setAutomaticOrderSending(true);

                    em.flush();
                }

            }
        });

    }

    private void populateOffers() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = em.createQuery("select s from " + Hotel.class.getName() + " s order by s.name").getResultList();

                User u = em.find(User.class, "admin");
                BillingConcept bc = (BillingConcept) em.createQuery("select s from " + BillingConcept.class.getName() + " s").getResultList().get(0);
                Actor prov = (Actor) em.createQuery("select s from " + Actor.class.getName() + " s").getResultList().get(0);

                Random random = new Random();

                for (int i = 0; i < 10; i++) {

                    Hotel h = hoteles.get(random.nextInt(hoteles.size() - 1));

                    DiscountOffer o;
                    h.getOffers().add(o = new DiscountOffer());
                    o.getHotels().add(h);

                    for (HotelContract c : h.getContracts()) {
                        h.getOffers().add(o);
                        o.getContracts().add(c);
                    }


                    o.setActive(true);
                    o.setIncludedInContractPdf(true);

                    o.setName("Discount offer " + i);

                    o.setPer(Per.BOOKING);
                    o.setScope(Scope.BOOKING);

                    o.setPercent(true);
                    o.setValue(15);

                    em.persist(o);


                }


            }
        });

    }

    private void populateContracts() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = em.createQuery("select s from " + Hotel.class.getName() + " s order by s.name").getResultList();
                List<Actor> actores = em.createQuery("select s from " + Actor.class.getName() + " s order by s.name").getResultList();

                User u = em.find(User.class, "admin");
                BillingConcept bc = (BillingConcept) em.createQuery("select s from " + BillingConcept.class.getName() + " s").getResultList().get(0);
                Actor prov = em.find(Actor.class, 4l); // barceló

                for (Hotel h : hoteles) {

                    int i = 1;
                    for (Actor a : actores) {

                        HotelContract c = new HotelContract();
                        h.getContracts().add(c);
                        c.getHotels().add(h);
                        em.persist(c);

                        c.setAudit(new Audit(u));
                        c.setBillingConcept(bc);
                        c.setPrivateComments("Test");
                        c.setSpecialTerms("Aquí las condiciones particulares de este contrato que no caben en la estructura que hemos montado");
                        c.setType((i % 2 == 0)? ContractType.SALE:ContractType.PURCHASE);
                        if (ContractType.SALE.equals(c.getType())) c.setSupplier(prov);
                        c.setTitle(h.getName() + " CONTRATO " + c.getType() + " TEST " + i);
                        c.setValidFrom(LocalDate.parse("2018-01-01"));
                        c.setValidTo(LocalDate.parse("2018-12-31"));
                        c.setVATIncluded(true);
                        c.setSupplier(prov);
                        c.getTargets().add(a);

                        c.setTerms(crearTerms(c, bc));

                        i++;
                    }

                }

            }
        });

    }

    private HotelContractPhoto crearTerms(HotelContract c, BillingConcept bc) {

        Hotel h = c.getHotels().get(0);

        HotelContractPhoto p = new HotelContractPhoto();

        p.setCurrencyIsoCode("EUR");
        p.setRatesType(RatesType.NET);

        p.setZeroPricesAllowed(false);

        p.getCommission().add(new DoublePerDateRange(LocalDate.now(), LocalDate.now().plusDays(10), 32.54));

        p.setMaxRoomsPerBooking(10);
        p.setMaxPaxPerBooking(30);
        p.setChildStartAge(2);
        p.setJuniorStartAge(7);
        p.setAdultStartAge(12);
        p.setYoungestFirst(false);

        for (Room r : h.getRooms()) p.getRooms().add(r.getCode());
        for (Board b : h.getBoards()) p.getBoards().add(b.getCode());

        for (String r : p.getRooms()) {
            p.getAllotment().add(new Allotment(r, null, null, 10));
        }
        for (int j = 1; j < 10; j++) {
            p.getCancellationRules().add(new CancellationRule(null, null, j * 10, j * 5, 0, 0, p.getRooms()));
        }

        //p.setWeekDaysRules();
        for (int j = 1; j < 30; j++) {
            p.getClauses().add("Claúsula contrato nº " + j);
        }


        {
            p.getGalas().add(new Gala(LocalDate.parse("2018-12-25"), 120, Arrays.asList(50.0, 20.0), p.getBoards()));
            p.getGalas().add(new Gala(LocalDate.parse("2018-12-31"), 180, Arrays.asList(50.0, 20.0), p.getBoards()));
        }

        for (int j = 1; j < 20; j++) {
            List<DatesRange> fechas = new ArrayList<>();
            for (int k = 0; k < 5; k++) {
                fechas.add(new DatesRange(c.getValidFrom().plusDays(j * 10), c.getValidFrom().plusDays((j + 1) * 10 - 1)));
            }
            Map<String, RoomFare> porHabitacion = new HashMap<>();
            for (String r : p.getRooms()) {
                RoomFare rf = new RoomFare();
                for (String b : p.getBoards()) {
                    BoardFare bf = new BoardFare();
                    bf.setRoomPrice(new FareValue(false, false, false, 30 + (j * 10)));
                    bf.setPaxPrice(new FareValue(false, false, false, 60.15 + (j * 10)));
                    for (int q = 0; q < 2; q++) {
                        bf.getPaxDiscounts().put(3 + q, new FareValue(false, true, true, q + 10));
                    }
                    for (int q = 0; q < 3; q++) {
                        for (int t = 0; t < 3; t++) {
                            bf.getChildDiscounts().put(t * 100 + 1 + q, new FareValue(false, true, true, q + 10));
                        }
                    }
                    rf.getFarePerBoard().put(b, bf);
                }
                porHabitacion.put(r, rf);
            }
            p.getFares().add(new Fare("Fare " + j, fechas, porHabitacion));
        }

        for (int j = 1; j < 20; j++) {
            p.getSupplements().add(new Supplement(null, null, false, true, "Suplemento " + j, (j % 2 == 0)?SupplementPer.PAX:SupplementPer.ROOM,
                    (j % 2 == 0)?SupplementScope.NIGHT:SupplementScope.BOOKING, false, 0, j, 0, bc.getCode(), p.getRooms(), p.getBoards()
                    ));
        }

        for (int j = 0; j < 50; j++) {
            p.getReleaseRules().add(new ReleaseRule(c.getValidFrom().plusDays(j * 10), c.getValidFrom().plusDays((j + 1) * 10 - 1), j % 14, p.getRooms()));
        }


        for (int j = 0; j < 50; j++) {
            p.getMinimumStayRules().add(new MinimumStayRule(c.getValidFrom().plusDays(j * 10), c.getValidFrom().plusDays((j + 1) * 10 - 1), j % 7, j % 2 == 0, 10, 0, SupplementPer.PAX, p.getRooms(), p.getBoards()));
        }




        return p;
    }


    private void testJaxb() throws JAXBException {

        HotelContractPhoto p = new HotelContractPhoto();

        for (int i = 0; i < 10000; i++) {
            long t0 = System.nanoTime();
            String s = p.toString();
            long t = System.nanoTime();

            if (i % 100 == 0) System.out.println("" + i + ". con jdom ha tardado " + (t - t0) + "ns");
        }


        System.out.println(p);


        JAXBContext jaxbContext = JAXBContext.newInstance(HotelContractPhoto.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // output pretty printed
        //jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        //jaxbMarshaller.marshal(customer, file);
        jaxbMarshaller.marshal(p, System.out);

        System.out.println();

        for (int i = 0; i < 10000; i++) {
            StringWriter r = new StringWriter();
            long t0 = System.nanoTime();
            jaxbMarshaller.marshal(p, r);
            long t = System.nanoTime();


            if (i % 100 == 0) System.out.println("" + i + ". con jaxb ha tardado " + (t - t0) + "ns");
            if (i >= 9999) {

                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                p = (HotelContractPhoto) jaxbUnmarshaller.unmarshal(new StringReader(r.toString()));


            }
        }



        System.out.println("p.getAdultStartAge() = " + p.getAdultStartAge());

    }

    private void testContract() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {


                List<Hotel> hoteles = em.createQuery("select s from " + Hotel.class.getName() + " s order by s.name").getResultList();

                Hotel h = hoteles.get(0);

                if (false) {

                    HotelContract c;
                    h.getContracts().add(c = new HotelContract());
                    c.getHotels().add(h);
                    h.getContracts().add(c);
                    em.persist(c);

                }


                HotelContract c = h.getContracts().get(0);

                if (c.getTerms() != null) {
                    System.out.println("terms=" + c.getTerms());
                } else {
                    System.out.println("terms es null");
                    HotelContractPhoto t;
                    c.setTerms(t = new HotelContractPhoto());
                }


                c.getTerms().setAdultStartAge(3);


            }
        });

    }


    public void populateRoomsBoardsAndHotelCategories() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {


                // crear tipos de habitación

                String[][] habs = {
                        {"DBL", "Double room", "Habitación doble"}
                        , {"DSV", "Double sea view", "Habitación doble vista mar"}
                        , {"DUI", "Double single use room", "Habitación doble uso individual"}
                        , {"SUI", "Suite", "Suite"}
                        , {"JSUI", "Junior suite", "Junior suite"}
                };

                for (String[] x : habs) {
                    RoomType r;
                    em.persist(r = new RoomType());
                    r.setCode(x[0]);
                    r.setName(new Literal(x[1], x[2]));
                }

                // crear tipos de régimen

                String[][] regs = {
                        {"RO", "Room only", "Solo alojamiento"}
                        , {"BB", "Bed and breakfast", "Alojamiento y desayuno"}
                        , {"HB", "Half board", "Media pensión"}
                        , {"FB", "Full board", "Pensión completa"}
                        , {"AI", "All inclusive", "Todo incluido"}
                };

                for (String[] x : regs) {
                    BoardType r;
                    em.persist(r = new BoardType());
                    r.setCode(x[0]);
                    r.setName(new Literal(x[1], x[2]));
                }

                // crear categorías de hotel

                String[][] cats = {
                        {"*", "1 star", "1 estrella"}
                        , {"**", "2 starts", "2 estrellas"}
                        , {"***", "3 stars", "3 estrellas"}
                        , {"****", "4 stars", "4 estrellas"}
                        , {"*****", "5 stars", "5 estrellas"}
                };

                for (String[] x : cats) {
                    HotelCategory r;
                    em.persist(r = new HotelCategory());
                    r.setCode(x[0]);
                    r.setName(new Literal(x[1], x[2]));
                }

            }
        });



    }


    public void populateHotels() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {


                City s = em.find(City.class, 1l);
                Office o = em.find(Office.class, 1l);


                List<RoomType> rts = em.createQuery("select s from " + RoomType.class.getName() + " s").getResultList();
                List<BoardType> bts = em.createQuery("select s from " + BoardType.class.getName() + " s").getResultList();
                List<HotelCategory> cats = em.createQuery("select s from " + HotelCategory.class.getName() + " s").getResultList();


                // crear hoteles

                Random random = new Random();


                String[] nombresHotel = {
                        "Hotel Don Juan"
                        , "Hotel Saratoga"
                        , "Hotel Tryp Bellver"
                        , "Hotel Alumdaina"
                        , "Hotel Abelux"
                        , "Hotel Innside by Melia Palma Center"
                        , "Hotel UR Palacio Avenida"
                        , "Palau Sa Font"
                        , "Palacio de Congresos"
                        , "Hotel Tryp Palma Bellver"
                        , "Hotel Palladium"
                        , "Puro Hotel"
                        , "Sant Frances Hotel Singular"
                        , "Hotel Continental"
                        , "Hotel Costa Azul"
                        , "Hotel Born"
                        , "Hotel Amic Horizonte"
                        , "Hotel San Lorenzo - Adults Only"
                        , "Hotel Melia Palas Atenea"
                        , "HM Jaime II"
                        , "Hotel Bonany"
                        , "Hotel Armadans"
                        , "Borne Suites"
                        , "Hotel Dalt Murada"
                        , "Hotel Cappuccino"
                };


                for (String hn : nombresHotel) {

                    Hotel h = new Hotel();
                    em.persist(h);
                    h.setName(hn);
                    h.setCity(s);
                    s.getHotels().add(h);
                    h.setOffice(o);
                    h.setActive(true);

                    h.setLat("39.5877926");
                    h.setLon("2.6484694");



                    h.setCategory(cats.get(random.nextInt(cats.size() - 1)));


                    int maxhabs = 2 + random.nextInt(rts.size() - 1);
                    for (int j = 0; j < maxhabs; j++) {
                        Room r = new Room();
                        r.setType(rts.get(random.nextInt(rts.size() - 1)));
                        r.setChildrenAllowed(true);
                        r.setDescription(new Literal("Beatiful room with all services included", "Bonita habitación con todos los servicios incluidos"));
                        r.setHotel(h);
                        r.setInfantsAllowed(true);
                        r.setMaxCapacity("3");
                        r.setMinPax(1);
                        h.getRooms().add(r);
                    }



                    int maxregs = 2 + random.nextInt(bts.size() - 1);
                    for (int j = 0; j < maxregs; j++) {
                        Board r = new Board();
                        r.setType(bts.get(random.nextInt(bts.size() - 1)));
                        r.setDescription(new Literal("Free buffet with water included", "Buffet libre con agua incluida"));
                        r.setHotel(h);
                        h.getBoards().add(r);
                    }

                    em.flush();

                }

            }
        });



    }


    private void populateStopSales() throws Throwable {

        Random random = new Random();

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = em.createQuery("select s from " + Hotel.class.getName() + " s").getResultList();

                List<Actor> actores =  em.createQuery("select s from " + Actor.class.getName() + " s").getResultList();

                for (Hotel h : hoteles) {

                    StopSales s;
                    h.setStopSales(s = new StopSales());
                    em.persist(s);
                    s.setHotel(h);

                    int maxStopSales = 10 + random.nextInt(30);

                    for (int i = 0; i < maxStopSales; i++) {

                        StopSalesOperation l;
                        s.getOperations().add(l = new StopSalesOperation());
                        em.persist(l);
                        l.setStopSales(s);
                        l.setOnNormalInventory(true);
                        l.setOnSecurityInventory(true);
                        l.setAction(StopSalesAction.CLOSE);

                        int desde = random.nextInt(2 * 365);
                        int hasta = desde + random.nextInt(10);

                        l.setStart(LocalDate.now().plusDays(desde));
                        l.setEnd(LocalDate.now().plusDays(hasta));
                        if (i % 5 == 0 && h.getRooms().size() > 0) {
                            l.getRooms().add(h.getRooms().get((h.getRooms().size() > 1)?random.nextInt(h.getRooms().size() - 1):0).getType());
                        }
                        if (i % 10 == 0) {
                            l.getActors().add(actores.get(random.nextInt(10) % actores.size()));
                        }

                    }

                    s.build(em);

                }


            }
        });

    }


    public void populateInventory() throws Throwable {

        Random random = new Random();

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = em.createQuery("select s from " + Hotel.class.getName() + " s").getResultList();


                for (Hotel h : hoteles) if (h.getRooms().size() > 0) {

                    Inventory i;
                    h.getInventories().add(i = new Inventory());
                    i.getHotels().add(h);
                    em.persist(i);
                    i.setName(h.getName());

                    int maxLines = 10 + random.nextInt(1000);
                    for (int j = 0; j < maxLines; j++) {

                        InventoryOperation l;
                        i.getOperations().add(l = new InventoryOperation());
                        em.persist(l);
                        l.setInventory(i);
                        l.setAction(InventoryAction.SET);
                        l.setQuantity(1 + random.nextInt(10));
                        int desde = random.nextInt(2 * 365);
                        int hasta = desde + random.nextInt(2 * 365 - desde);

                        l.setStart(LocalDate.now().plusDays(desde));
                        l.setEnd(LocalDate.now().plusDays(hasta));
                        l.setRoom(h.getRooms().get((h.getRooms().size() > 1)?random.nextInt(h.getRooms().size() - 1):0).getType());
                    }

                    i.build(em);

                }

            }
        });

    }

}
