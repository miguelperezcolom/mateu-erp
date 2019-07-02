package io.mateu.erp.services.easytravelapi.test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.booking.*;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.organization.PointOfSaleSettlementForm;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.population.Populator;
import io.mateu.erp.model.tpv.TPV;
import io.mateu.erp.model.tpv.TPVTransaction;
import io.mateu.erp.model.workflow.SendPurchaseOrdersByEmailTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.erp.services.easytravelapi.*;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import org.easytravelapi.generic.BookGenericRQ;
import org.easytravelapi.hotel.BookHotelRQ;
import org.easytravelapi.hotel.BookingKey;
import org.easytravelapi.hotel.GetHotelPriceDetailsRQ;
import org.easytravelapi.hotel.GetHotelRatesRQ;
import org.easytravelapi.transfer.BookTransferRQ;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class CommonsTester {


    public static void main(String[] args) {
        System.setProperty("appconf", "/home/miguel/work/quotravel.properties");

        EmailHelper.setTesting(true);

        String token = "eyAiY3JlYXRlZCI6ICJGcmkgTWFyIDIyIDEwOjIyOjI5IENFVCAyMDE5IiwgInVzZXJJZCI6ICJhZG1pbiIsICJhZ2VuY3lJZCI6ICIzIn0=";
        token = "eyAiY3JlYXRlZCI6ICJGcmkgTWFyIDIyIDEwOjIyOjI5IENFVCAyMDE5IiwgInVzZXJJZCI6ICJhZG1pbiIsICJhZ2VuY3lJZCI6ICIzIn0=";

        //testBooked();

        //testTPV();

        //testPriceTransferBooking();

        //testBorrarDuplicados();

        //testEnvioServiciosGuitart();

        //testSms();


        //testTransferRQ();

        //testCambioVuelo();

        //testGroupProforma();

        //testDuplicarGrupo();

        //testFileProforma();

        //testzn();

        //testzm();

        //testzq();

        //testPickupEmail();

        //testzz();

        //testxx();


        //testPortfolio(token);

        //testAvailHotels(token);

        //testHotelRates(token);

        //testHotelDetails(token);

        //testHotelConfirm(token);

        //testAvailTransfers(token);

        //testAvailTransfers(token);

        //testTransferDetails(token);

        //testTransferConfirm(token);
        
        //testGenericAvail(token);

        //testGenericRates(token);

        //testGenericCheck(token);

        //testGenericPriceDetail(token);

        //testGenericConfirm(token);

        //testTourAvail(token);

        //testExcursionAvail(token);

        //testExcursionRates(token);

        //testCircuitAvail(token);

        //testCircuitRates(token);

        //testPurchaseOrder();

        //testGroup();

        testInvoice();

        //testLiquidacion();

        //testLlegadaSalida();

        //testManifiesto();

        //testInformeEvento();

        WorkflowEngine.exit(0);
    }

    private static void testBooked() {

        try {

            Helper.transact(em -> {

                TransferBooking b = em.find(TransferBooking.class, 14280l);
                b.price(em);
                System.out.println("" + b.getTotalValue() + "/" + b.getTotalCost());

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


    }

    private static void testTPV()  {

        TPVTransaction t = new TPVTransaction();
        try {
            Helper.transact(em -> {

                t.setTpv(em.find(TPV.class, 1l));
                t.setSubject("TEST");
                t.setBooking(em.find(Booking.class, 1l));
                t.setValue(1);
                t.setCurrency(em.find(Currency.class, "EUR"));
                t.setLanguage("es");
                em.persist(t);

            });

            Helper.escribirFichero("/home/miguel/work/tpv.html", "<html><body>" + TPVTransaction.getForm(t.getId()) + "</body></html>");

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


    }

    private static void testPriceTransferBooking() {

        try {

            Helper.transact(em -> {

                TransferBooking b = em.find(TransferBooking.class, 14280l);
                b.price(em);
                System.out.println("" + b.getTotalValue() + "/" + b.getTotalCost());

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testDuplicarGrupo() {

        try {

            Helper.transact(em -> {

                QuotationRequest qr = em.find(QuotationRequest.class, 3l).createDuplicate();
                em.persist(qr);
                qr.confirm();
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testEnvioServiciosGuitart() {

        try {
            Helper.transact(em -> {

                Set<Service> ss = new HashSet<>();

                for (long id : new long[] {1l}) ss.add(em.find(Service.class, id));

                Service.sendToProvider(em, ss, em.find(Provider.class, 1l), null, null);

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


    }

    private static void testBorrarDuplicados() {

        try {

            for (long id : new long[] {13392l, 13393l}) try {
                Helper.transact(em -> {
                    System.out.println("eliminando reserva duplicada " + id);
                    Booking b = em.find(Booking.class, id);
                    if (b instanceof TransferBooking) {
                        TransferBooking tb = (TransferBooking) b;
                        List<TransferBookingRequest> rqs = em.createQuery("select x from " + TransferBookingRequest.class.getName() + " x where x.booking = :b").setParameter("b", b).getResultList();
                        rqs.forEach(rq -> {
                            rq.setRemoved(true);
                            rq.setBooking(null);
                        });
                    }
                    em.remove(b);
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }


            for (long id : new long[] {13396l, 13397l}) try {
                Helper.transact(em -> {
                    Booking b = em.find(Booking.class, id);
                    if (b != null) System.out.println("reserva " + id + " exite xxxxxxxx");
                    System.out.println("reserva " + id + " eliminada ok");
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void testSms() {

        try {
            Helper.transact(em -> {

                AppConfig.get(em).setPickupSmsTemplate(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/freemarker/pickupsms.ftl"), Charsets.UTF_8));

            });

            Helper.transact(em -> {

                TransferService s = em.find(TransferService.class, 2170l);

                System.out.println("********************************************");
                System.out.println("********************************************");
                System.out.println("********************************************");

                System.out.println(Helper.toJson(s.getData()));

                System.out.println("********************************************");
                System.out.println("********************************************");
                System.out.println("********************************************");


                System.out.println(Helper.freemark(AppConfig.get(em).getPickupSmsTemplate(), s.getData()));

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


    }

    private static void testTransferRQ() {

        try {
            Helper.transact(em -> {

                TransferBookingRequest b = em.find(TransferBookingRequest.class, 1528l);

                b.forceUpdate(em);

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        try {
            Helper.transact(em -> {

                TransferBookingRequest b = em.find(TransferBookingRequest.class, 1528l);

                System.out.println("reserva activa = " + b.getBooking().isActive());

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testCambioVuelo() {

        try {
            Helper.transact(em -> {

                TransferBooking b = em.find(TransferBooking.class, 13233l);

                b.setArrivalFlightNumber("EZY6691x");
                b.setArrivalFlightTime(LocalDateTime.of(2019, 5, 1, 21, 10));

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testzn() {

        try {
            Helper.transact(em -> {

                TransferBookingRequest rq = em.find(TransferBookingRequest.class, 1257l);

                rq.updateBooking(em);


            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testzm() {

        try {
            Helper.transact(em -> {

                TransferService s = em.find(TransferService.class, 2001l);

                s.sendToProvider(em, null, "miguelperezcolom@gmail.com", null);

            });


            Helper.transact(em -> {

                TransferService s = em.find(TransferService.class, 2001l);

                s.getPurchaseOrders().get(0).getSendingTasks().get(s.getPurchaseOrders().get(0).getSendingTasks().size() - 1).run(em, MDD.getCurrentUser());

            });


            Helper.transact(em -> {

                TransferService s = em.find(TransferService.class, 2001l);

                System.out.println("s.getProcessingStatus()=" + s.getProcessingStatus());

            });

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testzq() {

        try {
            Helper.transact(em -> {

                TransferBooking b = em.find(TransferBooking.class, 12773l);

                b.setDepartureFlightNumber(b.getDepartureFlightNumber() + "x");


            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testPickupEmail() {

        try {
            Helper.transact(em -> {

                AppConfig.get(em).setPickupEmailTemplate(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/freemarker/pickupemail.ftl"), Charsets.UTF_8));

            });

            Helper.transact(em -> {

                TransferService s = em.find(TransferService.class, 2170l);

                System.out.println("********************************************");
                System.out.println("********************************************");
                System.out.println("********************************************");

                System.out.println(Helper.toJson(s.getData()));

                System.out.println("********************************************");
                System.out.println("********************************************");
                System.out.println("********************************************");


                Helper.escribirFichero("/home/miguel/Descargas/email.html", Helper.freemark(AppConfig.get(em).getPickupEmailTemplate(), s.getData()));

                s.sendEmailToHotel(em);

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testzz() {

        try {
            Helper.transact(em -> {

                List<TransferBooking> reservas = em.createQuery("select x from " + TransferBooking.class.getName() + " x where x.audit.created > :d and x.arrivalFlightTime = null").setParameter("d", LocalDateTime.of(2019, 02, 28, 0, 1)).getResultList();

                for (TransferBooking reserva : reservas) {
                    reserva.generateServices(em);
                }

            });
        } catch (Throwable throwable) {
            MDD.alert(throwable);
        }


    }

    private static void testInformeEvento() {
        try {

            Helper.transact(em -> {

                AppConfig.get(em).setXslfoForEventReport(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/event_report.xsl"), Charsets.UTF_8));

            });


            Helper.transact(em -> {

                ManagedEvent pos = em.find(ManagedEvent.class, 5807l);

                pos.crearReport(em, new File("/home/miguel/Descargas/manifiesto.pdf"));


            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testManifiesto() {
        try {

            Helper.transact(em -> {

                AppConfig.get(em).setXslfoForEventManifest(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/event_manifest.xsl"), Charsets.UTF_8));

            });


            Helper.transact(em -> {

                ManagedEvent pos = em.find(ManagedEvent.class, 5807l);

                pos.crearPdf(em, new File("/home/miguel/Descargas/manifiesto.pdf"));


            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    private static void testLlegadaSalida() {


        try {
            Helper.transact(em -> {

                Booking b = em.find(Booking.class, 12820l);

                b.setSpecialRequests("" + LocalDateTime.now());


            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testLiquidacion() {
        try {

            Helper.transact(em -> {

                AppConfig.get(em).setXslfoForPOSSettlement(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/liquidacion_pos.xsl"), Charsets.UTF_8));

            });


            Helper.transact(em -> {

                PointOfSale pos = em.find(PointOfSale.class, 1l);

                PointOfSaleSettlementForm liq = new PointOfSaleSettlementForm(pos);
                liq.search();

                liq.crearPdf(em, new File("/home/miguel/Descargas/liquidacion.pdf"));


            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testInvoice() {

        try {
            Helper.transact(em -> {

                AppConfig.get(em).setXslfoForIssuedInvoice(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/factura.xsl"), Charsets.UTF_8));

            });

            Helper.transact(em -> {

                IssuedInvoice i = em.find(IssuedInvoice.class, 84l);

                Document xml = new Document(new Element("invoices"));

                xml.getRootElement().addContent(i.toXml(em));

                System.out.println(Helper.toString(xml.getRootElement()));


                File temp = new File("/home/miguel/Descargas/factura.pdf");

                FileOutputStream fileOut = new FileOutputStream(temp);
                //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
                String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
                System.out.println("xml=" + sxml);
                fileOut.write(Helper.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForIssuedInvoice())), new StreamSource(new StringReader(sxml))));
                fileOut.close();

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testFileInvoice() {

        try {
            Helper.transact(em -> {

                AppConfig.get(em).setXslfoForIssuedInvoice(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/factura.xsl"), Charsets.UTF_8));

            });

            Helper.transact(em -> {

                io.mateu.erp.model.booking.File file = em.find(io.mateu.erp.model.booking.File.class, 320l);

                Document xml = new Document(new Element("invoices"));

                List<BookingCharge> charges = new ArrayList<>();
                for (Booking b : file.getBookings()) charges.addAll(b.getCharges());

                Booking firstBooking = file.getBookings().size() > 0?file.getBookings().get(0):null;


                if (firstBooking != null) xml.getRootElement().addContent(new IssuedInvoice(MDD.getCurrentUser(), charges, true, firstBooking.getAgency().getCompany().getFinancialAgent(), firstBooking.getAgency().getFinancialAgent(), null).toXml(em));

                System.out.println(Helper.toString(xml.getRootElement()));


                File temp = new File("/home/miguel/Descargas/factura.pdf");

                FileOutputStream fileOut = new FileOutputStream(temp);
                //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
                String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
                System.out.println("xml=" + sxml);
                fileOut.write(Helper.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForIssuedInvoice())), new StreamSource(new StringReader(sxml))));
                fileOut.close();

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    public static void testPurchaseOrder() {

        try {
            Helper.transact(em -> {

                //AppConfig.get(em).setXslfoForQuotationRequest(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/grupo.xsl"), Charsets.UTF_8));
                AppConfig.get(em).setPurchaseOrderTemplate(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/freemarker/purchaseorder.ftl"), Charsets.UTF_8));

            });

            Helper.transact(em -> {

                AppConfig appconfig = AppConfig.get(em);

                PurchaseOrder po = em.find(PurchaseOrder.class, 23l);

                //po.setConfirmationNeeded(true);

                for (SendPurchaseOrdersTask t : po.getSendingTasks()) {
                    Map<String, Object> data = t.getData();
                    System.out.println("data=" + Helper.toJson(data));

                    String msg = Helper.freemark(AppConfig.get(em).getPurchaseOrderTemplate(), data);

                    if (msg.contains("mylogosrc") && appconfig.getLogo() != null) {
                        URL url = new URL(appconfig.getLogo().toFileLocator().getUrl());
                        String cid = url.toString(); //email.embed(url, "" + appconfig.getBusinessName() + " logo");
                        msg = msg.replaceAll("mylogosrc", cid); //"cid:" + cid);
                    }

                    Helper.escribirFichero("/home/miguel/Descargas/po_" + t.getId() + ".html", msg);
                }


            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testGroup() {

        try {
            Helper.transact(em -> {

                //AppConfig.get(em).setXslfoForQuotationRequest(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/grupo.xsl"), Charsets.UTF_8));
                AppConfig.get(em).setPurchaseOrderTemplate(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/freemarker/purchaseorder.ftl"), Charsets.UTF_8));

            });

            Helper.transact(em -> {

                QuotationRequest r = em.find(QuotationRequest.class, 1l);

                //r.createProforma(em, new File("/home/miguel/Descargas/testGrupo.pdf"));
                r.confirm();

                List<PurchaseOrder> pos = new ArrayList<>();

                for (Booking b : r.getFile().getBookings()) for (Service s : b.getServices()) pos.addAll(s.getPurchaseOrders());

                for (PurchaseOrder po : pos) {
                    for (SendPurchaseOrdersTask t : po.getSendingTasks()) {
                        Map<String, Object> data = t.getData();
                        System.out.println("data=" + Helper.toJson(data));
                        Helper.escribirFichero("/home/miguel/Descargas/po_" + t.getId() + ".html", Helper.freemark(AppConfig.get(em).getPurchaseOrderTemplate(), data));
                    }
                }

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }


    private static void testGroupProforma() {

        try {
            Helper.transact(em -> {

                AppConfig.get(em).setXslfoForQuotationRequest(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/grupo.xsl"), Charsets.UTF_8));

            });

            Helper.transact(em -> {

                QuotationRequest r = em.find(QuotationRequest.class, 1l);

                r.createProforma(em, new File("/home/miguel/Descargas/testGrupo.pdf"));
                //r.confirm();

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testFileProforma() {

        try {
            Helper.transact(em -> {

                AppConfig.get(em).setXslfoForIssuedInvoice(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/factura.xsl"), Charsets.UTF_8));

            });

            Helper.transact(em -> {

                io.mateu.erp.model.booking.File r = em.find(io.mateu.erp.model.booking.File.class, 1l);

                r.buildProforma(em, new File("/home/miguel/Descargas/testProforma.pdf"));
                //r.confirm();

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testxx() {

        try {
            Helper.transact(em -> {

                TransferBooking b = em.find(TransferBooking.class, 12741l);
                b.setSpecialRequests(b.getSpecialRequests() + "x");

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testCircuitRates(String token) {
        try {
            System.out.println(Helper.toJson(new CircuitBookingServiceImpl().getCircuitRates(token, "cir-153", 20190501, "es")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testCircuitAvail(String token) {
        try {
            System.out.println(Helper.toJson(new CircuitBookingServiceImpl().getAvailableCircuits(token, "es")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testExcursionRates(String token) {
        try {
            System.out.println(Helper.toJson(new ActivityBookingServiceImpl().getActivityRates(token, "exc-155", 20190501, "es")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testExcursionAvail(String token) {
        try {
            System.out.println(Helper.toJson(new ActivityBookingServiceImpl().getAvailableActivities(token, 20190628, "cou-ES", null)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testTourAvail(String token) {
    }

    private static void testGenericConfirm(String token) {
        try {
            BookGenericRQ rq = new BookGenericRQ();
            rq.setKey("ewogICJwcm9kdWN0IiA6ICIxIiwKICAiY2hpbGRyZW4iIDogMCwKICAiYWR1bHRzIiA6IDAsCiAgInZhcmlhbnQiIDogbnVsbCwKICAic3RhcnQiIDogMjAxOTA2MDEsCiAgImVuZCIgOiAyMDE5MDYwNywKICAibGFuZ3VhZ2UiIDogImVzIiwKICAidW5pdHMiIDogMSwKICAidG9rZW4iIDogImV5QWlZM0psWVhSbFpDSTZJQ0pYWldRZ1JtVmlJREl3SURFMk9qSTNPakUxSUVORlZDQXlNREU1SWl3Z0luVnpaWEpKWkNJNklDSjNaV0lpTENBaWNHRnlkRzVsY2tsa0lqb2dJakVpZlE9PSIKfQ==");
            rq.setBookingReference("Test " + LocalDateTime.now());
            rq.setCommentsToProvider("Test");
            rq.setEmail("miguelperezcolom@gmail.com");
            rq.setLeadName("Mr test");

            System.out.println(Helper.toJson(new GenericBookingServiceImpl().bookGeneric(token, rq)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testGenericPriceDetail(String token) {
        try {
            System.out.println(Helper.toJson(new GenericBookingServiceImpl().getGenericPriceDetails(token, "ewogICJwcm9kdWN0IiA6ICIzIiwKICAiY2hpbGRyZW4iIDogMCwKICAiYWR1bHRzIiA6IDAsCiAgInZhcmlhbnQiIDogIjIiLAogICJzdGFydCIgOiAyMDE5MDYwMSwKICAiZW5kIiA6IDIwMTkwNjA3LAogICJsYW5ndWFnZSIgOiAiZXMiLAogICJ1bml0cyIgOiAxLAogICJ0b2tlbiIgOiAiZXlBaVkzSmxZWFJsWkNJNklDSk5iMjRnUm1WaUlERTRJREUxT2pJd09qUXhJRU5GVkNBeU1ERTVJaXdnSW5WelpYSkpaQ0k2SUNKaFpHMXBiaUlzSUNKd1lYSjBibVZ5U1dRaU9pQWlNeUo5Igp9", "es", null, null)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testGenericCheck(String token) {
        try {
            System.out.println(Helper.toJson(new GenericBookingServiceImpl().check(token, "gen-1", 0, 0, 1, 20190601, 20190607, "es", null)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testGenericRates(String token) {
        try {
            System.out.println(Helper.toJson(new GenericBookingServiceImpl().getGenericRates(token, "gen-3", "es")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testGenericAvail(String token) {
        try {
            System.out.println(Helper.toJson(new GenericBookingServiceImpl().getAvailableGenerics(token, "cou-ES", "es")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testTransferConfirm(String token) {
        String key = "ewogICJmcm9tVHJhbnNmZXJQb2ludElkIiA6ICJ0cC0yNzEiLAogICJiaWtlcyIgOiAwLAogICJwYXgiIDogMSwKICAidmFsb3IiIDogMTY1Ljg2LAogICJ0b2tlbiIgOiAiZXlBaVkzSmxZWFJsWkNJNklDSlVkV1VnVFdGNUlESXhJREV4T2pNd09qSXdJRU5GVTFRZ01qQXhPU0lzSUNKMWMyVnlTV1FpT2lBaWQyVmlJaXdnSW1GblpXNWplVWxrSWpvZ0lqVXpJbjA9IiwKICAid2hlZWxDaGFpcnMiIDogMCwKICAidG9UcmFuc2ZlclBvaW50SWQiIDogInRwLTc4MSIsCiAgIm91dGdvaW5nRGF0ZSIgOiAyMDE5MDYxNywKICAiaW5jb21pbmdEYXRlIiA6IDIwMTkwNjEzLAogICJhZ2VzIiA6IFsgXSwKICAiZ29sZkJhZ2dhZ2VzIiA6IDAsCiAgInByaWNlSWQiIDogNDg0LAogICJiaWdMdWdnYWdlcyIgOiAwCn0=";
        try {
            BookTransferRQ rq = new BookTransferRQ();
            rq.setBookingReference("Test " + LocalDateTime.now());
            rq.setCommentsToProvider("Test");
            rq.setEmail("miguelperezcolom@gmail.com");
            rq.setLeadName("Mr test");
            rq.setPrivateComments("Test");
            rq.setKey(key);
            rq.setIncomingFlightNumber("IB1234");
            rq.setIncomingFlightOrigin("MAD");
            rq.setIncomingFlightTime(1650);

            rq.setOutgoingFlightNumber("AE4587");
            rq.setOutgoingFlightDestination("CDG");
            rq.setOutgoingFlightTime(2030);

            System.out.println(Helper.toJson(new TransferBookingServiceImpl().bookTransfer(token, rq)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testTransferDetails(String token) {
        String key = "ewogICJmcm9tVHJhbnNmZXJQb2ludElkIiA6ICJ0cC0yNzEiLAogICJiaWtlcyIgOiAwLAogICJwYXgiIDogMSwKICAidmFsb3IiIDogMTY1Ljg2LAogICJ0b2tlbiIgOiAiZXlBaVkzSmxZWFJsWkNJNklDSlVkV1VnVFdGNUlESXhJREV4T2pNd09qSXdJRU5GVTFRZ01qQXhPU0lzSUNKMWMyVnlTV1FpT2lBaWQyVmlJaXdnSW1GblpXNWplVWxrSWpvZ0lqVXpJbjA9IiwKICAid2hlZWxDaGFpcnMiIDogMCwKICAidG9UcmFuc2ZlclBvaW50SWQiIDogInRwLTc4MSIsCiAgIm91dGdvaW5nRGF0ZSIgOiAyMDE5MDYxNywKICAiaW5jb21pbmdEYXRlIiA6IDIwMTkwNjEzLAogICJhZ2VzIiA6IFsgXSwKICAiZ29sZkJhZ2dhZ2VzIiA6IDAsCiAgInByaWNlSWQiIDogNDg0LAogICJiaWdMdWdnYWdlcyIgOiAwCn0=";
        try {
            System.out.println(Helper.toJson(new TransferBookingServiceImpl().getTransferPriceDetails(token, key, "es", "")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testAvailTransfers(String token) {
        try {
            System.out.println(Helper.toJson(new TransferBookingServiceImpl().getAvailabeTransfers(token, "tp-271", "tp-781", 1, 0, 0, 0, 0, 0, 20190613, 20190617)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testHotelConfirm(String token) {
        try {
            BookHotelRQ rq = new BookHotelRQ();
            rq.setBookingReference("Test " + LocalDateTime.now());
            rq.setCommentsToProvider("Test");
            rq.setEmail("miguelperezcolom@gmail.com");
            rq.setLeadName("Mr test");
            rq.setPrivateComments("Test");
            BookingKey k;
            rq.getRateKeys().add(k = new BookingKey());
            k.setRateKey("NC0xMC0yMDE5MDYwMS0yMDE5MDYwOC00Mi0xMS00MC0xMS0xLTItMC0=");

            // k.setOccupancy(); //todo: sobra
            //k.setRequestPaymentData(); //todo: sobra
            //k.setRoomId(); //todo: sobra
            //k.setRoomName(); //todo: sobra
            //rq.setServices(); //todo: esto sobra?

            System.out.println(Helper.toJson(new HotelBookingServiceImpl().bookHotel(token, rq)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    private static void testHotelDetails(String token) {
        try {
            GetHotelPriceDetailsRQ rq = new GetHotelPriceDetailsRQ();
            rq.setLanguage("es");
            rq.setRatekeys("NC0xMC0yMDE5MDYwMS0yMDE5MDYwOC00Mi0xMS00MC0xMS0xLTItMC0=");
            rq.setCoupon("");
            System.out.println(Helper.toJson(new HotelBookingServiceImpl().getHotelPriceDetails(token, rq)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testHotelRates(String token) {
        try {
            GetHotelRatesRQ rq = new GetHotelRatesRQ();
            rq.setCheckin(20190601);
            rq.setCheckout(20190608);
            rq.setHotelId("hot-6");
            rq.setLanguage("es");
            rq.setOccupancies("1x2");
            System.out.println(Helper.toJson(new HotelBookingServiceImpl().getRates(token, rq)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testPortfolio(String token) {

        try {
            System.out.println(Helper.toJson(new CommonsServiceImpl().getPortfolio(token)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testAvailHotels(String token) {

        try {
            System.out.println(Helper.toJson(new HotelBookingServiceImpl().getAvailableHotels(token, "es", "des-1", 20190513, 20190517, "1x2", true)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

}
