package io.mateu.erp.model.population;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import io.mateu.erp.model.accounting.AccountingPlan;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.invoicing.InvoiceSerial;
import io.mateu.erp.model.organization.Company;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.partners.AgencyStatus;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.partners.ProviderStatus;
import io.mateu.erp.model.payments.Account;
import io.mateu.erp.model.payments.MethodOfPayment;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.ProductType;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.generic.Contract;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.erp.model.product.generic.Price;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.tour.*;
import io.mateu.erp.model.product.transfer.*;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.world.Resort;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.Permission;
import io.mateu.mdd.core.model.authentication.USER_STATUS;
import io.mateu.mdd.core.model.common.Resource;
import io.mateu.mdd.core.model.config.TemplateUseCase;
import io.mateu.mdd.core.model.multilanguage.Literal;
import io.mateu.mdd.core.model.util.Constants;
import io.mateu.mdd.core.util.DatesRange;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Map;

/**
 * used to populate a database with initial values
 *
 * Created by miguel on 13/9/16.
 */
public class Populator extends io.mateu.mdd.core.model.population.Populator {

    public static final String USER_ADMIN = "admin";

    public static Agency agencia;
    public static Provider proveedor;
    public static PointOfSale pos;
    public static Office office;
    public static ProductLine prodLine;
    public static GenericProduct genericProduct;
    public static Resort alcudia;
    public static Resort pmi;
    public static TransferPoint apt;
    public static TransferPoint hotelEnAlcudia;
    public static Hotel hotel;
    public static Excursion excursion;
    public static Account banco;
    public static MethodOfPayment visa;
    public static Currency eur;
    public static Currency usd;
    public static Currency gbp;

    public static void main(String... args) throws Throwable {

        new Populator().populate(io.mateu.erp.model.config.AppConfig.class);

    }

    public static void populateGenericProduct() throws Throwable {
        System.out.println("Populating sample generic product");

        Helper.transact(em -> {

            ProductType t = new ProductType();
            t.setName("Alquiler coches");
            em.persist(t);


            genericProduct = new GenericProduct();
            genericProduct.setType(t);
            genericProduct.setResort(office.getResort());
            genericProduct.setOffice(office);
            genericProduct.setName("Renault Megane");
            genericProduct.setActive(true);
            genericProduct.setUnitsDependant(true);
            //genericProduct.setProvidedBy();
            em.persist(genericProduct);



            //v enta

            Contract c = new Contract();
            c.setActive(true);
            c.setAudit(new Audit(MDD.getCurrentUser()));
            c.setBillingConcept(em.find(BillingConcept.class, "ANY"));
            c.setCurrency(em.find(Currency.class, "EUR"));
            c.setOffice(office);
            c.setProductLine(prodLine);
            c.setRatesType(RatesType.NET);
            c.setTitle("Contrato Venta Genéricos 2019");
            c.setType(ContractType.SALE);
            c.setValidFrom(LocalDate.of(2019, 1, 1));
            c.setValidTo(LocalDate.of(2019, 12, 31));
            c.setVATIncluded(true);


            Price p;
            c.getPrices().add(p = new Price());
            p.setContract(c);
            p.setActive(true);
            p.setProduct(genericProduct);
            p.setDescription("Alquiler");
            p.setPricePerUnitAndDay(50.2);

            em.persist(c);



            // compra


            c = new Contract();
            c.setActive(true);
            c.setAudit(new Audit(MDD.getCurrentUser()));
            c.setBillingConcept(em.find(BillingConcept.class, "ANY"));
            c.setCurrency(em.find(Currency.class, "EUR"));
            c.setOffice(office);
            c.setProductLine(prodLine);
            c.setRatesType(RatesType.NET);
            c.setTitle("Contrato Compra Genéricos 2019");
            c.setType(ContractType.PURCHASE);
            c.setValidFrom(LocalDate.of(2019, 1, 1));
            c.setValidTo(LocalDate.of(2019, 12, 31));
            c.setVATIncluded(true);
            c.setSupplier(proveedor);


            c.getPrices().add(p = new Price());
            p.setContract(c);
            p.setActive(true);
            p.setProduct(genericProduct);
            p.setDescription("Alquiler");
            p.setPricePerUnitAndDay(30.1);

            em.persist(c);


        });

    }

