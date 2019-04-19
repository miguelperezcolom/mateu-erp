package io.mateu.erp.model.organization;

import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.BookingCommission;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import io.mateu.erp.model.partners.CommissionAgent;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.common.Resource;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.vaadinport.vaadin.MDDUI;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter@Setter
public class PointOfSaleSettlementForm {

    @Output
    private final PointOfSale pointOfSale;

    @NotNull
    private LocalDate start;

    @NotNull
    private LocalDate end;

    @Output
    private int bookings;

    @Output@SameLine
    private double totalCash;

    @Output@SameLine
    private double totalCommission;

    @UseLinkToListView(deleteEnabled = true)
    private List<PointOfSaleSettlementFormLine> lines = new ArrayList<>();

    public void setLines(List<PointOfSaleSettlementFormLine> lines) {
        this.lines = lines;
        sumar();
    }

    public void sumar() {
        bookings = 0;
        totalCash = 0;
        totalCommission = 0;

        for (PointOfSaleSettlementFormLine line : lines) {
            bookings++;
            totalCash = Helper.roundEuros(totalCash + line.getCash());
            totalCommission = Helper.roundEuros(totalCommission + line.getCommissions());
        }
    }

    public PointOfSaleSettlementForm(PointOfSale pointOfSale) {
        this.pointOfSale = pointOfSale;
        try {
            search();
        } catch (Throwable throwable) {
            MDD.alert(throwable);
        }
    }


    @Action(icon = VaadinIcons.SEARCH, order = 1)
    public void search() throws Throwable {

        lines = new ArrayList<>();

        String jpql = "select x from " + BookingCharge.class.getName() + " x where x.booking.pos.id = " + pointOfSale.getId() + " and x.pointOfSaleSettlement is null";

        Map<String, Object> params = new HashMap<>();

        if (start != null) {
            jpql += " and x.audit.created >= :s ";
            params.put("s", start);
        }
        if (end != null) {
            jpql += " and x.audit.created < :e ";
            params.put("e", end.plusDays(1));
        }

        jpql += " order by x.id asc";

        List<BookingCharge> charges = Helper.selectObjects(jpql, params);

        List<Booking> bookings = new ArrayList<>();

        for (BookingCharge charge : charges) {
            if (!bookings.contains(charge.getBooking())) bookings.add(charge.getBooking());
        }

        for (Booking booking : bookings) {
            PointOfSaleSettlementFormLine l;
            lines.add(l = new PointOfSaleSettlementFormLine());

            l.setBookingId(booking.getId());
            double t = 0;
            for (BookingCharge c : booking.getCharges()) if (c.getPointOfSaleSettlement() == null) {
                t += c.getValueInNucs();
            }
            l.setCash(Helper.roundEuros(t));
            l.setDate(booking.getAudit().getCreated().toLocalDate());
            l.setDescription(booking.getDescription());
            l.setLeadName(booking.getLeadName());
            l.setValue(booking.getTotalValue());
            l.setCommissions(booking.getTotalCommission());
        }

        sumar();
    }


    @Action(icon = VaadinIcons.SEARCH, order = 2)
    public URL pdf() throws Throwable {

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


    @Action(icon = VaadinIcons.BOLT, order = 3)
    public void settle() throws Throwable {

        PointOfSaleSettlement[] r = new PointOfSaleSettlement[1];

        Helper.transact(em -> {

            PointOfSaleSettlement s = new PointOfSaleSettlement();
            s.setPointOfSale(em.find(PointOfSale.class, pointOfSale.getId()));
            s.setCreatedBy(MDD.getCurrentUser());

            for (PointOfSaleSettlementFormLine l : lines) {
                Booking b = em.find(Booking.class, l.getBookingId());

                for (BookingCharge c : b.getCharges()) if (c.getPointOfSaleSettlement() == null) {
                    if (c.getPointOfSaleSettlement() != null) throw new Exception("Charge " + c.getId() + " has already been settled in settlement " + c.getPointOfSaleSettlement().getId());
                    s.getCharges().add(c);
                    c.setPointOfSaleSettlement(s);
                }

            }

            s.setTotalCash(totalCash);
            s.setTotalCommissions(totalCommission);

            s.getPointOfSale().setUpdatePending(true);
            em.persist(s);

            r[0] = s;

        });

        search();

        MDDUI.get().getNavegador().goTo("private/financial/financial/more/pointsofsale/possettlements/" + r[0].getId());
    }

    public void crearPdf(EntityManager em, File file) throws IOException, SAXException {
        Document xml = new Document(toXml());


        System.out.println(Helper.toString(xml.getRootElement()));

        FileOutputStream fileOut = new FileOutputStream(file);
        //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
        System.out.println("xml=" + sxml);
        fileOut.write(Helper.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForPOSSettlement())), new StreamSource(new StringReader(sxml))));
        fileOut.close();

    }

    private Element toXml() {
        Element xml = new Element("settlement");

        DecimalFormat pf = new DecimalFormat("#####0.00");
        DecimalFormat nf = new DecimalFormat("##,###,###,###,##0.00");


        if (start != null) xml.setAttribute("start", start.format(DateTimeFormatter.ISO_DATE));
        if (end != null) xml.setAttribute("end", end.format(DateTimeFormatter.ISO_DATE));

        xml.setAttribute("date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        if (MDD.getCurrentUser() != null) xml.setAttribute("user", MDD.getCurrentUser().getLogin());

        xml.setAttribute("pointOfSale", pointOfSale.getName());
        xml.setAttribute("bookings", "" + bookings);
        xml.setAttribute("totalCash", nf.format(totalCash));
        xml.setAttribute("totalCommission", nf.format(totalCommission));
        xml.setAttribute("totalToPay", nf.format(Helper.roundEuros(totalCash - totalCommission)));


        Element els;
        xml.addContent(els = new Element("lines"));

        for (PointOfSaleSettlementFormLine l : lines) {
            Element el;
            els.addContent(el = new Element("line"));

            el.setAttribute("bookingId", "" + l.getBookingId());
            if (l.getLeadName() != null) el.setAttribute("leadName", l.getLeadName());
            if (l.getDescription() != null) el.setAttribute("description", l.getDescription());
            el.setAttribute("cash", nf.format(l.getCash()));
            el.setAttribute("value", nf.format(l.getValue()));
            el.setAttribute("commissions", nf.format(l.getCommissions()));

        }

        try {
            Helper.notransact(em -> {

                Map<CommissionAgent, Double> coms = new HashMap<>();

                for (PointOfSaleSettlementFormLine l : lines) {

                    Booking b = em.find(Booking.class, l.getBookingId());

                    for (BookingCommission c : b.getCommissions()) {
                        if (c.getAgent() != null && c.getTotal() != 0 && c.getSettlement() == null) {

                            Double v = coms.get(c.getAgent());
                            if (v == null) v = new Double(0);
                            coms.put(c.getAgent(), v + c.getTotal());

                        }
                    }


                }

                Element ecs;
                xml.addContent(ecs = new Element("commissions"));

                for (CommissionAgent commissionAgent : coms.keySet()) {
                    Element el;
                    ecs.addContent(el = new Element("line"));

                    el.setAttribute("agent", commissionAgent.getName());
                    el.setAttribute("total", nf.format(coms.get(commissionAgent)));
                }

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        return xml;
    }

    @Override
    public String toString() {
        return "POS settlement";
    }
}
