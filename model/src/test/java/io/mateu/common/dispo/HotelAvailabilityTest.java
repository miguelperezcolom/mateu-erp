package io.mateu.common.dispo;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import com.google.common.collect.Lists;
import io.mateu.common.model.authentication.Audit;
import io.mateu.common.model.authentication.USER_STATUS;
import io.mateu.common.model.authentication.User;
import io.mateu.common.model.multilanguage.Literal;
import io.mateu.erp.dispo.DispoRQ;
import io.mateu.erp.dispo.HotelAvailabilityRunner;
import io.mateu.erp.dispo.ModeloDispo;
import io.mateu.erp.dispo.Occupancy;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.LocalizationRule;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.easytravelapi.hotel.AvailableHotel;
import org.easytravelapi.hotel.BoardPrice;
import org.easytravelapi.hotel.Option;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class HotelAvailabilityTest
    extends TestCase
{

    private Map<String, RoomType> roomTypes;
    private Map<String, BoardType> boardTypes;
    private Map<String, Room> rooms;
    private Map<String, Board> boards;

    Currency eur;

    private Hotel hotel;
    private Actor agencia;
    private ModeloDispo modelo;
    private HotelContract contratoVenta;
    private User u;
    private BillingConcept centroProduccion;
    private Actor proveedor;
    private HotelContractPhoto condicionesContrato;
    private Map<Long, HotelContract> contratos;
    private LinearFare tarifaEnero;
    private LinearFareLine rfDobleEnAbril;
    private LinearFare fareAbril;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.out.println("setup");

        // creamos un usuario
        u = new User();
        u.setLogin("admin");
        u.setName("Admin");
        u.setStatus(USER_STATUS.ACTIVE);
        u.setEmail("miguelperezcolom@gmail.com");

        // creamos una divisa
        eur = new Currency();
        eur.setName("Euro");
        eur.setIso4217Code("487");
        eur.setIsoCode("EUR");

        //creamos un centro de producción
        centroProduccion = new BillingConcept();
        centroProduccion.setCode("HOT");
        centroProduccion.setName("Estancia en hotel");
        centroProduccion.setLocalizationRule(LocalizationRule.SERVICE);

        // creamos una agencia
        agencia = new Actor();
        agencia.setId(1);
        agencia.setName("Muchoviaje");
        agencia.setActive(true);

        // creamos un proveedor
        proveedor = new Actor();
        proveedor.setId(2);
        proveedor.setName("Barcelo Hoteles");
        proveedor.setActive(true);

        // creamos tipos de habitación
        roomTypes = new HashMap<>();
        {
            RoomType r;
            roomTypes.put("dbl", r = new RoomType());
            r.setCode("DBL");
            r.setName(new Literal("Double room", "Habitación doble"));
        }
        {
            RoomType r;
            roomTypes.put("dui", r = new RoomType());
            r.setCode("DUI");
            r.setName(new Literal("Double room single use", "Habitación doble uso individual"));
        }
        {
            RoomType r;
            roomTypes.put("sui", r = new RoomType());
            r.setCode("SUI");
            r.setName(new Literal("Suite", "Suite"));
        }

        // creamos tipos de régimen
        boardTypes = new HashMap<>();
        {
            BoardType b;
            boardTypes.put("sa", b = new BoardType());
            b.setCode("SA");
            b.setName(new Literal("Room only", "Solo alojamiento"));
        }
        {
            BoardType b;
            boardTypes.put("ad", b = new BoardType());
            b.setCode("AD");
            b.setName(new Literal("Bed & breakfast", "Alojamiento y desayuno"));
        }        {
            BoardType b;
            boardTypes.put("mp", b = new BoardType());
            b.setCode("MP");
            b.setName(new Literal("Half board", "Media pensión"));
        }


        // creamos hotel
        hotel = new Hotel();
        hotel.setId(1);
        hotel.setName("Hotel Abelux");
        hotel.setActive(true);
        hotel.setChildStartAge(2);
        hotel.setJuniorStartAge(10);
        hotel.setAdultStartAge(15);

        // habitaciones del hotel
        rooms = new HashMap<>();
        {
            Room r;
            hotel.getRooms().add(r = new Room());
            r.setId(1);
            r.setType(roomTypes.get("dbl"));
            r.setMinPax(1);
            r.setChildrenAllowed(true);
            r.setInfantsAllowed(true);
            r.setInfantsInBed(false);
            r.setInventoryPropietary(null);
            r.setMinAdultsForChildDiscount(2);
            r.setMaxCapacities(new MaxCapacities());
            r.setDescription(new Literal("Beautiful double room", "Bonita habitación doble"));
            r.setHotel(hotel);
            rooms.put("dbl", r);
        }
        {
            Room r;
            hotel.getRooms().add(r = new Room());
            r.setId(1);
            r.setType(roomTypes.get("sui"));
            r.setMinPax(1);
            r.setChildrenAllowed(true);
            r.setInfantsAllowed(true);
            r.setInfantsInBed(false);
            r.setInventoryPropietary(null);
            r.setMinAdultsForChildDiscount(2);
            r.setMaxCapacities(new MaxCapacities());
            r.setDescription(new Literal("Beautiful suite", "Bonita suite"));
            r.setHotel(hotel);
            rooms.put("sui", r);
        }

        // regímenes del hotel
        boards = new HashMap<>();
        {
            Board b;
            hotel.getBoards().add(b = new Board());
            b.setId(1);
            b.setType(boardTypes.get("sa"));
            b.setHotel(hotel);
            b.setDescription(new Literal("Sleep only, no meals included", "Solo la habitación, sin comindas"));
            boards.put("sa", b);
        }
        {
            Board b;
            hotel.getBoards().add(b = new Board());
            b.setId(1);
            b.setType(boardTypes.get("mp"));
            b.setHotel(hotel);
            b.setDescription(new Literal("Breadkast and lunch included", "Desayuno y almuerzo incluidos"));
            boards.put("mp", b);
        }


        // creamos paros
        StopSales ss;
        hotel.setStopSales(ss = new StopSales());
        StopSalesLine ssl;
        ss.getLines().add(ssl = new StopSalesLine());
        ssl.setStopSales(ss);
        ssl.setStart(LocalDate.of(2101, 1, 21));
        ssl.setEnd(LocalDate.of(2101, 1, 23));

        // creamos cupo
        Inventory i;
        hotel.getInventories().add(i = new Inventory());
        i.setHotel(hotel);
        i.setId(1);
        InventoryLine il;
        i.getLines().add(il = new InventoryLine());
        il.setInventory(i);
        il.setQuantity(100);
        il.setRoom(roomTypes.get("dbl"));
        il.setStart(LocalDate.of(2101, 1, 1));
        il.setEnd(LocalDate.of(2101, 6, 30));

        contratos = new HashMap<>();

        // creamos un contrato
        hotel.getContracts().add(contratoVenta = new HotelContract());
        contratoVenta.setType(ContractType.SALE);
        contratoVenta.setId(1);
        contratoVenta.setTitle("Contrato venta");
        contratoVenta.setVATIncluded(true);
        contratoVenta.setValidFrom(LocalDate.of(2101, 1, 1));
        contratoVenta.setValidTo(LocalDate.of(2101, 4, 30));
        contratoVenta.setHotel(hotel);
        contratoVenta.setBillingConcept(centroProduccion);
        contratoVenta.setAudit(new Audit(u));

        contratoVenta.setSupplier(proveedor);
        contratoVenta.setTerms(condicionesContrato = new HotelContractPhoto());

        contratos.put(1l, contratoVenta);

        {
            LinearFare f;
            condicionesContrato.getFares().add(f = new LinearFare());
            tarifaEnero = f;
            f.setName("Tarifa Enero");
            f.getDates().add(new DatesRange(LocalDate.of(2101, 1, 1), LocalDate.of(2101, 1, 31)));


            LinearFareLine l;
            f.getLines().add(l = new LinearFareLine("DBL", "SA", 0, 10, 0, 0, 0));

            // uso individual
            l.setSingleUsePrice(new FareValue("+40%"));

            // tercer pax
            l.setExtraAdultPrice(new FareValue("-2"));

            // primer niño
            l.setChildPrice(new FareValue("-7"));

            // segundo niño
            l.setExtraChildPrice(new FareValue(false, false, false, 6));

            // bebe
            l.setInfantPrice(new FareValue(false, false, false, 1));

            // junior
            l.setJuniorPrice(new FareValue(false, false, false, 8));

        }
        {
            LinearFare f;
            condicionesContrato.getFares().add(f = new LinearFare());
            f.setName("Tarifa Febrero");
            f.getDates().add(new DatesRange(LocalDate.of(2101, 2, 1), LocalDate.of(2101, 2, 28)));
            f.getLines().add(new LinearFareLine("DBL", "SA", 0, 15));
        }

        {
            LinearFare f;
            condicionesContrato.getFares().add(f = new LinearFare());
            f.setName("Tarifa Marzo");
            f.getDates().add(new DatesRange(LocalDate.of(2101, 3, 1), LocalDate.of(2101, 3, 31)));
            f.getLines().add(new LinearFareLine("DBL", "SA", 0, 20, 0, 0, 0));

            Supplement s;
            condicionesContrato.getSupplements().add(s = new Supplement());
            s.setStart(LocalDate.of(2101, 3, 5));
            s.setEnd(LocalDate.of(2101, 3, 7));
            s.setDescription("Suplemento F1");
            s.setAffectedByOffers(false);
            s.setBoards(null);
            s.setRooms(null);
            s.setOnRequest(false);
            s.setOptional(false);
            s.setPer(SupplementPer.PAX);
            s.setScope(SupplementScope.NIGHT);
            s.setValue(50);

            condicionesContrato.getSupplements().add(s = new Supplement());
            s.setStart(LocalDate.of(2101, 3, 15));
            s.setEnd(LocalDate.of(2101, 3, 31));
            s.setDescription("Suplemento por que si");
            s.setAffectedByOffers(false);
            s.setBoards(null);
            s.setRooms(null);
            s.setOnRequest(false);
            s.setOptional(false);
            s.setPer(SupplementPer.PAX);
            s.setScope(SupplementScope.NIGHT);
            s.setValue(50);

            condicionesContrato.getSupplements().add(s = new Supplement());
            s.setStart(LocalDate.of(2101, 3, 17));
            s.setEnd(LocalDate.of(2101, 3, 17));
            s.setDescription("Suplemento por que no");
            s.setAffectedByOffers(false);
            s.setBoards(null);
            s.setRooms(null);
            s.setOnRequest(false);
            s.setOptional(false);
            s.setPer(SupplementPer.ROOM);
            s.setScope(SupplementScope.NIGHT);
            s.setValue(100);

            condicionesContrato.getSupplements().add(s = new Supplement());
            s.setStart(LocalDate.of(2101, 3, 21));
            s.setEnd(LocalDate.of(2101, 3, 31));
            s.setDescription("Suplemento aplicación única");
            s.setAffectedByOffers(false);
            s.setBoards(null);
            s.setRooms(null);
            s.setOnRequest(false);
            s.setOptional(false);
            s.setPer(SupplementPer.ROOM);
            s.setScope(SupplementScope.BOOKING);
            s.setValue(100);

            condicionesContrato.getSupplements().add(s = new Supplement());
            s.setStart(LocalDate.of(2101, 3, 27));
            s.setEnd(LocalDate.of(2101, 3, 27));
            s.setDescription("Suplemento porcentual (+50% sobre alojamiento)");
            s.setAffectedByOffers(false);
            s.setBoards(null);
            s.setRooms(null);
            s.setOnRequest(false);
            s.setOptional(false);
            s.setPer(SupplementPer.ROOM);
            s.setScope(SupplementScope.BOOKING);
            s.setOnStay(true);
            s.setPercent(50);

        }

        {
            LinearFare f;
            condicionesContrato.getFares().add(f = new LinearFare());
            fareAbril = f;
            f.setName("Tarifa Febrero");
            f.getDates().add(new DatesRange(LocalDate.of(2101, 4, 1), LocalDate.of(2101, 4, 30)));
            LinearFareLine l;
            f.getLines().add(l = new LinearFareLine("DBL", "SA", 0, 30, 0, 0, 0));
            rfDobleEnAbril = l;

        }


        modelo = new ModeloDispo() {
            @Override
            public IHotelContract getHotelContract(long id) {
                return contratos.get(id);
            }
        };

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        System.out.println("teardown");
    }

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public HotelAvailabilityTest(String testName )
    {
        super( testName );
        System.out.println("hola");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        System.out.println("xxx");

        // assume SLF4J is bound to logback in the current environment
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        // print logback's internal status
        StatusPrinter.print(lc);


        return new TestSuite( HotelAvailabilityTest.class );

    }







    public void testHotelActivo() {

        hotel.setActive(false);

        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010101, 21010115, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("NOTAVAILABLE", rs.getBestDeal());


        hotel.setActive(true);

        rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertNotSame("NOTAVAILABLE", rs.getBestDeal());

    }

    public void testAgenciaActiva() {

        agencia.setActive(false);

        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010101, 21010115, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("NOTAVAILABLE", rs.getBestDeal());


        agencia.setActive(true);

        rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertNotSame("NOTAVAILABLE", rs.getBestDeal());

    }

    public void testParo() {

        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010115, 21010122, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("NOTAVAILABLE", rs.getBestDeal());

        rq = new DispoRQ(LocalDate.now(), 21010101, 21010115, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertNotSame("NOTAVAILABLE", rs.getBestDeal());

    }


    public void testPrecioBasePax01() {

        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010101, 21010115, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("280.0 EUR", rs.getBestDeal());

    }

    public void testPrecioBasePax02() {

        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010127, 21010210, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("370.0 EUR", rs.getBestDeal());

    }

    public void testPrecioUsoIndividual01() {

        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010101, 21010110, Lists.newArrayList(new Occupancy(1, 1, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("126.0 EUR", rs.getBestDeal());

    }

    public void testPrecioUsoIndividual02() {

        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010201, 21010210, Lists.newArrayList(new Occupancy(1, 1, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("135.0 EUR", rs.getBestDeal());

    }

    public void testPrecioTercerPax01() {

        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010101, 21010110, Lists.newArrayList(new Occupancy(1, 3, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("252.0 EUR", rs.getBestDeal());

    }

    public void testPrecioTercerPax02() {

        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010201, 21010210, Lists.newArrayList(new Occupancy(1, 3, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("405.0 EUR", rs.getBestDeal());

    }

    public void testPrecioPrimerNino01() {

        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010101, 21010110, Lists.newArrayList(new Occupancy(1, 3, new int[] {5})), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("207.0 EUR", rs.getBestDeal());

    }

    public void testPrecioPrimerNino02() {

        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010201, 21010210, Lists.newArrayList(new Occupancy(1, 3, new int[] {5})), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("405.0 EUR", rs.getBestDeal());

    }

    public void testPrecioPrimerNino03() {


        rooms.get("dbl").setMinAdultsForChildDiscount(3);
        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010101, 21010110, Lists.newArrayList(new Occupancy(1, 3, new int[] {5})), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);
        rooms.get("dbl").setMinAdultsForChildDiscount(2);

        assertEquals("252.0 EUR", rs.getBestDeal()); // aplica el descuento de tercer pax (-3 eur) en lugar del de primer niño (

    }

    public void testPrecioSegundoNino01() {


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010101, 21010110, Lists.newArrayList(new Occupancy(1, 4, new int[] {5, 4})), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("261.0 EUR", rs.getBestDeal()); // aplica el descuento de tercer pax (-3 eur) en lugar del de primer niño (

    }

    public void testPrecioSegundoNino02() {


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010201, 21010210, Lists.newArrayList(new Occupancy(1, 4, new int[] {5, 4})), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("540.0 EUR", rs.getBestDeal());

    }

    public void testPrecioBebe01() {


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010201, 21010210, Lists.newArrayList(new Occupancy(1, 3, new int[] {1})), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("270.0 EUR", rs.getBestDeal()); // por defecto el bebé es gratis, solo 2 adultos a 15 eur/noche

    }

    public void testPrecioBebe02() {


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010101, 21010110, Lists.newArrayList(new Occupancy(1, 3, new int[] {1})), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("189.0 EUR", rs.getBestDeal()); // aquí el primer bebé paga 1 euro

    }

    public void testPrecioJunior01() {


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010201, 21010210, Lists.newArrayList(new Occupancy(1, 3, new int[] {12})), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("405.0 EUR", rs.getBestDeal()); // por defecto el bebé es gratis, solo 2 adultos a 15 eur/noche

    }

    public void testPrecioJunior02() {


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010101, 21010110, Lists.newArrayList(new Occupancy(1, 3, new int[] {12})), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("252.0 EUR", rs.getBestDeal()); // aquí el primer bebé paga 1 euro

    }

    public void testPrecioJunior03() {

        hotel.setJuniorStartAge(0);
        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010101, 21010110, Lists.newArrayList(new Occupancy(1, 3, new int[] {12})), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("207.0 EUR", rs.getBestDeal()); // aquí el primer bebé paga 1 euro

    }

    public void testPrecioSuplemento01() {


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010301, 21010310, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("660.0 EUR", rs.getBestDeal()); // por defecto el bebé es gratis, solo 2 adultos a 15 eur/noche

    }

    public void testPrecioSuplemento02() {


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010315, 21010320, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("800.0 EUR", rs.getBestDeal()); // por defecto el bebé es gratis, solo 2 adultos a 15 eur/noche

    }

    public void testPrecioSuplemento03() {


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010321, 21010326, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("800.0 EUR", rs.getBestDeal()); // por defecto el bebé es gratis, solo 2 adultos a 15 eur/noche

    }

    public void testPrecioSuplemento04() {


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010327, 21010328, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("260.0 EUR", rs.getBestDeal()); // por defecto el bebé es gratis, solo 2 adultos a 15 eur/noche

    }


    public void testPrecioOferta01() {

        {
            DiscountOffer o;
            hotel.getOffers().add(o = new DiscountOffer());
            o.setHotel(hotel);

            o.setName("Oferta descuento 30%");
            o.setId(1);
            o.setActive(true);
            o.getStayDates().getRanges().add(new DatesRange(LocalDate.of(2101, 4, 1), LocalDate.of(2101, 4, 30)));
            o.setOnBoardBasis(true);
            o.setOnDiscounts(true);
            o.setOnRoom(true);
            o.setPercent(true);
            o.setValue(30);
            o.setScope(Scope.BOOKING);
            o.setPer(Per.BOOKING);
            o.setPrepayment(false);
        }


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010401, 21010406, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("210.0 EUR", rs.getBestDeal()); // por defecto el bebé es gratis, solo 2 adultos a 15 eur/noche

    }

    public void testPrecioOferta02() {

        {
            EarlyBookingOffer o;
            hotel.getOffers().add(o = new EarlyBookingOffer());
            o.setHotel(hotel);

            o.setName("Oferta early booking");
            o.setId(1);
            o.setActive(true);
            o.getStayDates().getRanges().add(new DatesRange(LocalDate.of(2101, 4, 1), LocalDate.of(2101, 4, 30)));
            o.setOnBoardBasis(true);
            o.setOnDiscounts(true);
            o.setOnRoom(true);

            o.getLines().getLines().add(new EarlyBookingOfferLine(120, 25));
            o.getLines().getLines().add(new EarlyBookingOfferLine(60, 15));

            o.setPrepayment(false);
        }


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010401, 21010406, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("225.0 EUR", rs.getBestDeal()); // por defecto el bebé es gratis, solo 2 adultos a 15 eur/noche

    }

    public void testPrecioOferta03() {

        {
            StayAndPayOffer o;
            hotel.getOffers().add(o = new StayAndPayOffer());
            o.setHotel(hotel);

            o.setName("Oferta 4 x 3");
            o.setId(1);
            o.setActive(true);
            o.getStayDates().getRanges().add(new DatesRange(LocalDate.of(2101, 4, 1), LocalDate.of(2101, 4, 30)));
            o.setOnBoardBasis(true);
            o.setOnDiscounts(true);
            o.setOnRoom(true);

            o.setStayNights(4);
            o.setPayNights(3);
            o.setWhichNights(WhichNights.FIRST);

            o.setPrepayment(false);
        }


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010401, 21010406, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("240.0 EUR", rs.getBestDeal()); // por defecto el bebé es gratis, solo 2 adultos a 15 eur/noche

    }


    public void testPrecioOferta04() {

        {
            FreeChildrenOffer o;
            hotel.getOffers().add(o = new FreeChildrenOffer());
            o.setHotel(hotel);

            o.setName("Oferta niños gratis");
            o.setId(1);
            o.setActive(true);
            o.getStayDates().getRanges().add(new DatesRange(LocalDate.of(2101, 4, 1), LocalDate.of(2101, 4, 30)));
            o.setOnBoardBasis(true);
            o.setOnDiscounts(true);
            o.setOnRoom(true);

            o.setPrepayment(false);
        }


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010401, 21010406, Lists.newArrayList(new Occupancy(1, 3, new int[] {5})), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        assertEquals("300.0 EUR", rs.getBestDeal());

    }

    public void testPrecioOferta05() {

        {



            BoardFare bf;
            fareAbril.getLines().add(new LinearFareLine("DBL", "MP", 0, 50, 0, 0, 0));

            BoardUpgradeOffer o;
            hotel.getOffers().add(o = new BoardUpgradeOffer());
            o.setHotel(hotel);

            o.setName("Oferta mejor régimen");
            o.setId(1);
            o.setActive(true);
            o.getStayDates().getRanges().add(new DatesRange(LocalDate.of(2101, 4, 1), LocalDate.of(2101, 4, 30)));
            o.setOnBoardBasis(true);
            o.setOnDiscounts(true);
            o.setOnRoom(true);

            o.setGet(boardTypes.get("mp"));
            o.setPay(boardTypes.get("sa"));

            o.setPrepayment(false);
        }


        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010401, 21010406, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        double precio = 0;
        for (Option o : rs.getOptions()) {
            for (BoardPrice bp : o.getPrices()) {
                System.out.println("" + bp.getBoardBasisId() + "=" + bp.getNetPrice().getValue());
                if ("mp".equalsIgnoreCase(bp.getBoardBasisId())) precio = bp.getNetPrice().getValue();
            }
        }

        assertEquals(300.0, precio);

    }


    public void testPrecioOferta06() {

        {
            LinearFare f = fareAbril;
            f.getLines().add(new LinearFareLine("SUI", "SA", 0, 100, 0, 0, 0));
        }

        {

            DispoRQ rq = new DispoRQ(LocalDate.now(), 21010401, 21010406, Lists.newArrayList(new Occupancy(1, 2, null)), false);
            AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

            double precio = 0;
            for (Option o : rs.getOptions()) {
                if ("sui".equalsIgnoreCase(o.getDistribution().get(0).getRoomId())) {
                    for (BoardPrice bp : o.getPrices()) {
                        System.out.println("" + o.getDistribution().get(0).getRoomId() + " en " + bp.getBoardBasisId() + "=" + bp.getNetPrice().getValue());
                        if ("sa".equalsIgnoreCase(bp.getBoardBasisId())) precio = bp.getNetPrice().getValue();
                    }
                }
            }

            assertEquals(1000.0, precio);

        }



        {

            RoomUpgradeOffer o;
            hotel.getOffers().add(o = new RoomUpgradeOffer());
            o.setHotel(hotel);

            o.setName("Oferta mejor habitación");
            o.setId(1);
            o.setActive(true);
            o.getStayDates().getRanges().add(new DatesRange(LocalDate.of(2101, 4, 1), LocalDate.of(2101, 4, 30)));
            o.setOnBoardBasis(true);
            o.setOnDiscounts(true);
            o.setOnRoom(true);

            o.setGet(rooms.get("sui"));
            o.setPay(rooms.get("dbl"));

            o.setPrepayment(false);
        }


        {
            DispoRQ rq = new DispoRQ(LocalDate.now(), 21010401, 21010406, Lists.newArrayList(new Occupancy(1, 2, null)), false);
            AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

            double precio = 0;
            for (Option o : rs.getOptions()) {
                if ("sui".equalsIgnoreCase(o.getDistribution().get(0).getRoomId())) {
                    for (BoardPrice bp : o.getPrices()) {
                        System.out.println("" + o.getDistribution().get(0).getRoomId() + " en " + bp.getBoardBasisId() + "=" + bp.getNetPrice().getValue());
                        if ("sa".equalsIgnoreCase(bp.getBoardBasisId())) precio = bp.getNetPrice().getValue();
                    }
                }
            }

            assertEquals(300.0, precio);
        }


    }


    public void testPrecioOferta07() {

        {

            PriceOffer o;
            hotel.getOffers().add(o = new PriceOffer());
            o.setHotel(hotel);

            o.setName("Oferta mejor habitación");
            o.setId(1);
            o.setActive(true);
            o.getStayDates().getRanges().add(new DatesRange(LocalDate.of(2101, 4, 1), LocalDate.of(2101, 4, 30)));
            o.setOnBoardBasis(true);
            o.setOnDiscounts(true);
            o.setOnRoom(true);

            o.setFare(new LinearFareLine("DBL", "SA", 0, 10, 0, 0, 0));

            o.setPrepayment(false);
        }



        DispoRQ rq = new DispoRQ(LocalDate.now(), 21010401, 21010406, Lists.newArrayList(new Occupancy(1, 2, null)), false);
        AvailableHotel rs = new HotelAvailabilityRunner().check(agencia, hotel, 1, 1, modelo, rq);

        double precio = 0;
        for (Option o : rs.getOptions()) {
            if ("dbl".equalsIgnoreCase(o.getDistribution().get(0).getRoomId())) {
                for (BoardPrice bp : o.getPrices()) {
                    System.out.println("" + o.getDistribution().get(0).getRoomId() + " en " + bp.getBoardBasisId() + "=" + bp.getNetPrice().getValue());
                    if ("sa".equalsIgnoreCase(bp.getBoardBasisId())) precio = bp.getNetPrice().getValue();
                }
            }
        }

        assertEquals(100.0, precio);

    }

}
