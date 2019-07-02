package io.mateu.erp.model.booking;

import io.mateu.erp.model.booking.parts.ExcursionBooking;
import io.mateu.erp.model.booking.parts.TourBooking;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSaleSettlementFormLine;
import io.mateu.erp.model.partners.CommissionAgent;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.Tour;
import io.mateu.erp.model.product.tour.ExcursionShift;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.config.Template;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
@Getter@Setter
public class ManagedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    @Output
    @SearchFilter
    private Office office;


    @ManyToOne
    @NotNull
    @Output
    @SearchFilter
    private Tour tour;


    @Output
    @SearchFilter
    @Order
    private LocalDate date;


    @ManyToOne
    @Output
    @SearchFilter
    private ExcursionShift shift;

    public boolean isShiftVisible() {
        return tour != null && tour instanceof Excursion;
    }

    private boolean active = true;

    @KPI
    private boolean deriveToThePool;


    private int maxUnits;

    @Output
    private int unitsBooked;

    @Output
    private int unitsLeft;



    @OneToMany(mappedBy = "managedEvent")
    @UseLinkToListView
    private List<TourBooking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "managedEvent")
    @UseLinkToListView
    private List<Service> services = new ArrayList<>();

    @TextArea
    private String privateComments;

    @KPI@Money
    private double totalSale;

    @KPI@Money
    private double totalCost;

    @KPI@Money@Balance
    private double totalMarkup;


    @Ignored
    private boolean updatePending;

    @PostPersist@PostUpdate
    public void post() {
        if (updatePending) {

            WorkflowEngine.add(() -> {
                try {
                    Helper.transact(em -> {

                        ManagedEvent e = em.find(ManagedEvent.class, getId());

                        double sale = 0;
                        double cost = 0;
                        int bkd = 0;
                        for (TourBooking b : e.getBookings()) {
                            if (b.isActive()) bkd += b.getPax();
                            sale += b.getTotalValue();
                            cost += b.getTotalCost();
                        }
                        e.setUnitsBooked(bkd);
                        e.setUnitsLeft(e.getMaxUnits() - e.getUnitsBooked());
                        e.setTotalSale(Helper.roundEuros(sale));
                        e.setTotalCost(Helper.roundEuros(cost));
                        e.setTotalMarkup(Helper.roundEuros(sale - cost));

                        e.setUpdatePending(false);

                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });

        }
    }



    @Action(order = 1)
    public URL schedule() {
        return null;
    }


    @Action(order = 2)
    public URL manifest() throws Throwable {
        String archivo = UUID.randomUUID().toString();

        File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(archivo, ".pdf"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), archivo + ".pdf");


        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
        System.out.println("Temp file : " + temp.getAbsolutePath());

        Helper.transact(em -> {
            crearPdf(em, temp);
        });

        String baseUrl = System.getProperty("tmpurl");
        URL url;
        if (baseUrl == null) {
            url = temp.toURI().toURL();
        } else url = new URL(baseUrl + "/" + temp.getName());


        return url;
    }


    public void crearPdf(EntityManager em, File file) throws IOException, SAXException {
        Document xml = new Document(toXml());


        System.out.println(Helper.toString(xml.getRootElement()));

        FileOutputStream fileOut = new FileOutputStream(file);
        //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
        System.out.println("xml=" + sxml);
        fileOut.write(Helper.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForEventManifest())), new StreamSource(new StringReader(sxml))));
        fileOut.close();

    }

    public void crearReport(EntityManager em, File file) throws IOException, SAXException {
        Document xml = new Document(toXml());


        System.out.println(Helper.toString(xml.getRootElement()));

        FileOutputStream fileOut = new FileOutputStream(file);
        //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
        System.out.println("xml=" + sxml);
        fileOut.write(Helper.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForEventReport())), new StreamSource(new StringReader(sxml))));
        fileOut.close();

    }

    private Element toXml() {
        Element xml = new Element("event");

        DecimalFormat pf = new DecimalFormat("#####0.00");
        DecimalFormat nf = new DecimalFormat("##,###,###,###,##0.00");


        if (date != null) xml.setAttribute("eventDate", date.format(DateTimeFormatter.ISO_DATE));

        xml.setAttribute("date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        if (MDD.getCurrentUser() != null) xml.setAttribute("user", MDD.getCurrentUser().getLogin());

        xml.setAttribute("bookings", "" + bookings.size());
        /*
        xml.setAttribute("totalValue", nf.format(totalValue));
        xml.setAttribute("totalCash", nf.format(totalCash));
        xml.setAttribute("totalCommission", nf.format(totalCommission));
        xml.setAttribute("totalToPay", nf.format(Helper.roundEuros(totalCash - totalCommission)));
        */


        Element els;
        xml.addContent(els = new Element("bookings"));

        int pax = 0;

        double ts = 0;
        double tc = 0;

        for (Booking b : bookings) {
            Element el;
            els.addContent(el = new Element("booking"));

            ExcursionBooking eb = (ExcursionBooking) b;

            el.setAttribute("id", "" + b.getId());
            if (b.getLeadName() != null) el.setAttribute("leadName", b.getLeadName());
            el.setAttribute("pax", "" + b.getPax());
            String s = "";
            s += eb.getTelephone();
            el.setAttribute("data", s);
            if (b.getSpecialRequests() != null) el.setAttribute("comments", b.getSpecialRequests());

            pax += b.getPax();

            ts += b.getTotalValue();
            tc += b.getTotalCost();
        }


        xml.setAttribute("pax", "" + pax);

        xml.setAttribute("totalSale", nf.format(Helper.roundEuros(ts)));
        xml.setAttribute("totalCost", nf.format(Helper.roundEuros(tc)));
        xml.setAttribute("totalMarkup", nf.format(Helper.roundEuros(ts - tc)));

        return xml;
    }

    /*

    @Action(order = 3)
    public URL pickups() {
        return null;
    }


    @Action(order = 4)
    public URL delivers() {
        return null;
    }


    */

    @Action(order = 5)
    public URL report() throws Throwable {
        String archivo = UUID.randomUUID().toString();

        File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(archivo, ".pdf"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), archivo + ".pdf");


        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
        System.out.println("Temp file : " + temp.getAbsolutePath());

        Helper.transact(em -> {
            crearReport(em, temp);
        });

        String baseUrl = System.getProperty("tmpurl");
        URL url;
        if (baseUrl == null) {
            url = temp.toURI().toURL();
        } else url = new URL(baseUrl + "/" + temp.getName());


        return url;
    }

    @Action
    public static void close(EntityManager em, Set<ManagedEvent> sel) {
        sel.forEach(e -> {
            e.setActive(false);
        });
    }

    @Action
    public static void open(EntityManager em, Set<ManagedEvent> sel) {
        sel.forEach(e -> {
            e.setActive(true);
        });
    }


    @Action
    public static void sendEmail(Set<ManagedEvent> sel, @Help("If blank the postscript will be sent as the email body") Template template, String changeEmail, @Help("If blank, the subject from the templaet will be used") String subject, @TextArea String postscript) {

    }


    @Action
    public static void deriveToThePool(EntityManager em, Set<ManagedEvent> sel) {
        sel.forEach(e -> {
            if (e.getTour().getPool() != null) e.setDeriveToThePool(true);
        });
    }


    @Override
    public String toString() {
        return "Managed event " + id;
    }
}
