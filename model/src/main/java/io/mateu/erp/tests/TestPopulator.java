package io.mateu.erp.tests;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.mdd.core.model.multilanguage.Literal;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.population.Populator;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.world.Zone;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.DiscountOffer;
import io.mateu.erp.model.product.hotel.offer.Per;
import io.mateu.erp.model.product.hotel.offer.Scope;
import io.mateu.erp.model.product.transfer.*;
import io.mateu.erp.model.financials.Currency;
import io.mateu.mdd.core.util.DatesRange;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;

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
        System.setProperty("defaultpuname", "mateu-common");


        populateAll(AppConfig.class, Partner.class, Hotel.class, true, HotelContract.class);

    }

    public static void populateEverythingButContracts() throws Throwable {
        populateAll(AppConfig.class, Partner.class, Hotel.class, false, HotelContract.class);
    }

    public static void populateEverything() throws Throwable {
        populateAll(AppConfig.class, Partner.class, Hotel.class,true, HotelContract.class);
    }

    public static void populateAll(Class appConfigClass, Class actorClass, Class hotelClass, boolean hotelContracts, Class hotelContractClass) throws Throwable {

        Helper.transact((JPATransaction) (em) -> {

            int nomappconfigs = em.createQuery("select x from " + AppConfig.class.getName() + " x").getResultList().size();

            if (nomappconfigs == 0) new Populator().populate(appConfigClass);

        });

        TestPopulator p = new TestPopulator();

        Helper.transact((JPATransaction) (em) -> {

            int numhots = em.createQuery("select x from " + Hotel.class.getName() + " x").getResultList().size();

            if (numhots > 0) throw new Exception("Can not populate with test data if there are already hotels in the database!");

        });


        p.populateActors(actorClass);

        p.populatePortfolio();

        p.populateTransferProduct();

        p.populateRoomsBoardsAndHotelCategories();

        p.populateHotels(hotelClass);

        if (hotelContracts) {
            p.populateStopSales(hotelClass);

            p.populateInventory(hotelClass);

            p.populateContracts(hotelClass, hotelContractClass);

            p.populateOffers(hotelClass);

            p.populateBookings();
        }


        p.populateAuthTokens();


        //p.testContract();

        //p.testJaxb();

        //p.testJson();
    }

    public void populateAuthTokens() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                {
                    AuthToken t = new AuthToken();
                    t.setId("eyAiY3JlYXRlZCI6ICJXZWQgTm92IDA4IDEyOjE4OjM0IENFVCAyMDE3IiwgInVzZXJJZCI6ICJhZG1pbiIsICJhY3RvcklkIjogIjMiIn0");
                    t.setActive(true);
                    t.setUser(em.find(io.mateu.erp.model.authentication.User.class, "admin"));
                    t.setPartner(em.find(Partner.class, 3l));
                    em.persist(t);
                }


                {
                    AuthToken t = new AuthToken();
                    t.setId("eyAiY3JlYXRlZCI6ICJXZWQgTm92IDA4IDEyOjE4OjQ3IENFVCAyMDE3IiwgInVzZXJJZCI6ICJhZG1pbiIsICJhY3RvcklkIjogIjMiLCAiaG90ZWxJZCI6ICIxMiJ9");
                    t.setActive(true);
                    t.setUser(em.find(io.mateu.erp.model.authentication.User.class, "admin"));
                    t.setPartner(em.find(Partner.class, 3l));
                    t.setHotel(em.find(Hotel.class, 12l));
                    em.persist(t);
                }

            }
        });

    }

    public void populateTransferProduct() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                Random random = new Random();

                io.mateu.erp.model.authentication.User u = em.find(io.mateu.erp.model.authentication.User.class, Populator.USER_ADMIN);

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

                Partner islandbus = null;
                Partner nosotros = null;

                for (Partner a : (List<Partner>) em.createQuery("select s from " + Partner.class.getName() + " s").getResultList()) {
                    if (a.getName().toLowerCase().contains("nosotros")) nosotros = a;
                    if (a.getName().toLowerCase().contains("islandbus")) islandbus = a;
                }

                BillingConcept bc = (BillingConcept) em.createQuery("select c from " + BillingConcept.class.getName() + " c").getResultList().get(0);


                for (Country cx : (List<Country>) em.createQuery("select c from " + Country.class.getName() + " c order by c.name").getResultList()) {

                    for (Destination s : cx.getDestinations()) {

                        List<io.mateu.erp.model.product.transfer.Zone> zonas = new ArrayList<>();
                        for (Zone l : s.getZones()) {
                            io.mateu.erp.model.product.transfer.Zone z = new io.mateu.erp.model.product.transfer.Zone();
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
                            c.setValidFrom(LocalDate.of(2017, 1, 1));
                            c.setValidTo(LocalDate.of(2020, 12, 31));
                            c.setVATIncluded(true);
                            c.setBillingConcept(bc);

                            for (io.mateu.erp.model.product.transfer.Zone de : zonas) for (io.mateu.erp.model.product.transfer.Zone a : zonas) if (!de.equals(a)){
                                Price p = new Price();
                                c.getPrices().add(p);
                                em.persist(p);
                                p.setContract(c);
                                p.setDestination(a);
                                p.setOrigin(de);
                                p.setTransferType(tt);
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

    public void populatePortfolio() throws Throwable {

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

                        Destination s = new Destination();
                        s.setName((String) ds.get("name"));
                        c.getDestinations().add(s);
                        s.setCountry(c);
                        em.persist(s);

                        em.flush();

                        for (Map<String, Object> dl : (List<Map<String, Object>>) ds.get("cities")) {

                            Zone l = new Zone();
                            l.setName((String) dl.get("name"));
                            s.getZones().add(l);
                            l.setDestination(s);
                            em.persist(l);

                            em.flush();

                            for (Map<String, Object> dtp : (List<Map<String, Object>>) dl.get("transferpoints")) {

                                TransferPoint p = new TransferPoint();
                                p.setName((String) dtp.get("name"));
                                p.setInstructions("---");
                                p.setType(TransferPointType.valueOf((String) dtp.get("type")));
                                l.getTransferPoints().add(p);
                                p.setZone(l);
                                em.persist(p);

                                em.flush();

                            }



                        }
                    }

                }


            }
        });


    }

    public void testJson() throws IOException {

        String json = CharStreams.toString(new InputStreamReader(
                this.getClass().getResourceAsStream("portfolio.json"), Charsets.UTF_8));

        Map<String, Object> m = Helper.fromJson(json);


        System.out.println("hecho!");
    }

    public void populateBookings() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                Currency eur = em.find(Currency.class, "EUR");

                Partner agencia = null;
                Partner proveedor = null;


                for (Partner a : (List<Partner>) em.createQuery("select s from " + Partner.class.getName() + " s").getResultList()) {
                    if (a.getName().toLowerCase().contains("muchoviaje")) agencia = a;
                    if (a.getName().toLowerCase().contains("islandbus")) proveedor = a;
                }

                io.mateu.erp.model.authentication.User u = em.find(io.mateu.erp.model.authentication.User.class, Populator.USER_ADMIN);

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
                    }


                }

            }
        });

    }

    public void populateActors(Class actorClass) throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {


                {
                    Partner a = (Partner) actorClass.newInstance();
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
                    a.setProvider(true);

                    em.flush();
                }



                {
                    Partner a = (Partner) actorClass.newInstance();
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
                    a.setProvider(true);

                    em.flush();
                }

                {
                    Partner a = (Partner) actorClass.newInstance();
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
                    a.setAgency(true);

                    em.flush();
                }


                {
                    Partner a = (Partner) actorClass.newInstance();
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
                    a.setProvider(true);

                    em.flush();
                }

                {
                    Partner a = (Partner) actorClass.newInstance();
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
                    a.setProvider(true);

                    em.flush();
                }

            }
        });

    }

    public void populateOffers(Class hotelClass) throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = em.createQuery("select s from " + hotelClass.getName() + " s order by s.name").getResultList();

                io.mateu.erp.model.authentication.User u = em.find(io.mateu.erp.model.authentication.User.class, "admin");
                BillingConcept bc = (BillingConcept) em.createQuery("select s from " + BillingConcept.class.getName() + " s").getResultList().get(0);
                Partner prov = (Partner) em.createQuery("select s from " + Partner.class.getName() + " s").getResultList().get(0);

                Random random = new Random();

                for (int i = 0; i < 10; i++) {

                    Hotel h = hoteles.get(random.nextInt(hoteles.size() - 1));

                    DiscountOffer o;
                    h.getOffers().add(o = new DiscountOffer());
                    o.setHotel(h);

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

    public void populateContracts(Class hotelClass, Class hotelContractClass) throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = em.createQuery("select s from " + hotelClass.getName() + " s order by s.name").getResultList();
                List<Partner> actores = em.createQuery("select s from " + Partner.class.getName() + " s order by s.name").getResultList();

                io.mateu.erp.model.authentication.User u = em.find(io.mateu.erp.model.authentication.User.class, "admin");
                BillingConcept bc = (BillingConcept) em.createQuery("select s from " + BillingConcept.class.getName() + " s").getResultList().get(0);
                Partner prov = em.find(Partner.class, 4l); // barceló
                Currency eur = em.find(Currency.class, "EUR");

                for (Hotel h : hoteles) {

                    int i = 1;
                    for (Partner a : actores) {

                        HotelContract c = (HotelContract) hotelContractClass.newInstance();
                        h.getContracts().add(c);
                        c.setHotel(h);
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
                        c.getPartners().add(a);

                        c.setCurrency(eur);

                        c.setTerms(crearTerms(c, bc));

                        i++;
                    }

                }

            }
        });

    }

    public HotelContractPhoto crearTerms(HotelContract c, BillingConcept bc) {

        Hotel h = c.getHotel();

        HotelContractPhoto p = new HotelContractPhoto();

        p.setRatesType(RatesType.NET);

        p.setZeroPricesAllowed(false);

        p.getCommission().add(new DoublePerDateRange(LocalDate.now(), LocalDate.now().plusDays(10), 32.54));

        p.setMaxRoomsPerBooking(10);
        p.setMaxPaxPerBooking(30);
        p.setChildStartAge(2);
        p.setJuniorStartAge(7);
        p.setAdultStartAge(12);
        p.setYoungestFirst(false);

        h.getRooms().stream().map((r) -> r.getCode()).forEach((r) -> p.getAllotment().add(new Allotment(r, null, null, 10)));

        for (int j = 1; j < 10; j++) {
            p.getCancellationRules().add(new CancellationRule(null, null, j * 10, j * 5, 0, 0, null));
        }

        //p.setWeekDaysRules();
        for (int j = 1; j < 30; j++) {
            p.getClauses().add("Claúsula contrato nº " + j);
        }


        {
            p.getGalas().add(new Gala(LocalDate.parse("2018-12-25"), 120, Arrays.asList(50.0, 20.0), null));
            p.getGalas().add(new Gala(LocalDate.parse("2018-12-31"), 180, Arrays.asList(50.0, 20.0), null));
        }

        for (int j = 1; j < 20; j++) {
            List<DatesRange> fechas = new ArrayList<>();
            for (int k = 0; k < 5; k++) {
                fechas.add(new DatesRange(c.getValidFrom().plusDays(j * 10), c.getValidFrom().plusDays((j + 1) * 10 - 1)));
            }

            List<LinearFareLine> lineas = new ArrayList<>();

            int finalJ = j;
            h.getRooms().stream().map((r) -> r.getCode()).forEach((r) -> {
                h.getBoards().stream().map((b) -> b.getCode()).forEach((b) -> {
                    double ad;
                    lineas.add(new LinearFareLine(r, b, 30 + (finalJ * 10), ad = 60.15 + (finalJ * 10), ad, ad / 2, 0));
                });
            });

            p.getFares().add(new LinearFare(fechas, "Fare " + j, lineas));
        }

        for (int j = 1; j < 20; j++) {
            p.getSupplements().add(new Supplement(null, null, false, true, "Suplemento " + j, (j % 2 == 0)?SupplementPer.PAX:SupplementPer.ROOM,
                    (j % 2 == 0)?SupplementScope.NIGHT:SupplementScope.BOOKING, false, 0, j, 0, bc.getCode(), null, null
            ));
        }

        for (int j = 0; j < 50; j++) {
            p.getReleaseRules().add(new ReleaseRule(c.getValidFrom().plusDays(j * 10), c.getValidFrom().plusDays((j + 1) * 10 - 1), j % 14, null));
        }


        for (int j = 0; j < 50; j++) {
            p.getMinimumStayRules().add(new MinimumStayRule(c.getValidFrom().plusDays(j * 10), c.getValidFrom().plusDays((j + 1) * 10 - 1), j % 7, j % 2 == 0, 10, 0, SupplementPer.PAX, null, null));
        }




        return p;
    }


    public void testJaxb() throws JAXBException {

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

    public void testContract() throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {


                List<Hotel> hoteles = em.createQuery("select s from " + Hotel.class.getName() + " s order by s.name").getResultList();

                Hotel h = hoteles.get(0);

                if (false) {

                    HotelContract c;
                    h.getContracts().add(c = new HotelContract());
                    c.setHotel(h);
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


    public void populateHotels(Class hotelClass) throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {


                Zone s = em.find(Zone.class, 1l);
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
                        , "xx-Hotel Alohas"
                        , "xx-Hotel Aloha"
                        , "xx-Central"
                        , "xx-Hotel Hawai"
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

                    Hotel h = (Hotel) hotelClass.newInstance();
                    em.persist(h);
                    h.setName(hn);
                    h.setZone(s);
                    s.getHotels().add(h);
                    h.setOffice(o);
                    h.setActive(true);

                    h.setLat("39.5877926");
                    h.setLon("2.6484694");



                    h.setCategory(cats.get(random.nextInt(cats.size() - 1)));

                    /*
                    String[][] habs = {
                        {"DBL", "Double room", "Habitación doble"}
                        , {"DSV", "Double sea view", "Habitación doble vista mar"}
                        , {"DUI", "Double single use room", "Habitación doble uso individual"}
                        , {"SUI", "Suite", "Suite"}
                        , {"JSUI", "Junior suite", "Junior suite"}
                };
                     */


                    int maxhabs = 2 + random.nextInt(rts.size() - 1);
                    for (int j = 0; j < maxhabs; j++) {
                        Room r = new Room();
                        r.setType(rts.get(random.nextInt(rts.size() - 1)));
                        r.setChildrenAllowed(true);
                        r.setDescription(new Literal("Beatiful room with all services included", "Bonita habitación con todos los servicios incluidos"));
                        r.setHotel(h);
                        r.setInfantsAllowed(true);
                        if ("dui".equalsIgnoreCase(r.getType().getCode())) {
                            r.getMaxCapacities().getCapacities().add(new MaxCapacity(1, 0, 0));
                        } else if ("sui".equalsIgnoreCase(r.getType().getCode())) {
                            r.getMaxCapacities().getCapacities().add(new MaxCapacity(4, 0, 0));
                        } else {
                            r.getMaxCapacities().getCapacities().add(new MaxCapacity(2, 0, 0));
                        }
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


                    StopSales ss;
                    h.setStopSales(ss = new StopSales());
                    em.persist(ss);
                    ss.setHotel(h);

                    em.flush();

                }

            }
        });



    }


    public void populateStopSales(Class hotelClass) throws Throwable {

        Random random = new Random();

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = em.createQuery("select s from " + hotelClass.getName() + " s").getResultList();

                List<Partner> actores =  em.createQuery("select s from " + Partner.class.getName() + " s").getResultList();

                for (Hotel h : hoteles) {

                    StopSales s = h.getStopSales();

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


    public void populateInventory(Class hotelClass) throws Throwable {

        Random random = new Random();

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = em.createQuery("select s from " + hotelClass.getName() + " s").getResultList();


                for (Hotel h : hoteles) if (h.getRooms().size() > 0) {

                    Inventory i;
                    h.getInventories().add(i = new Inventory());
                    i.setHotel(h);
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
