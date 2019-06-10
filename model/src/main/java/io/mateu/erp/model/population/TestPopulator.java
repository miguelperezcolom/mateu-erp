package io.mateu.erp.model.population;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.thedeanda.lorem.LoremIpsum;
import io.mateu.erp.model.accounting.AccountingPlan;
import io.mateu.erp.model.authentication.AgencyUser;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.authentication.CommissionAgentUser;
import io.mateu.erp.model.biz.Coupon;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.File;
import io.mateu.erp.model.booking.parts.*;
import io.mateu.erp.model.booking.tickets.Ticket;
import io.mateu.erp.model.booking.tickets.TicketBook;
import io.mateu.erp.model.booking.tickets.TicketStatus;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.invoicing.*;
import io.mateu.erp.model.organization.Company;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.organization.SalesPoint;
import io.mateu.erp.model.partners.*;
import io.mateu.erp.model.payments.*;
import io.mateu.erp.model.product.*;
import io.mateu.erp.model.product.generic.Extra;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.erp.model.product.generic.Price;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.tour.*;
import io.mateu.erp.model.product.transfer.*;
import io.mateu.erp.model.revenue.Markup;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.erp.model.taxes.VATPercent;
import io.mateu.erp.model.tpv.TPV;
import io.mateu.erp.model.tpv.TPVTYPE;
import io.mateu.erp.model.world.Airport;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.world.Resort;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.USER_STATUS;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.model.common.Resource;
import io.mateu.mdd.core.model.multilanguage.Literal;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.DatesRange;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class TestPopulator {


    public static void main(String[] args) {

        EmailHelper.setTesting(true);

        System.setProperty("appconf", "/home/miguel/work/quotravel.properties");
        System.setProperty("appconf", "/Users/miguel/quotravel.properties");

        try {
            Helper.runCommand("dropdb -U postgres quotravel; createdb -U postgres quotravel");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {

            new Populator().populate(AppConfig.class);

            populateEverything();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        WorkflowEngine.exit(0);

    }

    public static void populateEverything() throws Throwable {

        if (Helper.selectObjects("select x from " + Hotel.class.getName() + " x").size() == 0 || Helper.selectObjects("select x from " + Agency.class.getName() + " x").size() == 0) {

            EmailHelper.setTesting(true);

            completarAppConfig();


            createVats();

            crearCodigos();

            createCurrencies();

            createWorld();

            createPaymentStructure();

            crearPartners();

            crearProductos();

            crearComisiones();

            crearReservas();

            crearPagos();

            crearFacturas();

            crearTokens();

            crearCupones();

            EmailHelper.setTesting(false);

        } else throw new Exception("There already exist hotels or agencies in the database");

    }

    private static void crearPagos() throws Throwable {

        Helper.transact(em -> {

            User u = (User) Helper.selectObjects("select x from " + User.class.getName() + " x").get(0);

            Currency eur = em.find(Currency.class, "EUR");

            BankAccount banco = (BankAccount) Helper.selectObjects("select x from " + BankAccount.class.getName() + " x").get(0);


            MethodOfPayment visa = (MethodOfPayment) Helper.selectObjects("select x from " + MethodOfPayment.class.getName() + " x").get(0);

            List<Booking> bookings = em.createQuery("select x from " + Booking.class.getName() + " x order by x.id").getResultList();

            int pos = 0;
            for (Booking booking : bookings) if (booking.getTotalValue() != 0) {

                if (pos % 5 == 0) {
                    double v = booking.getTotalValue();
                    if (pos % 10 == 0) v = v * 0.3;
                    v = Helper.roundEuros(v);

                    Payment p = new Payment();
                    p.setAccount(banco);
                    p.setAgent(booking.getAgency().getFinancialAgent());
                    p.setDate(LocalDate.now());
                    p.setValueInNucs(v);


                    BookingPaymentAllocation a;
                    booking.getPayments().add(a = new BookingPaymentAllocation());
                    a.setPayment(p);
                    a.setBooking(booking);
                    p.setBreakdown(Lists.newArrayList(a));
                    a.setValue(v);

                    PaymentLine l;
                    p.setLines(Lists.newArrayList(l = new PaymentLine()));
                    l.setPayment(p);
                    l.setMethodOfPayment(visa);
                    l.setValue(v);
                    l.setCurrency(eur);

                    em.persist(p);
                }

                pos++;
            }

        });

    }

    private static void crearComisiones() throws Throwable {

        Helper.transact(em -> {

            List<ProductLine> lineasProducto = Helper.selectObjects("select x from " + ProductLine.class.getName() + " x");
            List<CommissionAgent> agentes = Helper.selectObjects("select x from " + CommissionAgent.class.getName() + " x");

            CommissionTerms cts = new CommissionTerms();

            cts.setName("Comisiones 2019");

            for (CommissionAgent agente : agentes) {

                for (ProductLine productLine : lineasProducto) {

                    CommissionTermsLine l;
                    cts.getLines().add(l = new CommissionTermsLine());
                    l.setTerms(cts);
                    l.setAgent(agente);
                    l.setBasis(CommissionAplicationBasis.MARKUP);
                    l.setProductLine(productLine);
                    l.setStart(LocalDate.of(2019, 01, 01));
                    l.setEnd(LocalDate.of(2019, 12, 31));
                    l.setPercent(50);
                }

            }

            em.persist(cts);


        });

    }

    private static void completarAppConfig() throws Throwable {

        Helper.transact(em -> {

            AppConfig c = AppConfig.get(em);
            c.setAdminEmailFrom("miguel@quotravel.eu");
            c.setAdminEmailSmtpHost("mail.quotravel.eu");
            c.setAdminEmailSmtpPort(25);
            c.setAdminEmailPassword("Ant0nia123");
            c.setAdminEmailUser("miguel@quotravel.eu");

        });

    }

    private static void crearCupones() throws Throwable {

        Helper.transact(em -> {

            Coupon c = new Coupon();
            c.setCode("CUPONTEST0001");
            c.setCurrency(em.find(Currency.class, "EUR"));
            c.setName("Cupón test 0001");
            c.setUnits(10);
            c.setAvailable(10);
            c.setFinalPrice(15.3);
            em.persist(c);

        });

    }

    private static void createPaymentStructure() throws Throwable {

        Helper.transact(em -> {

            BankAccount acc = new BankAccount();
            acc.setName("Banco Santander 6546546456465465464");
            acc.setCurrency(em.find(Currency.class, "EUR"));
            acc.setComments("Test");
            em.persist(acc);


            TPV tpv = new TPV();
            tpv.setAccount(acc);
            tpv.setActionUrl("");
            tpv.setKoUrl("https://sanbox1.quotravel.eu/tpv/ko");
            tpv.setMerchantCode("");
            tpv.setMerchantName("");
            tpv.setMerchantTerminal("");
            tpv.setMerchantSecret("");
            tpv.setName("Paypal");
            tpv.setNotificationUrl("https://sanbox1.quotravel.eu/tpv/notificacion");
            tpv.setOffice(em.find(Office.class, 1l));
            tpv.setOkUrl("https://sanbox1.quotravel.eu/tpv/ok");
            tpv.setPaypalEmail("miguelperezcolom-facilitator@gmail.com");
            tpv.setPrivateKey("AK-Srf2HoktGIeYhzN9FsIUHYatUAV5R4bvvRJ.jRmqwzL-TMTbY5CVQ");
            tpv.setType(TPVTYPE.PAYPAL);
            tpv.setXml(false);

            em.persist(tpv);

        });

        Helper.transact(em -> {

            BankAccount acc = new BankAccount();
            acc.setName("Bankia 454654849898776444");
            acc.setCurrency(em.find(Currency.class, "EUR"));
            acc.setComments("Test");
            em.persist(acc);


            TPV tpv = new TPV();
            tpv.setAccount(acc);
            tpv.setActionUrl("https://sis-t.redsys.es:25443/sis/realizarPago");
            tpv.setKoUrl("https://sanbox1.quotravel.eu/tpv/ko");
            tpv.setMerchantCode("079194643");
            tpv.setMerchantName("VIAJES IBIZA");
            tpv.setMerchantTerminal("003");
            tpv.setMerchantSecret("sq7HjrUOBfKmC576ILgskD5srU870gJ7");
            tpv.setName("Bankia Redsys");
            tpv.setNotificationUrl("https://sanbox1.quotravel.eu/tpv/notificacion");
            tpv.setOffice(em.find(Office.class, 1l));
            tpv.setOkUrl("https://sanbox1.quotravel.eu/tpv/ok");
            tpv.setType(TPVTYPE.SERMEPA);
            tpv.setXml(false);

            em.persist(tpv);

            ((List<PointOfSale>)em.createQuery("select x from " + PointOfSale.class.getName() + " x").getResultList()).forEach(p -> p.setTpv(tpv));

        });

    }

    private static void crearFacturas() throws Throwable {

        Helper.transact(em -> {

            List<BookingCharge> cargos = Helper.selectObjects("select x from " + BookingCharge.class.getName() + " x");

            List<IssuedInvoice> invoices = Invoicer.invoice(em, MDD.getCurrentUser(), cargos.size() > 100?cargos.subList(0, cargos.size() - cargos.size() % 100):cargos);

            invoices.forEach(i -> {
                //i.setNumber(i.getAgency().getCompany().getBillingSerial().createInvoiceNumber());
                //i.setSerial(i.getAgency().getCompany().getBillingSerial());
                for (AbstractInvoiceLine l : i.getLines()) {
                    if (l instanceof BookingInvoiceLine) {
                        BookingInvoiceLine bil = (BookingInvoiceLine) l;
                        bil.getCharge().setInvoice(i);
                        em.merge(bil.getCharge());
                    } else if (l instanceof ChargeInvoiceLine) {
                        ChargeInvoiceLine bil = (ChargeInvoiceLine) l;
                        bil.getCharge().setInvoice(i);
                        em.merge(bil.getCharge());
                    }
                }
                em.persist(i);
            });


        });

        Helper.transact(em -> {

            List<Provider> proveedores = Helper.selectObjects("select x from " + Provider.class.getName() + " x");


            proveedores.forEach(p -> {

                List<PurchaseCharge> cargos = null;
                try {
                    cargos = Helper.selectObjects("select x from " + PurchaseCharge.class.getName() + " x where x.provider.id = " + p.getId());

                    if (cargos.size() > 0) createInvoice(em, cargos.size() > 100?cargos.subList(0, cargos.size() - cargos.size() % 100):cargos, "" + (System.currentTimeMillis() % 1000), LocalDate.now(), LocalDate.now(), LocalDate.now());

                } catch (Throwable throwable) {
                    Helper.printStackTrace(throwable);
                }


            });


        });

    }

    private static void createInvoice(EntityManager em, List<PurchaseCharge> charges, String invoiceNumber, LocalDate invoiceDate, LocalDate dueDate, LocalDate taxDate) throws Throwable {
        if (charges.size() == 0) throw new Exception("No charge selected");
        ReceivedInvoice result = new ReceivedInvoice(MDD.getCurrentUser(), charges, charges.get(0).getProvider().getFinancialAgent(), charges.get(0).getPurchaseOrder().getOffice().getCompany().getFinancialAgent(), invoiceNumber);
        em.persist(result);
    }

    private static void crearTokens() throws Throwable {

        Helper.transact(em -> {

            AgencyUser u = new AgencyUser();
            u.setAgency(em.find(Agency.class, 1l));
            u.setLogin("agency1");
            u.setName("Agency user 1");
            u.setEmail("miguelperezcolom@gmail.com");
            u.setAudit(new Audit(MDD.getCurrentUser()));
            u.setStatus(USER_STATUS.ACTIVE);
            u.setPassword("1");
            em.persist(u);

            {
                AuthToken t = new AuthToken();
                t.setId("eyAiY3JlYXRlZCI6ICJGcmkgTWFyIDIyIDEwOjIyOjI5IENFVCAyMDE5IiwgInVzZXJJZCI6ICJhZG1pbiIsICJhZ2VuY3lJZCI6ICIzIn0=");
                t.setActive(true);
                t.setUser(u);
                t.setPos(em.find(PointOfSale.class, 2l));
                em.persist(t);
            }


            {
                AuthToken t = new AuthToken();
                t.setId("eyAiY3JlYXRlZCI6ICJGcmkgTWFyIDIyIDEyOjI3OjI4IENFVCAyMDE5IiwgInVzZXJJZCI6ICJhZG1pbiIsICJhZ2VuY3lJZCI6ICIzIiwgImhvdGVsSWQiOiAiMTIifQ==");
                t.setActive(true);
                t.setUser(u);
                t.setHotel(em.find(Hotel.class, 12l));
                t.setPos(em.find(PointOfSale.class, 2l));
                em.persist(t);
            }

        });

    }

    private static void crearProductos() throws Throwable {

        Helper.transact(em -> {

            VAT iva = em.find(VAT.class, 1l);

            List<BillingConcept> conceptosTraslados = new ArrayList<>();
            for (String n : Lists.newArrayList("TRA-Traslado")) {
                BillingConcept concepto;
                conceptosTraslados.add(concepto = new BillingConcept());
                concepto.setName(n.split("-")[1]);
                concepto.setCode(n.split("-")[0]);
                concepto.setLocalizationRule(LocalizationRule.SERVICE);
                concepto.setHotelIncluded(true);
                concepto.setSpecialRegime(true);
                em.persist(concepto);
                em.flush();
                AppConfig.get(em).setBillingConceptForTransfer(concepto);
                VATPercent p;
                iva.getPercents().add(p = new VATPercent());
                p.setVat(iva);
                p.setBillingConcept(concepto);
                p.setPercent(10);
            }

            List<BillingConcept> conceptosHotel = new ArrayList<>();
            for (String n : Lists.newArrayList("EST-Estancia")) {
                BillingConcept concepto;
                conceptosHotel.add(concepto = new BillingConcept());
                concepto.setName(n.split("-")[1]);
                concepto.setCode(n.split("-")[0]);
                concepto.setLocalizationRule(LocalizationRule.SERVICE);
                concepto.setTransportIncluded(true);
                concepto.setSpecialRegime(true);
                em.persist(concepto);
                em.flush();
                AppConfig.get(em).setBillingConceptForHotel(concepto);
                VATPercent p;
                iva.getPercents().add(p = new VATPercent());
                p.setVat(iva);
                p.setBillingConcept(concepto);
                p.setPercent(10);
            }

            List<BillingConcept> conceptosFreeText = new ArrayList<>();
            for (String n : Lists.newArrayList("OTROS-Otros", "CAR-Alquiler coche", "IB-Billete IBERIA", "AE-Billete AIREUROPA", "TRANS-Billete TRANSMEDITERRANEA")) {
                BillingConcept concepto;
                conceptosFreeText.add(concepto = new BillingConcept());
                concepto.setName(n.split("-")[1]);
                concepto.setCode(n.split("-")[0]);
                concepto.setLocalizationRule(LocalizationRule.SERVICE);
                em.persist(concepto);
                em.flush();
                if ("otros".equalsIgnoreCase(concepto.getCode())) {
                    AppConfig.get(em).setBillingConceptForOthers(concepto);
                    AppConfig.get(em).setBillingConceptForExcursion(concepto);
                    AppConfig.get(em).setBillingConceptForCircuit(concepto);
                    concepto.setSpecialRegime(true);
                }
                VATPercent p;
                iva.getPercents().add(p = new VATPercent());
                p.setVat(iva);
                p.setBillingConcept(concepto);
                p.setPercent(21);
            }
        });

        Helper.transact(em -> {

            List<Tag> etiquetas = new ArrayList<>();
            for (String n : new String[] {"Familia", "Aventuras", "Caribe", "Ocio", "Emociones fuertes"}) {
                Tag l = new Tag();
                etiquetas.add(l);
                l.setName(n);
                l.setDescription(new Literal(n, n));
                em.persist(l);
            }

            List<Office> oficinas = Helper.selectObjects("select x from " + Office.class.getName() + " x");

            List<Provider> proveedores = Helper.selectObjects("select x from " + Provider.class.getName() + " x");


            List<ProductLine> lineasProducto = Helper.selectObjects("select x from " + ProductLine.class.getName() + " x");

            List<ProductType> tiposProducto = new ArrayList<>();
            for (String n : Lists.newArrayList("Alquiler coche", "Otros")) {
                ProductType tipo;
                tiposProducto.add(tipo = new ProductType());
                tipo.setName(n);
                em.persist(tipo);
                em.flush();
            }

            List<GenericProduct> productos = new ArrayList<>();
            int pos = 0;
            for (String n : new String[] { "Alquiler Ford Fiesta Formentera", "Alquiler Scooter Formentera", "Alquiler Scooter Ibiza"}) {
                GenericProduct p = new GenericProduct();
                productos.add(p);
                p.setActive(true);
                p.setName(n);
                p.setOffice(oficinas.get(nextInt() % oficinas.size()));
                p.setType(tiposProducto.get(nextInt() % tiposProducto.size()));
                p.setProductLine(lineasProducto.get(nextInt() % lineasProducto.size()));
                p.setResort(p.getOffice().getResort());
                p.setUnitsDependant(true);
                Variant v;
                p.getVariants().add(v = new Variant());
                v.setProduct(p);
                v.setDescription(new Literal("xx", "qq"));
                v.setName(new Literal("General", "General"));

                DataSheet ds;
                p.setDataSheet(ds = new DataSheet());
                ds.setName(p.getName());
                ds.setDescription(new Literal(LoremIpsum.getInstance().getWords(20, 30), LoremIpsum.getInstance().getWords(20, 30)));
                ds.getTags().add(etiquetas.get(nextInt() % etiquetas.size()));
                ds.setMainImage(new Resource(new URL("https://x.cdrst.com/foto/hotel-sf/fd1/medianaresp/hotel-los-delfines-servicios-5502a1d.jpg")));

                em.persist(p);
            }

            {
                io.mateu.erp.model.product.generic.Contract cv = null;
                {
                    io.mateu.erp.model.product.generic.Contract c = cv = new io.mateu.erp.model.product.generic.Contract();
                    c.setVATIncluded(true);
                    c.setValidFrom(LocalDate.of(2019, 01, 01));
                    c.setValidTo(LocalDate.of(2019, 12, 31));
                    c.setType(ContractType.SALE);
                    c.setTitle("Venta genéricos 2019");
                    c.setRatesType(RatesType.NET);
                    c.setProductLine(getProductLine(lineasProducto, "Otros productos"));
                    c.setOffice(oficinas.get(0));
                    c.setCurrency(em.find(Currency.class, "EUR"));
                    c.setBillingConcept(em.find(BillingConcept.class, "OTROS"));
                    c.setAudit(new Audit(MDD.getCurrentUser()));
                    c.setActive(true);
                    int i = 0;
                    for (GenericProduct producto : productos) {
                        Price p;
                        c.getPrices().add(p = new Price());
                        p.setContract(c);
                        p.setTariff(em.find(Tariff.class, 1l));
                        p.setDescription(producto.getName());
                        p.setProduct(producto);
                        p.setActive(true);
                        p.setOrder(i++);
                        p.setUnitPrice(Helper.roundEuros(Math.abs(new Random().nextDouble()) * 1000d));
                    }
                    em.persist(c);
                }

                {
                    io.mateu.erp.model.product.generic.Contract c = new io.mateu.erp.model.product.generic.Contract();
                    c.setVATIncluded(true);
                    c.setValidFrom(LocalDate.of(2019, 01, 01));
                    c.setValidTo(LocalDate.of(2019, 12, 31));
                    c.setType(ContractType.PURCHASE);
                    c.setTitle("Compra genéricos 2019");
                    c.setRatesType(RatesType.NET);
                    c.setProductLine(getProductLine(lineasProducto, "Otros productos"));
                    c.setOffice(oficinas.get(0));
                    c.setCurrency(em.find(Currency.class, "EUR"));
                    c.setBillingConcept(em.find(BillingConcept.class, "OTROS"));
                    c.setAudit(new Audit(MDD.getCurrentUser()));
                    c.setActive(true);
                    c.setSupplier(proveedores.get(nextInt() % proveedores.size()));
                    int i = 0;
                    for (Price pp : cv.getPrices()) {
                        Price p;
                        c.getPrices().add(p = new Price());
                        p.setContract(c);
                        p.setTariff(em.find(Tariff.class, 1l));
                        p.setDescription(pp.getDescription());
                        p.setProduct(pp.getProduct());
                        p.setActive(true);
                        p.setOrder(i++);
                        p.setUnitPricePerDay(Helper.roundEuros(pp.getUnitPrice() * 0.75));
                    }
                    em.persist(c);
                }

            }



            List<Excursion> excursiones = new ArrayList<>();
            for (String n : new String[] {
                    "Super Saona", "Saona Exclusive", "Viva Safari", "Saona Combo",
                    "Catalina Snorkeling", "Blue Caribe Catamaran", "Bayahibe Snorkeling",
                    "Deep Sea Fishing", "Crazy Wheels – Quad & Buggy", "Santo Domingo City Tour",
                    "Dominibus Chavón", "Coco Bongo Show Disco", "Horseback Riding at “Cueva de Chico”",
                    "Horseback Riding to Padre Nuestro", "La Hacienda 7-in-1", "Bayahibe Runners Safari",
                    "Ziplines Adventure", "Samaná El Limón", "Los Haitises National Park",
                    "Whales watching – Samaná Bay"}) {
                Excursion p = new Excursion();
                excursiones.add(p);
                p.setActive(true);
                p.setName(n);
                p.setOffice(oficinas.get(nextInt() % oficinas.size()));
                p.setType(tiposProducto.get(nextInt() % tiposProducto.size()));
                p.setProductLine(lineasProducto.get(nextInt() % lineasProducto.size()));
                p.setResort(p.getOffice().getResort());

                p.setDuration(TourDuration.HALFDAY);
                ExcursionShift s;
                p.getShifts().add(s = new ExcursionShift());
                s.setName("8h30-18h30");
                s.setExcursion(p);
                s.setStart(LocalDate.of(2019, 01, 01));
                s.setEnd(LocalDate.of(2019, 12, 31));
                s.getLanguages().add(em.find(ExcursionLanguage.class, "es"));
                s.setMaxPax(100);

                Variant v;
                p.getVariants().add(v = new Variant());
                v.setName(new Literal("VIP", "VIP"));
                v.setDescription(new Literal("Only available as VIP", "Solo variante VIP disponible"));
                v.setProduct(p);


                DataSheet ds;
                p.setDataSheet(ds = new DataSheet());
                ds.setName(p.getName());
                ds.setDescription(new Literal(LoremIpsum.getInstance().getWords(20, 30), LoremIpsum.getInstance().getWords(20, 30)));
                ds.getTags().add(etiquetas.get(nextInt() % etiquetas.size()));
                ds.setMainImage(new Resource(new URL("http://www.exploreviva.com/wp-content/uploads/2017/07/exc-saonasup.jpg"
                        //"https://x.cdrst.com/foto/hotel-sf/fd1/medianaresp/hotel-los-delfines-servicios-5502a1d.jpg"
                )));


                em.persist(p);

                p.generateEvents(em);
            }


            {
                io.mateu.erp.model.product.tour.Contract cv = null;
                {
                    io.mateu.erp.model.product.tour.Contract c = cv = new io.mateu.erp.model.product.tour.Contract();
                    c.setVATIncluded(true);
                    c.setValidFrom(LocalDate.of(2019, 01, 01));
                    c.setValidTo(LocalDate.of(2019, 12, 31));
                    c.setType(ContractType.SALE);
                    c.setTitle("Venta excursiones 2019");
                    c.setRatesType(RatesType.NET);
                    c.setProductLine(getProductLine(lineasProducto, "Otros productos"));
                    c.setOffice(oficinas.get(0));
                    c.setCurrency(em.find(Currency.class, "EUR"));
                    c.setBillingConcept(em.find(BillingConcept.class, "OTROS"));
                    c.setAudit(new Audit(MDD.getCurrentUser()));
                    c.setActive(true);
                    int i = 0;
                    for (Excursion producto : excursiones) for (Variant v : producto.getVariants()) {
                        TourPrice p;
                        c.getPrices().add(p = new TourPrice());
                        p.setContract(c);
                        p.setTariff(em.find(Tariff.class, 1l));
                        p.setDescription(producto.getName());
                        p.setTour(producto);
                        p.setVariant(v);
                        p.setActive(true);
                        p.setOrder(i++);
                        p.setAdultPrice(Helper.roundEuros(Math.abs(new Random().nextDouble()) * 1000d));
                    }
                    em.persist(c);
                }

                {
                    io.mateu.erp.model.product.tour.Contract c = new io.mateu.erp.model.product.tour.Contract();
                    c.setVATIncluded(true);
                    c.setValidFrom(LocalDate.of(2019, 01, 01));
                    c.setValidTo(LocalDate.of(2019, 12, 31));
                    c.setType(ContractType.PURCHASE);
                    c.setTitle("Compra excursiones 2019");
                    c.setRatesType(RatesType.NET);
                    c.setProductLine(getProductLine(lineasProducto, "Otros productos"));
                    c.setOffice(oficinas.get(0));
                    c.setCurrency(em.find(Currency.class, "EUR"));
                    c.setBillingConcept(em.find(BillingConcept.class, "OTROS"));
                    c.setAudit(new Audit(MDD.getCurrentUser()));
                    c.setActive(true);
                    c.setSupplier(proveedores.get(nextInt() % proveedores.size()));
                    int i = 0;
                    for (TourPrice pp : cv.getPrices()) {
                        TourPrice p;
                        c.getPrices().add(p = new TourPrice());
                        p.setContract(c);
                        p.setTariff(em.find(Tariff.class, 1l));
                        p.setDescription(pp.getDescription());
                        p.setTour(pp.getTour());
                        p.setVariant(pp.getVariant());
                        p.setActive(true);
                        p.setOrder(i++);
                        p.setAdultPrice(Helper.roundEuros(pp.getAdultPrice() * 0.75));
                    }
                    em.persist(c);
                }

            }


            List<Circuit> circuitos = new ArrayList<>();
            for (String n : new String[] { "Crucero Mediterráneo", "Escocia en 5 días", "Andalucía en 10 días"}) {
                Circuit p = new Circuit();
                circuitos.add(p);
                p.setActive(true);
                p.setName(n);
                p.setOffice(oficinas.get(nextInt() % oficinas.size()));
                p.setType(tiposProducto.get(nextInt() % tiposProducto.size()));
                p.setProductLine(lineasProducto.get(nextInt() % lineasProducto.size()));
                p.setResort(p.getOffice().getResort());

                CircuitCalendar s;
                p.getSchedule().add(s = new CircuitCalendar());
                s.setCircuit(p);
                s.setStart(LocalDate.parse("2019-01-01"));
                s.setEnd(LocalDate.parse("2019-12-31"));
                s.setWeekdays(new boolean[] {true, false, false, false, false, false, false});

                Variant v;
                p.getVariants().add(v = new Variant());
                v.setName(new Literal("VIP", "VIP"));
                v.setDescription(new Literal("Only available as VIP", "Solo variante VIP disponible"));
                v.setProduct(p);

                DataSheet ds;
                p.setDataSheet(ds = new DataSheet());
                ds.setName(p.getName());
                ds.setDescription(new Literal(LoremIpsum.getInstance().getWords(20, 30), LoremIpsum.getInstance().getWords(20, 30)));
                ds.getTags().add(etiquetas.get(nextInt() % etiquetas.size()));
                ds.setMainImage(new Resource(new URL("https://x.cdrst.com/foto/hotel-sf/fd1/medianaresp/hotel-los-delfines-servicios-5502a1d.jpg")));


                em.persist(p);

                p.generateEvents(em);
            }

            {
                io.mateu.erp.model.product.tour.Contract cv = null;
                {
                    io.mateu.erp.model.product.tour.Contract c = cv = new io.mateu.erp.model.product.tour.Contract();
                    c.setVATIncluded(true);
                    c.setValidFrom(LocalDate.of(2019, 01, 01));
                    c.setValidTo(LocalDate.of(2019, 12, 31));
                    c.setType(ContractType.SALE);
                    c.setTitle("Venta circuitos 2019");
                    c.setRatesType(RatesType.NET);
                    c.setProductLine(getProductLine(lineasProducto, "Otros productos"));
                    c.setOffice(oficinas.get(0));
                    c.setCurrency(em.find(Currency.class, "EUR"));
                    c.setBillingConcept(em.find(BillingConcept.class, "OTROS"));
                    c.setAudit(new Audit(MDD.getCurrentUser()));
                    c.setActive(true);
                    int i = 0;
                    for (Circuit producto : circuitos) for (Variant v : producto.getVariants()) {
                        TourPrice p;
                        c.getPrices().add(p = new TourPrice());
                        p.setContract(c);
                        p.setTariff(em.find(Tariff.class, 1l));
                        p.setDescription(producto.getName());
                        p.setTour(producto);
                        p.setVariant(v);
                        p.setActive(true);
                        p.setOrder(i++);
                        p.setAdultPrice(Helper.roundEuros(Math.abs(new Random().nextDouble()) * 1000d));
                    }
                    em.persist(c);
                }

                {
                    io.mateu.erp.model.product.tour.Contract c = new io.mateu.erp.model.product.tour.Contract();
                    c.setVATIncluded(true);
                    c.setValidFrom(LocalDate.of(2019, 01, 01));
                    c.setValidTo(LocalDate.of(2019, 12, 31));
                    c.setType(ContractType.PURCHASE);
                    c.setTitle("Compra circuitos 2019");
                    c.setRatesType(RatesType.NET);
                    c.setProductLine(getProductLine(lineasProducto, "Otros productos"));
                    c.setOffice(oficinas.get(0));
                    c.setCurrency(em.find(Currency.class, "EUR"));
                    c.setBillingConcept(em.find(BillingConcept.class, "OTROS"));
                    c.setAudit(new Audit(MDD.getCurrentUser()));
                    c.setActive(true);
                    c.setSupplier(proveedores.get(nextInt() % proveedores.size()));
                    int i = 0;
                    for (TourPrice pp : cv.getPrices()) {
                        TourPrice p;
                        c.getPrices().add(p = new TourPrice());
                        p.setContract(c);
                        p.setTariff(em.find(Tariff.class, 1l));
                        p.setDescription(pp.getDescription());
                        p.setTour(pp.getTour());
                        p.setVariant(pp.getVariant());
                        p.setActive(true);
                        p.setOrder(i++);
                        p.setAdultPrice(Helper.roundEuros(pp.getAdultPrice() * 0.75));
                    }
                    em.persist(c);
                }

            }


            // traslados

            {

                List<TransferPoint> puntos = Helper.selectObjects("select x from " + TransferPoint.class.getName() + " x");
                List<Airport> aeropuertos = Helper.selectObjects("select x from " + Airport.class.getName() + " x");

                Zone a = new Zone();
                a.setName("Aeropuertos");
                a.getPoints().addAll(puntos.stream().filter(p -> TransferPointType.AIRPORT.equals(p.getType())).collect(Collectors.toList()));
                em.persist(a);

                Zone b = new Zone();
                b.setName("Resto puntos");
                b.getPoints().addAll(puntos.stream().filter(p -> !TransferPointType.AIRPORT.equals(p.getType())).collect(Collectors.toList()));
                em.persist(b);


                Vehicle v = new Vehicle();
                v.setName("BUS");
                v.setMinPax(0);
                v.setMaxPax(100);
                em.persist(v);


                io.mateu.erp.model.product.transfer.Contract cv = null;
                {
                    io.mateu.erp.model.product.transfer.Contract c = cv = new io.mateu.erp.model.product.transfer.Contract();
                    c.setVATIncluded(true);
                    c.setValidFrom(LocalDate.of(2019, 01, 01));
                    c.setValidTo(LocalDate.of(2019, 12, 31));
                    c.setType(ContractType.SALE);
                    c.setTitle("Venta traslados 2019");
                    c.setRatesType(RatesType.NET);
                    c.setProductLine(getProductLine(lineasProducto, "Otros productos"));
                    c.setOffice(oficinas.get(0));
                    c.setCurrency(em.find(Currency.class, "EUR"));
                    c.setBillingConcept(em.find(BillingConcept.class, "TRA"));
                    c.setAudit(new Audit(MDD.getCurrentUser()));
                    c.setActive(true);
                    int i = 0;
                    for (TransferType t : Lists.newArrayList(TransferType.PRIVATE, TransferType.SHUTTLE, TransferType.EXECUTIVE)) {
                        io.mateu.erp.model.product.transfer.Price p;
                        c.getPrices().add(p = new io.mateu.erp.model.product.transfer.Price());
                        p.setContract(c);
                        p.setTariff(em.find(Tariff.class, 1l));
                        p.setOrigin(a);
                        p.setDestination(b);
                        p.setPricePer(PricePer.PAX);
                        p.setTransferType(t);
                        p.setVehicle(v);
                        p.setFromPax(0);
                        p.setToPax(1000);
                        p.setPrice(Helper.roundEuros(Math.abs(new Random().nextDouble()) * 100d));
                    }
                    em.persist(c);
                }

                {
                    io.mateu.erp.model.product.transfer.Contract c = new io.mateu.erp.model.product.transfer.Contract();
                    c.setVATIncluded(true);
                    c.setValidFrom(LocalDate.of(2019, 01, 01));
                    c.setValidTo(LocalDate.of(2019, 12, 31));
                    c.setType(ContractType.PURCHASE);
                    c.setTitle("Compra traslados 2019");
                    c.setRatesType(RatesType.NET);
                    c.setProductLine(getProductLine(lineasProducto, "Otros productos"));
                    c.setOffice(oficinas.get(0));
                    c.setCurrency(em.find(Currency.class, "EUR"));
                    c.setBillingConcept(em.find(BillingConcept.class, "TRA"));
                    c.setAudit(new Audit(MDD.getCurrentUser()));
                    c.setActive(true);
                    c.setSupplier(proveedores.get(nextInt() % proveedores.size()));
                    int i = 0;
                    for (io.mateu.erp.model.product.transfer.Price pp : cv.getPrices()) {
                        io.mateu.erp.model.product.transfer.Price p;
                        c.getPrices().add(p = new io.mateu.erp.model.product.transfer.Price());
                        p.setContract(c);
                        p.setTariff(em.find(Tariff.class, 1l));
                        p.setOrigin(pp.getOrigin());
                        p.setDestination(pp.getDestination());
                        p.setPricePer(pp.getPricePer());
                        p.setTransferType(pp.getTransferType());
                        p.setVehicle(pp.getVehicle());
                        p.setFromPax(pp.getFromPax());
                        p.setToPax(pp.getToPax());
                        p.setPrice(Helper.roundEuros(pp.getPrice() * 0.75));
                    }
                    em.persist(c);
                }

            }


            // hoteles

            List<Hotel> hoteles = Helper.selectObjects("select x from " + Hotel.class.getName() + " x");

            for (Hotel hotel : hoteles) {

                Inventory inv = new Inventory();
                inv.setHotel(hotel);
                inv.setName("Cupo general " + hotel.getName());
                hotel.getInventories().add(inv);
                em.persist(inv);

                HotelContract cv = null;
                {
                    HotelContract c = cv = new HotelContract();
                    c.setVATIncluded(true);
                    c.setValidFrom(LocalDate.of(2019, 01, 01));
                    c.setValidTo(LocalDate.of(2019, 12, 31));
                    c.setType(ContractType.SALE);
                    c.setTitle("Venta " + hotel.getName() + " 2019");
                    c.setRatesType(RatesType.NET);
                    c.setProductLine(getProductLine(lineasProducto, "Hoteles Mallorca"));
                    c.setOffice(oficinas.get(0));
                    c.setCurrency(em.find(Currency.class, "EUR"));
                    c.setBillingConcept(em.find(BillingConcept.class, "EST"));
                    c.setAudit(new Audit(MDD.getCurrentUser()));
                    c.setActive(true);
                    c.setHotel(hotel);
                    c.setTariff(em.find(Tariff.class, 1l));
                    hotel.getContracts().add(c);
                    c.setInventory(inv);
                    int i = 0;
                    c.setTerms(new HotelContractPhoto());
                    c.getTerms().setContract(c);
                    LinearFare lf;
                    c.getTerms().getFares().add(lf = new LinearFare(c.getTerms()));
                    lf.setName("T1");
                    lf.getDates().add(new DatesRange(LocalDate.of(2019, 01, 01), LocalDate.of(2019, 12, 31)));
                    LinearFareLine lfl;
                    lf.getLines().add(lfl = new LinearFareLine());
                    lfl.setRoomTypeCode(hotel.getRooms().get(0).getType());
                    lfl.setBoardTypeCode(hotel.getBoards().get(0).getType());
                    lfl.setAdultPrice(Helper.roundEuros(Math.abs(new Random().nextDouble()) * 1000d));
                    lfl.setFare(lf);
                    em.persist(c);
                }

                {
                    HotelContract c = new HotelContract();
                    c.setVATIncluded(true);
                    c.setValidFrom(LocalDate.of(2019, 01, 01));
                    c.setValidTo(LocalDate.of(2019, 12, 31));
                    c.setType(ContractType.PURCHASE);
                    c.setTitle("Compra " + hotel.getName() + " 2019");
                    c.setRatesType(RatesType.NET);
                    c.setProductLine(getProductLine(lineasProducto, "Hoteles Mallorca"));
                    c.setOffice(oficinas.get(0));
                    c.setCurrency(em.find(Currency.class, "EUR"));
                    c.setBillingConcept(em.find(BillingConcept.class, "EST"));
                    c.setAudit(new Audit(MDD.getCurrentUser()));
                    c.setActive(true);
                    c.setSupplier(proveedores.get(nextInt() % proveedores.size()));
                    c.setHotel(hotel);
                    hotel.getContracts().add(c);
                    c.setTariff(em.find(Tariff.class, 1l));
                    c.setInventory(inv);
                    int i = 0;
                    c.setTerms(new HotelContractPhoto());
                    c.getTerms().setContract(c);
                    c.getTerms().getAllotment().add(new Allotment(c.getTerms(), "DBL", LocalDate.of(2019, 01, 01), LocalDate.of(2019, 12, 31), 1000));
                    LinearFare lf;
                    c.getTerms().getFares().add(lf = new LinearFare(c.getTerms()));
                    lf.setName("T1");
                    lf.getDates().add(new DatesRange(LocalDate.of(2019, 01, 01), LocalDate.of(2019, 12, 31)));
                    LinearFareLine lfl;
                    lf.getLines().add(lfl = new LinearFareLine());
                    lfl.setRoomTypeCode(hotel.getRooms().get(0).getType());
                    lfl.setBoardTypeCode(hotel.getBoards().get(0).getType());
                    lfl.setAdultPrice(Helper.roundEuros(cv.getTerms().getFares().get(0).getLines().get(0).getAdultPrice() * 0.75));
                    lfl.setFare(lf);
                    em.persist(c);
                }

            }



        });

    }

    private static ProductLine getProductLine(List<ProductLine> lineasProducto, String s) {
        ProductLine l = null;
        for (ProductLine productLine : lineasProducto) {
            if (productLine.getName().equalsIgnoreCase(s)) {
                l = productLine;
                break;
            }
        }
        return l;
    }

    private static void crearReservas() throws Throwable {
        Helper.transact(em -> {


            List<CommissionAgent> reps = Helper.selectObjects("select x from " + CommissionAgent.class.getName() + " x order by x.name");

            List<PointOfSale> poses = Helper.selectObjects("select x from " + PointOfSale.class.getName() + " x order by x.name");

            List<BillingConcept> conceptosTraslados = Lists.newArrayList(em.find(BillingConcept.class, "TRA"));

            List<BillingConcept> conceptosHotel = Lists.newArrayList(em.find(BillingConcept.class, "EST"));

            List<BillingConcept> conceptosFreeText = Lists.newArrayList(em.find(BillingConcept.class, "CAR"), em.find(BillingConcept.class, "IB"), em.find(BillingConcept.class, "AE"), em.find(BillingConcept.class, "TRANS"));

            List<Agency> agencias = Helper.selectObjects("select x from " + Agency.class.getName() + " x");

            List<Provider> proveedores = Helper.selectObjects("select x from " + Provider.class.getName() + " x");

            List<Hotel> hoteles = Helper.selectObjects("select x from " + Hotel.class.getName() + " x");

            List<TransferPoint> puntos = Helper.selectObjects("select x from " + TransferPoint.class.getName() + " x");

            List<Office> oficinas = Helper.selectObjects("select x from " + Office.class.getName() + " x");

            List<ProductLine> lineasProducto = Helper.selectObjects("select x from " + ProductLine.class.getName() + " x");

            List<GenericProduct> genericos = Helper.selectObjects("select x from " + GenericProduct.class.getName() + " x");

            List<Excursion> excursiones = Helper.selectObjects("select x from " + Excursion.class.getName() + " x");

            List<Circuit> circuitos = Helper.selectObjects("select x from " + Circuit.class.getName() + " x");

            List<Market> markets = Helper.selectObjects("select x from " + Market.class.getName() + " x");

            List<Ticket> tickets = crearTickets(em, excursiones);


            User u = (User) Helper.selectObjects("select x from " + User.class.getName() + " x").get(0);

            Currency eur = em.find(Currency.class, "EUR");

            LocalDate d = LocalDate.now().minusDays(10);

            File f = null;
            for (int i = 0; i < 200; i++) {

                LocalDate d0 = d.plusDays(nextInt() % 30);
                LocalDate d1 = d0.plusDays(nextInt() % 10);

                Agency agencia = agencias.get(nextInt() % agencias.size());
                CommissionAgent rep = null;

                if (nextInt() % 10 == 0) {
                    f = new File();
                    //f.setActive(nextInt() % 10 > 2);
                    f.setAudit(new Audit(u));
                    f.setBalance((nextInt() % 10000) / 10d);
                    f.setComments("Test booking");
                    f.setCurrency(eur);
                    f.setFinish(d1);
                    f.setLeadName(LoremIpsum.getInstance().getName());
                    f.setAgency(agencia);
                    f.setStart(d0);
                    f.setTotalCost((nextInt() % 10000) / 10d);
                    f.setTotalValue((nextInt() % 10000) / 10d);
                    f.setTotalNetValue((nextInt() % 10000) / 10d);
                    em.persist(f);
                } else if (nextInt() % 3 == 0) {
                    f = null;
                }
                if (nextInt() % 2 == 0) {
                     rep = reps.get(nextInt() % reps.size());
                }
                Market market = markets.get(nextInt() % markets.size());


                switch (nextInt() % 6) {
                    case 0: crearReservaTraslado(em, u, f, puntos, poses, conceptosTraslados, agencia, rep, market); break;
                    case 1: crearReservaHotel(em, u, f, hoteles, poses, conceptosHotel, agencia, rep, market); break;
                    case 2: crearReservaFreeText(em, u, f, oficinas, poses, conceptosFreeText, agencia, proveedores, lineasProducto, rep, market); break;
                    case 3: crearReservaGenerico(em, u, f, oficinas, poses, conceptosFreeText, agencia, genericos, rep, market); break;
                    case 4: crearReservaExcursion(em, u, f, oficinas, poses, conceptosFreeText, agencia, excursiones, rep, market); break;
                    case 5: crearReservaCircuito(em, u, f, oficinas, poses, conceptosFreeText, agencia, circuitos, rep, market); break;
                }

                em.flush();
            }



        });

        Helper.transact(em -> {
            ((List<Booking>)em.createQuery("select x from " + Booking.class.getName() + " x order by x.id").getResultList()).forEach(b -> b.setActive(nextInt() % 10 > 2));
        });

    }

    private static List<Ticket> crearTickets(EntityManager em, List<Excursion> excursiones) throws Throwable {
        List<Ticket> l = new ArrayList<>();
        TicketBook b = new TicketBook();
        b.setTicketsGenerated(true);
        b.setReference("TB000001");
        b.setSerie("TS18");
        b.setFromNumber(1);
        b.setToNumber(100);
        em.persist(b);

        DecimalFormat nf = new DecimalFormat("000000");

        for (int i = 1; i <= 100; i++) {
            Ticket t = new Ticket();
            l.add(t);
            t.setBook(b);
            t.setStatus(TicketStatus.LIVE);
            t.setId(b.getSerie() + nf.format(i));
            em.persist(t);
        }

        em.flush();

        return l;
    }

    private static void crearReservaExcursion(EntityManager em, User u, File f, List<Office> oficinas, List<PointOfSale> poses, List<BillingConcept> conceptos, Agency agencia, List<Excursion> excursiones, CommissionAgent rep, Market market) throws Throwable {
        ExcursionBooking b = new ExcursionBooking();
        if (f != null) {
            f.getBookings().add(b);
            b.setFile(f);
        }
        b.setLeadName((f != null)?f.getLeadName():LoremIpsum.getInstance().getName());

        b.setExcursion(excursiones.get(nextInt() % excursiones.size()));
        b.setVariant(b.getExcursion().getVariants().get(nextInt() % b.getExcursion().getVariants().size()));
        b.setShift(b.getExcursion().getShifts().get(nextInt() % b.getExcursion().getShifts().size()));
        b.setLanguage(em.find(ExcursionLanguage.class, "es"));

        b.setCommissionAgent(rep);
        b.setAgency(agencia);
        b.setTariff(em.find(Tariff.class, 1l));
        b.setAgencyReference("" + (nextInt() % 1000000000));
        b.setEmail("miguelperezcolom@gmail.com");
        b.setTelephone(LoremIpsum.getInstance().getPhone());
        b.setMarket(market);

        b.setPax(3);


        LocalDate d0 = LocalDate.now().minusDays(10).plusDays(nextInt() % 30);
        LocalDate d1 = d0.plusDays(nextInt() % 10);
        //b.setActive(nextInt() % 10 > 2);
        b.setConfirmed(nextInt() % 10 > 2);
        b.setAudit(new Audit(u));
        b.setBalance((nextInt() % 10000) / 10d);
        b.setSpecialRequests("Test booking");
        b.setEnd(d1);
        b.setStart(d0);
        b.setTotalCost((nextInt() % 10000) / 10d);
        b.setTotalValue((nextInt() % 10000) / 10d);
        b.setTotalNetValue((nextInt() % 10000) / 10d);
        b.setOverridedBillingConcept(conceptos.get(nextInt() % conceptos.size()));
        b.setPos(poses.get(nextInt() % poses.size()));
        b.setPrivateComments(LoremIpsum.getInstance().getWords(5, 10));
        b.setValued(true);
        b.setValueOverrided(true);
        b.setCostOverrided(true);
        b.setOverridedValue(b.getTotalValue());
        b.setOverridedCost(b.getTotalCost());
        b.setOverridedCostCurrency(b.getCurrency());

        em.persist(b);
    }


    private static void crearReservaCircuito(EntityManager em, User u, File f, List<Office> oficinas, List<PointOfSale> poses, List<BillingConcept> conceptos, Agency agencia, List<Circuit> circuitos, CommissionAgent rep, Market market) throws Throwable {
        CircuitBooking b = new CircuitBooking();
        if (f != null) {
            f.getBookings().add(b);
            b.setFile(f);
        }
        b.setLeadName((f != null)?f.getLeadName():LoremIpsum.getInstance().getName());

        b.setCircuit(circuitos.get(nextInt() % circuitos.size()));
        b.setVariant(b.getCircuit().getVariants().get(nextInt() % b.getCircuit().getVariants().size()));

        b.setCommissionAgent(rep);
        b.setAgency(agencia);
        b.setTariff(em.find(Tariff.class, 1l));
        b.setAgencyReference("" + (nextInt() % 1000000000));
        b.setEmail("miguelperezcolom@gmail.com");
        b.setTelephone(LoremIpsum.getInstance().getPhone());
        b.setMarket(market);



        LocalDate d0 = LocalDate.now().minusDays(10).plusDays(nextInt() % 30);
        LocalDate d1 = d0.plusDays(nextInt() % 10);
        //b.setActive(nextInt() % 10 > 2);
        b.setConfirmed(nextInt() % 10 > 2);
        b.setAudit(new Audit(u));
        b.setBalance((nextInt() % 10000) / 10d);
        b.setSpecialRequests("Test booking");
        b.setEnd(d1);
        b.setStart(d0);
        b.setTotalCost((nextInt() % 10000) / 10d);
        b.setTotalValue((nextInt() % 10000) / 10d);
        b.setTotalNetValue((nextInt() % 10000) / 10d);
        b.setOverridedBillingConcept(conceptos.get(nextInt() % conceptos.size()));
        b.setPos(poses.get(nextInt() % poses.size()));
        b.setPrivateComments(LoremIpsum.getInstance().getWords(5, 10));
        b.setValued(true);
        b.setValueOverrided(true);
        b.setCostOverrided(true);
        b.setOverridedValue(b.getTotalValue());
        b.setOverridedCost(b.getTotalCost());
        b.setOverridedCostCurrency(b.getCurrency());

        em.persist(b);
    }

    private static void crearReservaGenerico(EntityManager em, User u, File f, List<Office> oficinas, List<PointOfSale> poses, List<BillingConcept> conceptos, Agency agencia, List<GenericProduct> genericos, CommissionAgent rep, Market market) throws Throwable {
        GenericBooking b = new GenericBooking();
        if (f != null) {
            f.getBookings().add(b);
            b.setFile(f);
        }
        b.setLeadName((f != null)?f.getLeadName():LoremIpsum.getInstance().getName());

        b.setOffice(oficinas.get(nextInt() % oficinas.size()));
        b.setProduct(genericos.get(nextInt() % genericos.size()));
        b.setVariant(b.getProduct().getVariants().get(0));
        b.setPax(nextInt() % 10);
        for (Extra e : b.getProduct().getExtras()) {
            if (nextInt() % 2 == 0) {
                GenericBookingExtra x;
                b.getExtras().add(x = new GenericBookingExtra());
                x.setBooking(b);
                x.setExtra(e);
                x.setUnits(1);
            }
        }

        b.setCommissionAgent(rep);
        b.setAgency(agencia);
        b.setTariff(em.find(Tariff.class, 1l));
        b.setAgencyReference("" + (nextInt() % 1000000000));
        b.setEmail("miguelperezcolom@gmail.com");
        b.setTelephone(LoremIpsum.getInstance().getPhone());
        b.setMarket(market);



        LocalDate d0 = LocalDate.now().minusDays(10).plusDays(nextInt() % 30);
        LocalDate d1 = d0.plusDays(nextInt() % 10);
        //b.setActive(nextInt() % 10 > 2);
        b.setConfirmed(nextInt() % 10 > 2);
        b.setAudit(new Audit(u));
        b.setSpecialRequests("Test booking");
        b.setEnd(d1);
        b.setStart(d0);
        b.setTotalCost((nextInt() % 10000) / 10d);
        b.setTotalValue((nextInt() % 10000) / 10d);
        b.setTotalNetValue((nextInt() % 10000) / 10d);
        b.setOverridedBillingConcept(conceptos.get(nextInt() % conceptos.size()));
        b.setPos(poses.get(nextInt() % poses.size()));
        b.setPrivateComments(LoremIpsum.getInstance().getWords(5, 10));
        b.setValued(true);
        b.setValueOverrided(true);
        b.setCostOverrided(true);
        b.setOverridedValue(b.getTotalValue());
        b.setOverridedCost(b.getTotalCost());
        b.setOverridedCostCurrency(b.getCurrency());


        em.persist(b);
    }

    private static void crearReservaTraslado(EntityManager em, User u, File f, List<TransferPoint> puntos, List<PointOfSale> poses, List<BillingConcept> conceptos, Agency agencia, CommissionAgent rep, Market market) throws Throwable {
        TransferBooking b = new TransferBooking();
        if (f != null) {
            f.getBookings().add(b);
            b.setFile(f);
        }
        b.setLeadName((f != null)?f.getLeadName():LoremIpsum.getInstance().getName());

        b.setCommissionAgent(rep);
        b.setAgency(agencia);
        b.setTariff(em.find(Tariff.class, 1l));
        b.setAgencyReference("" + (nextInt() % 1000000000));
        b.setEmail("miguelperezcolom@gmail.com");
        b.setTelephone(LoremIpsum.getInstance().getPhone());
        b.setMarket(market);


        b.setTransferType(TransferType.SHUTTLE);
        b.setOrigin(puntos.get(nextInt() % puntos.size()));
        b.setDestination(puntos.get(nextInt() % puntos.size()));

        LocalDate d0 = LocalDate.now().minusDays(10).plusDays(nextInt() % 30);
        LocalDate d1 = d0.plusDays(nextInt() % 10);

        b.setArrivalFlightNumber("IB4567");
        b.setArrivalFlightOrigin("MAD");
        b.setArrivalFlightTime(LocalDateTime.of(d0.getYear(), d0.getMonth(), d0.getDayOfMonth(), 11, 25));
        b.setDepartureFlightDestination("BCN");
        b.setDepartureFlightNumber("AE4875");
        b.setDepartureFlightTime(LocalDateTime.of(d1.getYear(), d1.getMonth(), d1.getDayOfMonth(), 10, 15));
        b.setPax(2);

        //b.setActive(nextInt() % 10 > 2);
        b.setConfirmed(nextInt() % 10 > 2);
        b.setAudit(new Audit(u));
        b.setBalance((nextInt() % 10000) / 10d);
        b.setSpecialRequests("Test booking");
        b.setEnd(d1);
        b.setStart(d0);
        b.setTotalCost((nextInt() % 10000) / 10d);
        b.setTotalValue((nextInt() % 10000) / 10d);
        b.setTotalNetValue((nextInt() % 10000) / 10d);
        b.setOverridedBillingConcept(conceptos.get(nextInt() % conceptos.size()));
        b.setPos(poses.get(nextInt() % poses.size()));
        b.setPrivateComments(LoremIpsum.getInstance().getWords(5, 10));
        b.setValued(true);
        b.setValueOverrided(true);
        b.setCostOverrided(true);
        b.setOverridedValue(b.getTotalValue());
        b.setOverridedCost(b.getTotalCost());
        b.setOverridedCostCurrency(b.getCurrency());

        em.persist(b);

    }



    public static void crearReservaHotel(EntityManager em, User u, File f, List<Hotel> hoteles, List<PointOfSale> poses, List<BillingConcept> conceptos, Agency agencia, CommissionAgent rep, Market market) throws Throwable {
        HotelBooking b = new HotelBooking();
        if (f != null) {
            f.getBookings().add(b);
            b.setFile(f);
        }
        b.setLeadName((f != null)?f.getLeadName():LoremIpsum.getInstance().getName());

        b.setCommissionAgent(rep);
        b.setAgency(agencia);
        b.setAgencyReference("" + (nextInt() % 1000000000));
        b.setEmail("miguelperezcolom@gmail.com");
        b.setTelephone(LoremIpsum.getInstance().getPhone());
        b.setMarket(market);

        //b.setActive(nextInt() % 10 > 2);
        b.setConfirmed(nextInt() % 10 > 2);

        LocalDate d0 = LocalDate.now().minusDays(10).plusDays(nextInt() % 30);
        LocalDate d1 = d0.plusDays(nextInt() % 10);

        b.setHotel(hoteles.get(nextInt() % hoteles.size()));
        HotelBookingLine l;
        b.getLines().add(l = new HotelBookingLine());
        l.setBooking(b);
        l.setActive(b.isActive());
        l.setAdultsPerRoom(2);
        l.setBoard(b.getHotel().getBoards().get(nextInt() % b.getHotel().getBoards().size()));
        l.setRoom(b.getHotel().getRooms().get(nextInt() % b.getHotel().getRooms().size()));
        l.setStart(d0);
        l.setEnd(d1);
        l.setRooms(1);


        b.setAudit(new Audit(u));
        b.setBalance((nextInt() % 10000) / 10d);
        b.setSpecialRequests("Test booking");
        b.setEnd(d1);
        b.setStart(d0);
        b.setTotalCost((nextInt() % 10000) / 10d);
        b.setTotalValue((nextInt() % 10000) / 10d);
        b.setTotalNetValue((nextInt() % 10000) / 10d);
        b.setOverridedBillingConcept(conceptos.get(nextInt() % conceptos.size()));
        b.setPos(poses.get(nextInt() % poses.size()));
        b.setPrivateComments(LoremIpsum.getInstance().getWords(5, 10));
        b.setValued(true);
        b.setValueOverrided(true);
        b.setCostOverrided(true);
        b.setOverridedValue(b.getTotalValue());
        b.setOverridedCost(b.getTotalCost());
        b.setOverridedCostCurrency(b.getCurrency());


        em.persist(b);
    }


    private static void crearReservaFreeText(EntityManager em, User u, File f, List<Office> oficinas, List<PointOfSale> poses, List<BillingConcept> conceptos, Agency agencia, List<Provider> proveedores, List<ProductLine> lineasProducto, CommissionAgent rep, Market market) throws Throwable {
        FreeTextBooking b = new FreeTextBooking();
        if (f != null) {
            f.getBookings().add(b);
            b.setFile(f);
        }
        b.setLeadName((f != null)?f.getLeadName():LoremIpsum.getInstance().getName());

        b.setOffice(oficinas.get(nextInt() % oficinas.size()));
        b.setServiceDescription(LoremIpsum.getInstance().getWords(10, 20));
        b.setProductLine(lineasProducto.get(nextInt() % lineasProducto.size()));
        b.setProvider(proveedores.get(nextInt() % proveedores.size()));
        b.setOverridedBillingConcept(conceptos.get(nextInt() % conceptos.size()));

        b.setCommissionAgent(rep);
        b.setAgency(agencia);
        b.setAgencyReference("" + (nextInt() % 1000000000));
        b.setEmail("miguelperezcolom@gmail.com");
        b.setTelephone(LoremIpsum.getInstance().getPhone());
        b.setMarket(market);

        b.setPax(1 + (nextInt() % 2));
        LocalDate d0 = LocalDate.now().minusDays(10).plusDays(nextInt() % 30);
        LocalDate d1 = d0.plusDays(nextInt() % 10);
        //b.setActive(nextInt() % 10 > 2);
        b.setAudit(new Audit(u));
        b.setBalance((nextInt() % 10000) / 10d);
        b.setSpecialRequests("Test booking");
        b.setEnd(d1);
        b.setStart(d0);
        b.setTotalCost((nextInt() % 10000) / 10d);
        b.setTotalValue((nextInt() % 10000) / 10d);
        b.setTotalNetValue((nextInt() % 10000) / 10d);
        b.setOverridedBillingConcept(conceptos.get(nextInt() % conceptos.size()));
        b.setOverridedValue(b.getTotalNetValue());
        b.setPos(poses.get(nextInt() % poses.size()));
        b.setPrivateComments(LoremIpsum.getInstance().getWords(5, 10));
        b.setValued(true);
        b.setValueOverrided(true);

        b.setCostOverrided(true);
        b.setOverridedCost(b.getTotalCost());
        b.setOverridedCostCurrency(b.getAgency().getCurrency());

        if (b.getOverridedValue() == 0) b.setOverridedValue(150.33);

        em.persist(b);
    }

    private static int nextInt() {
        return Math.abs(new Random().nextInt());
    }

    private static void crearPartners() throws Throwable {

        Helper.transact(em -> {

            List<Market> markets = new ArrayList<>();

            for (String n : Lists.newArrayList("Nacional", "Británico", "Alemán", "Italiano")) {
                Market m;
                markets.add(m = new Market());
                m.setName(n);
                em.persist(m);
                em.flush();
            }

            Markup mu = new Markup();
            mu.setActive(true);
            mu.setName("Markup general");
            em.persist(mu);
            em.flush();

            Map<String, Object> data = Helper.fromYaml(Helper.leerInputStream(TestPopulator.class.getResourceAsStream("/testdata/partners.yaml"), "utf-8"));

            for (String pn : (List<String>) data.get("agencias")) {
                crearAgencia(em, pn, mu, markets);
            }

            for (String pn : (List<String>) data.get("transportistas")) {
                crearProveedor(em, pn);
            }

            for (String pn : (List<String>) data.get("terceros")) {
                crearAgencia(em, pn, mu, markets);
                crearProveedor(em, pn);
            }

        });


        Helper.transact(em -> {

            Map<String, Object> data = Helper.fromYaml(Helper.leerInputStream(TestPopulator.class.getResourceAsStream("/testdata/partners.yaml"), "utf-8"));

            int pos = 1;
            for (String pn : (List<String>) data.get("reps")) {
                crearRep(em, pn, pos++);
            }

        });

    }

    private static void crearRep(EntityManager em, String pn, int i) throws Throwable {

        List<Office> oficinas = Helper.selectObjects("select x from " + Office.class.getName() + " x");

        PointOfSale pos = new PointOfSale();
        pos.setName(pn);
        pos.setTariff(em.find(Tariff.class, 1l));
        pos.setOffice(oficinas.get(0));
        pos.setFinancialAgent(crearAgenteFinanciero(em, pn, pn));
        pos.setSalesPoint(em.find(SalesPoint.class, 1l));
        em.persist(pos);


        CommissionAgent a = new CommissionAgent();
        a.setName(pn);
        a.setStatus(CommissionAgentStatus.ACTIVE);
        a.setEmail("miguelperezcolom@gmail.com");
        a.setTelephone("xx");
        a.setComments("xx");
        a.setFullAddress("xx");
        a.setFinancialAgent(pos.getFinancialAgent());
        em.persist(a);



        BankAccount banco = (BankAccount) Helper.selectObjects("select x from " + BankAccount.class.getName() + " x").get(0);
        Agency agencia = (Agency) Helper.selectObjects("select x from " + Agency.class.getName() + " x").get(0);


        CommissionAgentUser u = new CommissionAgentUser();
        u.setAgency(agencia);
        u.setCommissionAgent(a);
        u.setBank(banco);
        u.setPointOfSale(pos);
        u.setLogin("rep" + i);
        u.setName(pn);
        u.setEmail("miguelperezcolom@gmail.com");
        u.setAudit(new Audit(MDD.getCurrentUser()));
        u.setStatus(USER_STATUS.ACTIVE);
        u.setPassword("" + i);
        em.persist(u);
    }

    private static void crearAgencia(EntityManager em, String pn, Markup mu, List<Market> markets) {


        Agency p = new Agency();
        p.setStatus(AgencyStatus.ACTIVE);
        p.setBalance(Helper.roundEuros(Math.abs(new Random().nextDouble()) * 100000d));
        p.setBookings(Math.abs(new Random().nextInt()) % 10000);
        p.setComments("");
        p.setCompany(em.find(Company.class, 1l));
        p.setCurrency(em.find(Currency.class, "EUR"));
        p.setEmail("miguelperezcolom@gmail.com");
        p.setExportableToinvoicingApp(true);
        p.setFinancialAgent(crearAgenteFinanciero(em, pn, pn));
        p.setInvoiced(Helper.roundEuros(Math.abs(new Random().nextDouble()) * 100000d));
        p.setMarket(markets.get(Math.abs(new Random().nextInt()) % markets.size()));
        p.setMarkup(mu);
        mu.getAgencies().add(p);
        p.setName(pn);
        p.setOnRequestAllowed(true);
        p.setPVPAllowed(true);
        p.setThridPartyAllowed(true);
        p.setDocumentationRequired(true);

        em.persist(p);

    }

    private static void crearProveedor(EntityManager em, String pn) {


        Provider p = new Provider();
        p.setAutomaticOrderConfirmation(true);
        p.setStatus(ProviderStatus.ACTIVE);
        p.setComments("");
        p.setCurrency(em.find(Currency.class, "EUR"));
        p.setEmail("miguelperezcolom@gmail.com");
        p.setExportableToinvoicingApp(true);
        p.setFinancialAgent(crearAgenteFinanciero(em, pn, pn));
        p.setAutomaticOrderSending(true);
        p.setName(pn);
        p.setOrdersSendingMethod(PurchaseOrderSendingMethod.EMAIL);
        p.setPayableByInVoucher(pn);
        p.setSendOrdersTo("miguelperezcolom@gmail.com");

        em.persist(p);

    }
    private static void crearCodigos() throws Throwable {

        Helper.transact(em -> {
            Tariff t = new Tariff();
            t.setName("Estándar");
            t.setNameToShow(new Literal("Standard", "Estándar"));
            t.setAvailableOnline(true);
            em.persist(t);
        });

        Helper.transact(em -> {

            RoomType r = new RoomType();
            r.setCode("DBL");
            r.setName(new Literal("Double room", "Habitación doble"));
            em.persist(r);

            r = new RoomType();
            r.setCode("SUI");
            r.setName(new Literal("Suite", "Suite"));
            em.persist(r);

            r = new RoomType();
            r.setCode("DUI");
            r.setName(new Literal("Double single use", "Habitación doble uso individual"));
            em.persist(r);

            r = new RoomType();
            r.setCode("APT");
            r.setName(new Literal("2 rooms apartment", "Apartamento 2 habitaciones"));
            em.persist(r);

            r = new RoomType();
            r.setCode("JSUI");
            r.setName(new Literal("Junior suite", "Junior suite"));
            em.persist(r);


            BoardType b = new BoardType();
            b.setCode("RO");
            b.setName(new Literal("Room only", "Solo alojamiento"));
            em.persist(b);

            b = new BoardType();
            b.setCode("BB");
            b.setName(new Literal("Bed and breakfast", "Alojamiento y desayuno"));
            em.persist(b);

            b = new BoardType();
            b.setCode("HB");
            b.setName(new Literal("Half board", "Media pensión"));
            em.persist(b);

            b = new BoardType();
            b.setCode("FB");
            b.setName(new Literal("Full board", "Pensión completa"));
            em.persist(b);


            HotelCategory c = new HotelCategory();
            c.setCode("*");
            c.setName(new Literal("1 star", "1 estrella"));
            c.setStars(1);
            em.persist(c);

            em.flush();

            c = new HotelCategory();
            c.setCode("**");
            c.setName(new Literal("2 stars", "2 estrellas"));
            c.setStars(2);
            em.persist(c);

            em.flush();

            c = new HotelCategory();
            c.setCode("***");
            c.setName(new Literal("3 stars", "3 estrellas"));
            c.setStars(3);
            em.persist(c);

            em.flush();

            c = new HotelCategory();
            c.setCode("****");
            c.setName(new Literal("4 stars", "4 estrellas"));
            c.setStars(4);
            em.persist(c);

            em.flush();

            c = new HotelCategory();
            c.setCode("*****");
            c.setName(new Literal("5 stars", "5 estrellas"));
            c.setStars(5);
            em.persist(c);

            em.flush();


            HotelType ht = new HotelType();
            ht.setName("Hotel");
            ht.setNameTranslated(new Literal("Hotel", "Hotel"));
            em.persist(ht);
            em.flush();

            ProductType pt = new ProductType();
            pt.setName("Hotel");
            em.persist(pt);

            em.flush();

            pt = new ProductType();
            pt.setName("Transfer");
            em.persist(pt);

            em.flush();

            pt = new ProductType();
            pt.setName("Excursion");
            em.persist(pt);

            em.flush();

            pt = new ProductType();
            pt.setName("Circuit");
            em.persist(pt);

            em.flush();

            pt = new ProductType();
            pt.setName("Transport");
            em.persist(pt);

            em.flush();

            pt = new ProductType();
            pt.setName("Rent a car");
            em.persist(pt);

            em.flush();

            pt = new ProductType();
            pt.setName("Official guide");
            em.persist(pt);

            em.flush();




            ProductLine l = new ProductLine();
            l.setName("Hoteles Mallorca");
            em.persist(l);

            l = new ProductLine();
            l.setName("Hoteles Ibiza y Menorca");
            em.persist(l);

            l = new ProductLine();
            l.setName("Hoteles resto mundo");
            em.persist(l);

            l = new ProductLine();
            l.setName("Traslados");
            em.persist(l);

            l = new ProductLine();
            l.setName("Otros productos");
            em.persist(l);

            l = new ProductLine();
            l.setName("Sunhotels");
            em.persist(l);

            ExcursionLanguage el = new ExcursionLanguage();
            el.setCode("es");
            el.setName(new Literal("Spanish", "Español"));
            em.persist(el);

            el = new ExcursionLanguage();
            el.setCode("en");
            el.setName(new Literal("English", "Inglés"));
            em.persist(el);

        });

    }

    private static void createVats() throws Throwable {

        Helper.transact(em -> {

            VAT v = new VAT();
            v.setName("IVA");
            v.setSpecialRegimePercent(21);
            em.persist(v);

            em.flush();

            v = new VAT();
            v.setName("IGIC");
            v.setSpecialRegimePercent(21);
            em.persist(v);


            MethodOfPayment pm = new MethodOfPayment();
            pm.setName("Contado");
            em.persist(pm);

            em.flush();

            pm = new MethodOfPayment();
            pm.setName("VISA");
            em.persist(pm);

            em.flush();

            pm = new MethodOfPayment();
            pm.setName("Transferencia bancaria");
            em.persist(pm);

            em.flush();

            PaymentTerms pt = new PaymentTerms();
            pt.setName("Prepago");
            PaymentTermsLine l;
            pt.getLines().add(l = new PaymentTermsLine());
            l.setTerms(pt);
            l.setPercent(100);
            l.setReferenceDate(PaymentReferenceDate.CONFIRMATION);
            l.setRelease(0);
            em.persist(pt);

            em.flush();

            pt = new PaymentTerms();
            pt.setName("FF + 30");
            pt.getLines().add(l = new PaymentTermsLine());
            l.setTerms(pt);
            l.setPercent(100);
            l.setReferenceDate(PaymentReferenceDate.INVOICE);
            l.setRelease(30);
            em.persist(pt);

            em.flush();

            pt = new PaymentTerms();
            pt.setName("CHKIN - 15");
            pt.getLines().add(l = new PaymentTermsLine());
            l.setTerms(pt);
            l.setPercent(100);
            l.setReferenceDate(PaymentReferenceDate.ARRIVAL);
            l.setRelease(-15);
            em.persist(pt);

            em.flush();
        });

    }

    private static void createWorld() throws Throwable {

        Helper.transact(em -> {

            Company co = new Company();
            co.setName("Viajes Ibiza");
            co.setFinancialAgent(crearAgenteFinanciero(em, co.getName(), "Invisa"));
            AccountingPlan plan;
            co.setAccountingPlan(plan = new AccountingPlan());
            plan.setName("Plan general");
            plan.setCurrency(em.find(Currency.class, "EUR"));
            InvoiceSerial is;
            co.setBillingSerial(is = new InvoiceSerial());
            is.setName("Facturas emitidas");
            is.setNextNumber(1);
            is.setPrefix("2019/");
            em.persist(is);
            co.setSelfBillingSerial(is = new InvoiceSerial());
            is.setName("Abonos");
            is.setNextNumber(1);
            is.setPrefix("R-2019/");
            em.persist(is);
            em.persist(plan);
            em.persist(co);
            em.flush();

            List<ProductLine> lineasProducto = Helper.selectObjects("select x from " + ProductLine.class.getName() + " x");

            Map<String, Object> data = Helper.fromYaml(Helper.leerInputStream(TestPopulator.class.getResourceAsStream("/testdata/world.yaml"), "utf-8"));

            Office o = null;

            for (Map<String, Object> dp : (List<Map<String, Object>>) data.get("paises")) {
                Country c = new Country();
                c.setIsoCode((String) dp.get("codigo"));
                c.setName((String) dp.get("nombre"));
                if ("es".equalsIgnoreCase(c.getIsoCode())) c.setVat(em.find(VAT.class, 1l));
                em.persist(c);
                em.flush();

                if (!Strings.isNullOrEmpty((String) dp.get("oficina"))) {
                    o = new Office();
                    o.setName((String) dp.get("oficina"));
                    o.setCurrency(em.find(Currency.class, "EUR"));
                    o.setCompany(co);
                }

                for (Map<String, Object> dd : (List<Map<String, Object>>) dp.get("destinos")) {

                    Destination d = new Destination();
                    d.setCountry(c);
                    c.getDestinations().add(d);
                    d.setName((String) dd.get("nombre"));
                    em.persist(d);

                    if (!Strings.isNullOrEmpty((String) dd.get("oficina"))) {
                        o = new Office();
                        o.setName((String) dd.get("oficina"));
                        o.setCurrency(em.find(Currency.class, "EUR"));
                        o.setCompany(co);
                    }

                    crearAeropuertos(em, d, o, dd);

                    for (Map<String, Object> dz : (List<Map<String, Object>>) dd.get("zonas")) {

                        Resort z = new Resort();
                        z.setDestination(d);
                        d.getResorts().add(z);
                        z.setName((String) dz.get("nombre"));
                        em.persist(z);

                        if (!Strings.isNullOrEmpty((String) dz.get("oficina"))) {
                            o = new Office();
                            o.setName((String) dz.get("oficina"));
                            o.setCurrency(em.find(Currency.class, "EUR"));
                            o.setCompany(co);
                            o.setEmail("miguelperezcolom@gmail.com");
                        }

                        if (o != null && o.getResort() == null) {
                            o.setResort(z);

                            /*
                            TransferPoint dtpft = new TransferPoint();
                            dtpft.setZone(z);
                            dtpft.setType(TransferPointType.AIRPORT);
                            dtpft.setName(z.getName() + " airport");
                            dtpft.setOffice(o);
                            o.setDefaultAirportForTransfers(dtpft);
                            em.persist(dtpft);
                            */

                            em.persist(o);
                            em.flush();
                        }

                        for (String hn : (List<String>) dz.get("hoteles")) {

                            crearHotel(em, z, hn, lineasProducto);

                        }
                    }

                }

            }

            SalesPoint sp = new SalesPoint();
            sp.setName("Office");
            sp.setOffice(o);
            em.persist(sp);


            PointOfSale pos = new PointOfSale();
            pos.setName("Web");
            pos.setOffice(o);
            pos.setTariff(em.find(Tariff.class, 1l));
            pos.setSalesPoint(sp);
            em.persist(pos);

            pos = new PointOfSale();
            pos.setName("Agencia");
            pos.setOffice(o);
            pos.setTariff(em.find(Tariff.class, 1l));
            pos.setSalesPoint(sp);
            em.persist(pos);



            //System.out.println(Helper.toJson(data));

        });

    }

    private static void crearAeropuertos(EntityManager em, Destination d, Office o, Map<String, Object> dd) {


        if (dd.containsKey("aeropuerto")) {

            Map<String,Object> da = (Map<String, Object>) dd.get("aeropuerto");

            Airport a;
            d.getAirports().add(a = new Airport());
            a.setDestination(d);
            a.setIataCode((String) da.get("codigo"));
            a.setName((String) da.get("nombre"));

            Resort r;
            d.getResorts().add(r = new Resort());
            r.setDestination(d);
            r.setName(a.getName());
            em.persist(r);

            TransferPoint p;
            a.setTransferPoint(p = new TransferPoint());
            p.setOffice(o);
            p.setResort(r);
            r.getTransferPoints().add(p);
            p.setType(TransferPointType.AIRPORT);
            p.setName(a.getName());
            p.setArrivalInstructionsForPrivate(new Literal(LoremIpsum.getInstance().getWords(30, 70), LoremIpsum.getInstance().getWords(30, 70)));
            p.setArrivalInstructionsForShuttle(new Literal(LoremIpsum.getInstance().getWords(30, 70), LoremIpsum.getInstance().getWords(30, 70)));
            p.setDepartureInstructions(new Literal(LoremIpsum.getInstance().getWords(30, 70), LoremIpsum.getInstance().getWords(30, 70)));

            em.persist(p);

            em.persist(a);

        }


    }

    private static FinancialAgent crearAgenteFinanciero(EntityManager em, String name, String busName) {
        FinancialAgent a = new FinancialAgent();

        a.setBusinessName(busName);
        a.setName(name);
        a.setAddress(LoremIpsum.getInstance().getCity());
        a.setAutomaticInvoiceBasis(AutomaticInvoiceBasis.NONE);
        a.setCity(LoremIpsum.getInstance().getCity());
        a.setCountry(LoremIpsum.getInstance().getCountry());
        a.setEmail("miguelperezcolom@gmail.com");
        a.setEU(true);
        a.setInvoiceGrouping(InvoiceGrouping.BOOKING);
        a.setMethodOfPayment(em.find(MethodOfPayment.class, 2l));
        a.setCustomerPaymentTerms(em.find(PaymentTerms.class, 2l));
        a.setPostalCode("07001");
        a.setRetention(null);
        a.setRiskType(RiskType.CREDIT);
        a.setSpecialRegime(true);
        a.setVat(em.find(VAT.class, 1l));
        a.setVatIdentificationNumber("A638978787333");

        em.persist(a);
        return a;
    }

    private static void crearHotel(EntityManager em, Resort z, String hn, List<ProductLine> lineasProducto) throws MalformedURLException {

        Hotel h = new Hotel();
        h.setResort(z);
        z.getProducts().add(h);
        h.setName(hn);
        h.setOffice(em.find(Office.class, 1l));
        h.setAddress(LoremIpsum.getInstance().getName());
        h.setAdultStartAge(16);
        h.setHotelType(em.find(HotelType.class, 1l));
        h.setProductLine(lineasProducto.get(nextInt() % lineasProducto.size()));
        Board b;
        h.getBoards().add(b = new Board());
        b.setHotel(h);
        b.setType(em.find(BoardType.class, "BB"));
        b.setDescription(new Literal(LoremIpsum.getInstance().getName(), LoremIpsum.getInstance().getName()));
        h.setCategory(em.find(HotelCategory.class, "**"));
        h.setChildStartAge(2);
        //h.setContracts();
        h.setEmail("miguelperezcolom@gmail.com");
        h.setFax(LoremIpsum.getInstance().getPhone());
        h.setJuniorStartAge(0);
        //h.setInventories();
        h.setLat("15.454564654");
        h.setLon("0.212456455");
        //h.setOffers();
        //h.setRealInventory();
        Room r;
        h.getRooms().add(r = new Room());
        r.setHotel(h);
        r.setInfantsAllowed(true);
        r.setChildrenAllowed(true);
        r.setMinPax(1);
        r.setType(em.find(RoomType.class, "DBL"));
        r.setDescription(new Literal(LoremIpsum.getInstance().getName(), LoremIpsum.getInstance().getName()));
        r.setPhoto(new Resource(new URL("https://x.cdrst.com/foto/hotel-sf/fd1/medianaresp/hotel-los-delfines-servicios-5502a1d.jpg")));
        //h.setStopSales();
        h.setTelephone(LoremIpsum.getInstance().getPhone());
        //h.setTransferPoint();
        h.setZip(LoremIpsum.getInstance().getZipCode());
        h.setActive(true);
        h.setOffice(em.find(Office.class, 1l));
        h.setType(em.find(ProductType.class, 1l));
        h.setDataSheet(crerDataSheet(em, h));
        em.persist(h);

        crearTransferPoint(em, h);

        em.flush();

    }

    private static DataSheet crerDataSheet(EntityManager em, Hotel h) throws MalformedURLException {
        DataSheet s = new DataSheet();
        s.setDescription(new Literal(LoremIpsum.getInstance().getName(), LoremIpsum.getInstance().getName()));
        s.setName(h.getName());
        s.setMainImage(new Resource(new URL("https://x.cdrst.com/foto/hotel-sf/fd1/medianaresp/hotel-los-delfines-servicios-5502a1d.jpg")));
        em.persist(s.getMainImage());
        em.persist(s);
        return s;
    }

    private static void crearTransferPoint(EntityManager em, Hotel h) {

        TransferPoint p = new TransferPoint();
        h.setTransferPoint(p);
        p.setName(h.getName());
        p.setOffice(h.getOffice());
        p.setType(TransferPointType.HOTEL);
        p.setResort(h.getResort());
        p.getResort().getTransferPoints().add(p);
        em.persist(p);

    }

    private static void createCurrencies() throws Throwable {

        Helper.transact(em -> {

            for (String[] d : new String[][]{
                    {"EUR", "Euro", "978"}
                    , {"USD", "US dollar", "840"}
                    , {"GBP", "GB Pound", "826"}}) {

                if (Helper.find(Currency.class, d[0]) == null) {
                    Currency c = new Currency();
                    c.setIsoCode(d[0]);
                    c.setName(d[1]);
                    c.setIsoNumericCode(Integer.parseInt(d[2]));
                    em.persist(c);
                }

            }


        });

        Helper.transact(em -> {
            AppConfig.get(em).setNucCurrency(em.find(Currency.class, "EUR"));
        } );

    }


}