    public static void populateTransferProduct() throws Throwable {
        System.out.println("Populating sample transfer product");

        Helper.transact(em -> {


            Zone z1 = new Zone();
            z1.setName("Zona aeropuerto");
            z1.getResorts().add(pmi);
            em.persist(z1);

            Zone z2 = new Zone();
            z2.setName("Cualquier punto isla");
            z2.getResorts().add(alcudia);
            em.persist(z2);

            Vehicle v = new Vehicle();
            v.setName("TAXI");
            v.setMinPax(1);
            v.setMaxPax(4);
            em.persist(v);


            // venta

            io.mateu.erp.model.product.transfer.Contract c = new io.mateu.erp.model.product.transfer.Contract();
            c.setActive(true);
            c.setAudit(new Audit(MDD.getCurrentUser()));
            c.setBillingConcept(em.find(BillingConcept.class, "TRA"));
            c.setCurrency(em.find(Currency.class, "EUR"));
            c.setOffice(office);
            c.setProductLine(prodLine);
            c.setRatesType(RatesType.NET);
            c.setTitle("Contrato Venta Traslados 2019");
            c.setType(ContractType.SALE);
            c.setValidFrom(LocalDate.of(2019, 1, 1));
            c.setValidTo(LocalDate.of(2019, 12, 31));
            c.setVATIncluded(true);


            io.mateu.erp.model.product.transfer.Price p;
            c.getPrices().add(p = new io.mateu.erp.model.product.transfer.Price());
            p.setContract(c);
            p.setVehicle(v);
            p.setTransferType(TransferType.PRIVATE);
            p.setPricePer(PricePer.SERVICE);
            p.setPrice(60.4);
            p.setOrigin(z1);
            p.setDestination(z2);

            em.persist(c);


            // venta

            c = new io.mateu.erp.model.product.transfer.Contract();
            c.setActive(true);
            c.setAudit(new Audit(MDD.getCurrentUser()));
            c.setBillingConcept(em.find(BillingConcept.class, "ANY"));
            c.setCurrency(em.find(Currency.class, "EUR"));
            c.setOffice(office);
            c.setProductLine(prodLine);
            c.setRatesType(RatesType.NET);
            c.setTitle("Contrato Compra Traslados 2019");
            c.setType(ContractType.PURCHASE);
            c.setValidFrom(LocalDate.of(2019, 1, 1));
            c.setValidTo(LocalDate.of(2019, 12, 31));
            c.setVATIncluded(true);

            c.setSupplier(proveedor);


            c.getPrices().add(p = new io.mateu.erp.model.product.transfer.Price());
            p.setContract(c);
            p.setVehicle(v);
            p.setTransferType(TransferType.PRIVATE);
            p.setPricePer(PricePer.SERVICE);
            p.setPrice(20.5);
            p.setOrigin(z1);
            p.setDestination(z2);

            em.persist(c);


        });
    }

    public static void populateHotelProduct() throws Throwable {

        System.out.println("Populating sample hotel product");

        Helper.transact(em -> {

            ProductType t = new ProductType();
            t.setName("Hotel");
            em.persist(t);

            HotelType ht = new HotelType();
            ht.setName("Hotel ciudad");
            em.persist(ht);

            HotelCategory hc = new HotelCategory();
            hc.setCode("***");
            hc.setStars(3);
            hc.setName(new Literal("3 stars", "3 estrellas"));
            em.persist(hc);

            hotel = new Hotel();
            hotel.setHotelType(ht);
            hotel.setType(t);
            hotel.setCategory(hc);
            hotel.setResort(office.getResort());
            hotel.setOffice(office);
            hotel.setName("Hotel Valparaiso");
            hotel.setActive(true);
            //genericProduct.setProvidedBy();
            em.persist(hotel);

            Board b;
            hotel.getBoards().add(b = new Board());
            b.setHotel(hotel);
            b.setType(em.find(BoardType.class, "BB"));
            b.setDescription(new Literal("bla bla bla", "bla bla bla"));

            Room r;
            hotel.getRooms().add(r = new Room());
            r.setHotel(hotel);
            r.setType(em.find(RoomType.class, "DBL"));
            r.setDescription(new Literal("bla bla bla", "bla bla bla"));
            r.setMinPax(1);
            r.setMaxCapacities(new MaxCapacities());
            r.getMaxCapacities().getCapacities().add(new MaxCapacity(3, 0, 0));

            Inventory i = new Inventory();
            i.setHotel(hotel);
            i.setName("Cupo 2019");
            em.persist(i);

            // venta

            HotelContract c = new HotelContract();
            c.setActive(true);
            c.setAudit(new Audit(MDD.getCurrentUser()));
            c.setBillingConcept(em.find(BillingConcept.class, "HOT"));
            c.setCurrency(em.find(Currency.class, "EUR"));
            c.setOffice(office);
            c.setProductLine(prodLine);
            c.setRatesType(RatesType.NET);
            c.setTitle("Contrato Venta Hotel Valparaiso 2019");
            c.setType(ContractType.SALE);
            c.setValidFrom(LocalDate.of(2019, 1, 1));
            c.setValidTo(LocalDate.of(2019, 12, 31));
            c.setVATIncluded(true);
            c.setInventory(i);
            i.getContracts().add(c);
            c.setHotel(hotel);
            hotel.getContracts().add(c);

            HotelContractPhoto terms = new HotelContractPhoto();

            LinearFare lf;
            terms.getFares().add(lf = new LinearFare(terms));
            lf.setName("T1");
            lf.getDates().add(new DatesRange(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31)));

            LinearFareLine lfl;
            lf.getLines().add(lfl = new LinearFareLine(lf));

            lfl.setRoomTypeCode(em.find(RoomType.class, "DBL"));
            lfl.setBoardTypeCode(em.find(BoardType.class, "BB"));
            lfl.setAdultPrice(30.5);


            terms.getAllotment().add(new Allotment(terms, "DBL", LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31), 30));


            c.setTerms(terms);


            em.persist(c);



            // compra

            c = new HotelContract();
            c.setActive(true);
            c.setAudit(new Audit(MDD.getCurrentUser()));
            c.setBillingConcept(em.find(BillingConcept.class, "HOT"));
            c.setCurrency(em.find(Currency.class, "EUR"));
            c.setOffice(office);
            c.setProductLine(prodLine);
            c.setRatesType(RatesType.NET);
            c.setTitle("Contrato Compra Hotel Valparaiso 2019");
            c.setType(ContractType.PURCHASE);
            c.setValidFrom(LocalDate.of(2019, 1, 1));
            c.setValidTo(LocalDate.of(2019, 12, 31));
            c.setVATIncluded(true);
            c.setInventory(i);
            i.getContracts().add(c);
            c.setHotel(hotel);
            hotel.getContracts().add(c);


            c.setSupplier(proveedor);

            terms = new HotelContractPhoto();

            terms.getFares().add(lf = new LinearFare(terms));
            lf.setName("T1");
            lf.getDates().add(new DatesRange(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31)));

            lf.getLines().add(lfl = new LinearFareLine(lf));

            lfl.setRoomTypeCode(em.find(RoomType.class, "DBL"));
            lfl.setBoardTypeCode(em.find(BoardType.class, "BB"));
            lfl.setAdultPrice(20.1);


            terms.getAllotment().add(new Allotment(terms, "DBL", LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31), 30));


            c.setTerms(terms);


            em.persist(c);


        });

    }

    public static void populateExcursionProduct() throws Throwable {

        System.out.println("Populating sample excursion product");

        Helper.transact(em -> {

            ProductType t = new ProductType();
            t.setName("Excursión Isla Saona");
            em.persist(t);


            excursion = new Excursion();
            excursion.setType(t);
            excursion.setResort(office.getResort());
            excursion.setOffice(office);
            excursion.setName("Excursión Isla Saona");
            excursion.setActive(true);
            excursion.setDuration(TourDuration.WHOLEDAY);
            ExcursionShift s;
            excursion.getShifts().add(s = new ExcursionShift());
            s.setTour(excursion);
            s.setName("Turno único");
            s.setWeekdays(new boolean[] {true, true, true, true, true, true, true});
            //genericProduct.setProvidedBy();
            Variant v;
            excursion.getVariants().add(v = new Variant());
            v.setProduct(excursion);
            v.setName(new Literal("No variant", "Sin variantes"));
            v.setDescription(new Literal("---", "---"));


            // coste 1
            t = new ProductType();
            t.setName("Barco");
            em.persist(t);

            GenericProduct p = new GenericProduct();
            p.setType(t);
            p.setResort(office.getResort());
            p.setOffice(office);
            p.setName("Barco a Isla Saona (ida y vuelta)");
            p.setActive(true);
            p.setAdultsDependant(true);
            //genericProduct.setProvidedBy();
            Variant vp;
            p.getVariants().add(vp = new Variant());
            vp.setProduct(p);
            vp.setName(new Literal("No variant", "Sin variantes"));
            vp.setDescription(new Literal("---", "---"));
            em.persist(p);



            TourCost coste;
            excursion.getCosts().add(coste = new TourCost());
            coste.setTour(excursion);
            coste.setVariant(excursion.getVariants().get(0));
            coste.setOrder(1);
            coste.setProduct(p);
            coste.setProductVariant(p.getVariants().get(0));
            coste.setType(p.getType());

            // coste 2
            t = new ProductType();
            t.setName("Restaurante");
            em.persist(t);

            p = new GenericProduct();
            p.setType(t);
            p.setResort(office.getResort());
            p.setOffice(office);
            p.setName("Comida en Isla Saona");
            p.setActive(true);
            p.setAdultsDependant(true);
            p.setChildrenDependant(true);
            p.setProvidedBy(proveedor);
            //genericProduct.setProvidedBy();
            p.getVariants().add(vp = new Variant());
            vp.setProduct(p);
            vp.setName(new Literal("No variant", "Sin variantes"));
            vp.setDescription(new Literal("---", "---"));
            em.persist(p);



            excursion.getCosts().add(coste = new TourCost());
            coste.setTour(excursion);
            coste.setVariant(excursion.getVariants().get(0));
            coste.setOrder(1);
            coste.setProduct(p);
            coste.setProductVariant(p.getVariants().get(0));
            coste.setType(p.getType());

            // coste 3
            t = new ProductType();
            t.setName("Guía oficial");
            em.persist(t);

            p = new GenericProduct();
            p.setType(t);
            p.setResort(office.getResort());
            p.setOffice(office);
            p.setName("Guía oficial");
            p.setActive(true);
            p.setUnitsDependant(true);
            //genericProduct.setProvidedBy();
            p.getVariants().add(vp = new Variant());
            vp.setProduct(p);
            vp.setName(new Literal("No variant", "Sin variantes"));
            vp.setDescription(new Literal("---", "---"));
            em.persist(p);



            excursion.getCosts().add(coste = new TourCost());
            coste.setTour(excursion);
            coste.setVariant(excursion.getVariants().get(0));
            coste.setOrder(1);
            coste.setProduct(p);
            coste.setProductVariant(p.getVariants().get(0));
            coste.setType(p.getType());


            em.persist(excursion);




            // venta

            io.mateu.erp.model.product.tour.Contract c = new io.mateu.erp.model.product.tour.Contract();
            c.setActive(true);
            c.setAudit(new Audit(MDD.getCurrentUser()));
            c.setBillingConcept(em.find(BillingConcept.class, "ANY"));
            c.setCurrency(em.find(Currency.class, "EUR"));
            c.setOffice(office);
            c.setProductLine(prodLine);
            c.setRatesType(RatesType.NET);
            c.setTitle("Contrato Venta Excursiones 2019");
            c.setType(ContractType.SALE);
            c.setValidFrom(LocalDate.of(2019, 1, 1));
            c.setValidTo(LocalDate.of(2019, 12, 31));
            c.setVATIncluded(true);


            TourPrice precio;
            c.getPrices().add(precio = new TourPrice());
            precio.setContract(c);
            precio.setActive(true);
            precio.setTour(excursion);
            precio.setVariant(excursion.getVariants().get(0));
            precio.setDescription("Precio único");
            precio.setPricePerAdult(50.2);

            em.persist(c);



            // compra


            c = new io.mateu.erp.model.product.tour.Contract();
            c.setActive(true);
            c.setAudit(new Audit(MDD.getCurrentUser()));
            c.setBillingConcept(em.find(BillingConcept.class, "ANY"));
            c.setCurrency(em.find(Currency.class, "EUR"));
            c.setOffice(office);
            c.setProductLine(prodLine);
            c.setRatesType(RatesType.NET);
            c.setTitle("Contrato Compra Excursiones 2019");
            c.setType(ContractType.PURCHASE);
            c.setValidFrom(LocalDate.of(2019, 1, 1));
            c.setValidTo(LocalDate.of(2019, 12, 31));
            c.setVATIncluded(true);
            c.setSupplier(proveedor);


            c.getPrices().add(precio = new TourPrice());
            precio.setContract(c);
            precio.setActive(true);
            precio.setTour(excursion);
            precio.setVariant(excursion.getVariants().get(0));
            precio.setDescription("Precio único");
            precio.setPricePerAdult(30.1);

            em.persist(c);


        });


    }

    public void populate(Class appConfigClass) throws Throwable {

        System.out.println("Populating database...");


        //authentication
        Helper.transact((JPATransaction) (em)->{

            Map<String, Object> initialData = null;
            String initialDataPath = System.getProperty("initialdata", "/home/miguel/work/initialdata.yml");
            try {
                initialData = Helper.fromYaml(Files.toString(new java.io.File(initialDataPath), Charset.defaultCharset()));
            } catch (Exception e) {
                System.out.println("No initial data found at " + initialDataPath);
            }


            if (true) {
                eur = new Currency();
                eur.setIsoCode("EUR");
                eur.setIsoNumericCode(978);
                eur.setName("Euro");
                eur.setExchangeRateToNucs(1);
                em.persist(eur);
            }

            io.mateu.erp.model.config.AppConfig c = (io.mateu.erp.model.config.AppConfig) appConfigClass.newInstance();
            c.setId(1);

            c.setXslfoForTransferContract(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/contrato_transfer.xsl"), Charsets.UTF_8));
            c.setXslfoForHotelContract(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/contrato_hotel.xsl"), Charsets.UTF_8));
            c.setXslfoForGenericContract(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/contrato_generico.xsl"), Charsets.UTF_8));
            c.setXslfoForTourContract(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/contrato_tour.xsl"), Charsets.UTF_8));
            c.setXslfoForWorld(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/world.xsl"), Charsets.UTF_8));
            c.setXslfoForList(Resources.toString(Resources.getResource(Populator.class, "/xsl/listing.xsl"), Charsets.UTF_8));
            c.setXslfoForPOSSettlement(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/liquidacion_pos.xsl"), Charsets.UTF_8));
            c.setXslfoForEventManifest(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/event_manifest.xsl"), Charsets.UTF_8));
            c.setXslfoForEventReport(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/event_report.xsl"), Charsets.UTF_8));
            c.setXslfoForQuotationRequest(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/grupo.xsl"), Charsets.UTF_8));
            c.setXslfoForIssuedInvoice(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/factura.xsl"), Charsets.UTF_8));
            c.setXslfoForPurchaseOrder(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/please_book.xsl"), Charsets.UTF_8));
            c.setXslfoForTransfersList(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/transfers_list.xsl"), Charsets.UTF_8));
            c.setXslfoForVoucher(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/voucher.xsl"), Charsets.UTF_8));
            c.setXslfoForObject(Resources.toString(Resources.getResource(Populator.class, "/xsl/object.xsl"), Charsets.UTF_8));

            c.setPickupEmailTemplate(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/freemarker/pickupemail.ftl"), Charsets.UTF_8));
            c.setPickupSmsTemplate(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/freemarker/pickupsms.ftl"), Charsets.UTF_8));
            c.setPickupSmsTemplateEs(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/freemarker/pickupsmses.ftl"), Charsets.UTF_8));
            c.setPurchaseOrderTemplate(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/freemarker/purchaseorder.ftl"), Charsets.UTF_8));
            c.setVouchersEmailTemplate(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/freemarker/voucheremail.ftl"), Charsets.UTF_8));
            c.setPaymentEmailTemplate(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/freemarker/paymentemail.ftl"), Charsets.UTF_8));
            c.setBookedEmailTemplate(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/freemarker/bookingemail.ftl"), Charsets.UTF_8));


            c.setAdminEmailSmtpHost((String) Helper.get(initialData, "smtp/host"));
            c.setAdminEmailFrom((String) Helper.get(initialData, "smtp/user"));
            c.setAdminEmailPassword((String) Helper.get(initialData, "smtp/password"));
            c.setAdminEmailSmtpPort((Integer) Helper.get(initialData, "smtp/port", 0));
            c.setAdminEmailUser((String) Helper.get(initialData, "smtp/user"));
            c.setAdminEmailCC((String) Helper.get(initialData, "smtp/cc"));


            c.setNucCurrency(eur);


            em.persist(c);

            c.createDummyDates();


            // create super admin permission
            int pid = 1;
            Permission p = new Permission();
            p.setId(pid++);
            p.setName("Super admin");
            em.persist(p);


            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Financial");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Booking");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Operations");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Portfolio");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Biz");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("CMS");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Management");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Utils");
                em.persist(px);
            }


            {
                // create user admin
                ERPUser u = new ERPUser();
                u.setLogin(USER_ADMIN);
                u.setName("Admin");
                //u.setPassword(Helper.md5("1"));
                u.setEmail("miguelperezcolom@gmail.com");
                u.setPassword("1");
                u.setStatus(USER_STATUS.ACTIVE);
                u.getPermissions().add(p);
                Resource f;
                u.setPhoto(f = new Resource());
                f.setName("foto-perfil-ejemplo.png");
                f.setBytes(ByteStreams.toByteArray(Populator.class.getResourceAsStream("/images/" + f.getName())));
                em.persist(f);
                em.persist(u);
            }

            {
                // create user admin
                ERPUser u = new ERPUser();
                u.setLogin(Constants.SYSTEM_USER_LOGIN);
                u.setName("System");
                //u.setPassword(Helper.md5("1"));
                u.setEmail("miguelperezcolom@gmail.com");
                u.setPassword("1");
                u.setStatus(USER_STATUS.ACTIVE);
                u.getPermissions().add(p);
                Resource f;
                u.setPhoto(f = new Resource());
                f.setName("foto-perfil-ejemplo.png");
                f.setBytes(ByteStreams.toByteArray(Populator.class.getResourceAsStream("/images/" + f.getName())));
                em.persist(f);
                em.persist(u);
            }

            {
                // create user admin
                ERPUser u = new ERPUser();
                u.setLogin(Constants.IMPORTING_USER_LOGIN);
                u.setName("Importing User");
                //u.setPassword(Helper.md5("1"));
                u.setEmail("miguelperezcolom@gmail.com");
                u.setPassword("1");
                u.setStatus(USER_STATUS.ACTIVE);
                u.getPermissions().add(p);
                Resource f;
                u.setPhoto(f = new Resource());
                f.setName("foto-perfil-ejemplo.png");
                f.setBytes(ByteStreams.toByteArray(Populator.class.getResourceAsStream("/images/" + f.getName())));
                em.persist(f);
                em.persist(u);
            }


            {
                TemplateUseCase tuc = new TemplateUseCase();
                tuc.setName("User");
                em.persist(tuc);
            }

            {
                TemplateUseCase tuc = new TemplateUseCase();
                tuc.setName("Partner");
                em.persist(tuc);
            }

            {
                TemplateUseCase tuc = new TemplateUseCase();
                tuc.setName("Booking");
                em.persist(tuc);
            }



            if (false) {
                Currency usd = new Currency();
                usd.setIsoCode("USD");
                usd.setIsoNumericCode(840);
                usd.setName("US Dollar");
                em.persist(usd);
            }








        });

        // multilanguage


        System.out.println("Database populated.");

    }


    public static void populateBaseForTests() {
        try {
            Helper.transact(em -> {

                eur = em.find(Currency.class, "EUR");

                {
                    usd = new Currency();
                    usd.setIsoCode("USD");
                    usd.setIsoNumericCode(117);
                    usd.setName("US dollar");
                    usd.setExchangeRateToNucs(0.875);
                    em.persist(usd);
                }

                {
                    gbp = new Currency();
                    gbp.setIsoCode("GBP");
                    gbp.setIsoNumericCode(875);
                    gbp.setName("GB pound");
                    gbp.setExchangeRateToNucs(1.423);
                    em.persist(gbp);
                }


                banco = new Account();
                banco.setCurrency(eur);
                banco.setName("Banco Santander 65465465465464");
                em.persist(banco);

                visa = new MethodOfPayment();
                visa.setName("VISA");
                em.persist(visa);


                io.mateu.erp.model.config.AppConfig ac = io.mateu.erp.model.config.AppConfig.get(em);

                BillingConcept bc = new BillingConcept();
                bc.setName("Anything");
                bc.setCode("ANY");
                bc.setLocalizationRule(LocalizationRule.ISSUING_COMPANY);
                em.persist(bc);
                ac.setBillingConceptForCircuit(bc);
                ac.setBillingConceptForExcursion(bc);
                ac.setBillingConceptForOthers(bc);


                bc = new BillingConcept();
                bc.setName("Hotel");
                bc.setCode("HOT");
                bc.setLocalizationRule(LocalizationRule.SERVICE);
                em.persist(bc);
                ac.setBillingConceptForHotel(bc);

                bc = new BillingConcept();
                bc.setName("Transfer");
                bc.setCode("TRA");
                bc.setLocalizationRule(LocalizationRule.SERVICE);
                em.persist(bc);
                ac.setBillingConceptForTransfer(bc);

                bc = new BillingConcept();
                bc.setName("Handling fee");
                bc.setCode("HAN");
                bc.setLocalizationRule(LocalizationRule.ISSUING_COMPANY);
                em.persist(bc);


                Country co = new Country();
                co.setIsoCode("ES");
                co.setName("Spain");
                em.persist(co);

                Destination s;
                co.getDestinations().add(s = new Destination());
                s.setCountry(co);
                s.setName("Mallorca");
                em.persist(s);

                Resort resort;
                s.getResorts().add(pmi = resort = new Resort());
                resort.setDestination(s);
                resort.setName("Palma");
                em.persist(resort);

                em.flush();


                AccountingPlan plan = new AccountingPlan();
                plan.setName("Accounting plan");
                plan.setCurrency(eur);
                em.persist(plan);

                FinancialAgent a = new FinancialAgent();
                a.setName("We.inc");
                a.setBusinessName("We.inc SLU");
                a.setAutomaticInvoiceBasis(AutomaticInvoiceBasis.NONE);
                a.setInvoiceGrouping(InvoiceGrouping.BOOKING);
                a.setRiskType(RiskType.CREDIT);
                a.setCurrency(eur);
                em.persist(a);

                InvoiceSerial serieFacturas = new InvoiceSerial();
                serieFacturas.setName("Facturas emitidas");
                serieFacturas.setNextNumber(1);
                serieFacturas.setPrefix("FACTEST-");
                em.persist(serieFacturas);


                InvoiceSerial serieAbonos = new InvoiceSerial();
                serieAbonos.setName("Abonos emitidos");
                serieAbonos.setNextNumber(1);
                serieAbonos.setPrefix("ABOCTEST-");
                em.persist(serieAbonos);


                Company cia = new Company();
                cia.setName("We");
                cia.setFinancialAgent(a);
                cia.setAccountingPlan(plan);
                cia.setBillingSerial(serieFacturas);
                cia.setSelfBillingSerial(serieAbonos);
                em.persist(cia);


                office = new Office();
                office.setName("Head office");
                office.setResort(pmi);
                office.setCurrency(eur);
                office.setCompany(cia);
                em.persist(office);

                pos = new PointOfSale();
                pos.setName("Point of sale");
                pos.setOffice(office);
                em.persist(pos);

                s.getResorts().add(alcudia = resort = new Resort());
                resort.setDestination(s);
                resort.setName("Alcudia");
                em.persist(resort);

                em.flush();

                resort.getTransferPoints().add(hotelEnAlcudia = new TransferPoint());
                hotelEnAlcudia.setResort(alcudia);
                hotelEnAlcudia.setName("Hotel Alcudiamar");
                hotelEnAlcudia.setType(TransferPointType.HOTEL);
                hotelEnAlcudia.setOffice(office);
                em.persist(hotelEnAlcudia);


                RoomType r = new RoomType();
                r.setCode("DBL");
                r.setName(new Literal("Double room", "Habitación doble"));
                em.persist(r);

                BoardType b = new BoardType();
                b.setCode("BB");
                b.setName(new Literal("Bed and breakfast", "Alojamiento y desayuno"));
                em.persist(b);


                prodLine = new ProductLine();
                prodLine.setName("Cualquier producto");
                em.persist(prodLine);




                resort.getTransferPoints().add(apt = new TransferPoint());
                apt.setResort(pmi);
                apt.setName("PMI");
                apt.setType(TransferPointType.AIRPORT);
                apt.setOffice(office);
                em.persist(apt);


                agencia = new Agency();
                agencia.setCurrency(eur);
                agencia.setStatus(AgencyStatus.ACTIVE);
                agencia.setEmail("miguelperezcolom@gmail.com");
                agencia.setDocumentationRequired(true);


                a = new FinancialAgent();
                a.setName("Muchoviaje");
                a.setName("Muchoviaje SLU");
                a.setAutomaticInvoiceBasis(AutomaticInvoiceBasis.NONE);
                a.setInvoiceGrouping(InvoiceGrouping.BOOKING);
                a.setRiskType(RiskType.CREDIT);
                a.setCurrency(eur);

                // directos. Se enviará email
                a.setDirectSale(true);

                em.persist(a);

                agencia.setFinancialAgent(a);
                agencia.setCompany(cia);
                agencia.setName("Muchoviaje");
                em.persist(agencia);

                em.flush();


                proveedor = new Provider();
                proveedor.setCurrency(eur);
                proveedor.setStatus(ProviderStatus.ACTIVE);
                proveedor.setEmail("miguelperezcolom@gmail.com");


                a = new FinancialAgent();
                a.setName("Transunion");
                a.setName("Transunion SLU");
                a.setAutomaticInvoiceBasis(AutomaticInvoiceBasis.NONE);
                a.setInvoiceGrouping(InvoiceGrouping.BOOKING);
                a.setRiskType(RiskType.CREDIT);
                a.setCurrency(eur);
                em.persist(a);

                proveedor.setFinancialAgent(a);
                proveedor.setName("Transunion");

                proveedor.setAutomaticOrderSending(true);
                proveedor.setAutomaticOrderConfirmation(true);
                proveedor.setOrdersSendingMethod(PurchaseOrderSendingMethod.EMAIL);
                proveedor.setSendOrdersTo("miguelperezcolom@gmail.com");
                em.persist(proveedor);

                em.flush();

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
