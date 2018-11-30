package io.mateu.erp.model.product.hotel.contracting;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.invoicing.WarrantySettlementCharge;
import io.mateu.erp.model.payments.DueDate;
import io.mateu.erp.model.payments.DueDateType;
import io.mateu.erp.model.payments.HotelContractDueDate;
import io.mateu.erp.model.product.hotel.HotelSalesForecast;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;
import io.mateu.mdd.core.interfaces.CalendarLimiter;
import io.mateu.mdd.core.util.DatesRange;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.joda.time.Days;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class HotelContract extends AbstractContract implements IHotelContract, CalendarLimiter {

    @KPI
    private double salePercent;


    @MainSearchFilter
    @ManyToOne
    @NotNull@Position(3)
    @ListColumn
    @NoChart
    private Hotel hotel;

    @ManyToOne
    @NotNull@Position(4)
    private Inventory inventory;


    @DependsOn("hotel")
    public DataProvider getInventoryDataProvider() throws Throwable {
        return new JPQLListDataProvider("select x from " + Inventory.class.getName() + " x " + ((getHotel() != null)?" where x.hotel.id = " + getHotel().getId():""));
    }

    @ManyToOne
    @Position(5)
    private Inventory securityInventory;

    @DependsOn("hotel")
    public DataProvider getSecurityInventoryDataProvider() throws Throwable {
        return new JPQLListDataProvider("select x from " + Inventory.class.getName() + " x " + ((getHotel() != null)?" where x.hotel.id = " + getHotel().getId():""));
    }



    @ManyToOne@Position(6)
    private HotelContract parent;


    @Position(7)
    private double incrementPercent;


    @Position(8)
    private boolean pdfInEnglish;


    @Tab("Due dates")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotelContract")
    @FullWidth
    @FieldsFilter("date,amount,paid")
    private List<HotelContractDueDate> dueDates = new ArrayList<>();

    public HotelContractDueDate createDueDatesInstance() {

        HotelContractDueDate dd = new HotelContractDueDate();
        dd.setHotelContract(this);
        dd.setType(ContractType.PURCHASE.equals(getType())?DueDateType.PAYMENT:DueDateType.COLLECTION);
        dd.setCurrency(getCurrency());

        return dd;
    }

    @Tab("Forecast")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotelContract")
    @FullWidth
    private List<HotelSalesForecast> forecast = new ArrayList<>();

    @Tab("Offers")
    @SearchFilter
    @ManyToMany(mappedBy = "contracts")
    @UseLinkToListView
    private List<AbstractHotelOffer> offers = new ArrayList<>();

    @Tab("Warranty")
    private boolean warranty;

    @NotNull
    private WarrantySettlementBasis warrantySettlementBasis = WarrantySettlementBasis.NONE;

    private boolean productionWarranty;

    private double warrantyPercent;

    private boolean extrasIncludedInWarranty;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotelContract")
    @UseLinkToListView
    @Ignored
    private List<WarrantySettlementCharge> warrantySettlements = new ArrayList<>();


    @Tab("Terms")
    /**
     * groups related
     */
    private int maxPaxPerBooking;

    /**
     * groups related
     */
    private int maxRoomsPerBooking;

    /**
     * for warranties
     */
    private boolean zeroPricesAllowed;

    @DoNotIncludeSeparator
    @Column(name = "terms_hotel")
    @Convert(converter = HotelContractPhotoConverter.class)
    @FullWidth
    private HotelContractPhoto terms;

    public HotelContractPhoto getTerms() {
        if (terms != null && terms.getContract() == null) terms.setContract(this);
        return terms;
    }




    @Override
    public Document toXml(EntityManager em) {
        Document doc = super.toXml(em);

        Element xml = doc.getRootElement();

        if (isPdfInEnglish()) xml.setAttribute("lan", "en");

        Element duedates;
        xml.addContent(duedates = new Element("dueDates"));
        getDueDates().forEach(o -> duedates.addContent(new Element("dueDate").setAttribute("date", o.getDate().format(DateTimeFormatter.ISO_DATE)).setAttribute("currency", o.getCurrency().getIsoCode()).setAttribute("amount", Helper.formatEuros(o.getAmount()))));
        double totalVencimientos = 0;
        for (DueDate dd : dueDates) totalVencimientos += dd.getAmount();
        duedates.setAttribute("total", Helper.formatEuros(totalVencimientos));

        if (getHotel() != null) {
            Element h;
            xml.addContent(h = new Element("hotel"));
            if (getHotel().getCategoryName() != null) h.setAttribute("category", getHotel().getCategoryName());
            if (getHotel().getName() != null) h.setAttribute("name", getHotel().getName());
            if (getSupplier() != null && getSupplier().getFinancialAgent() != null && getSupplier().getFinancialAgent().getName() != null) h.setAttribute("bussinessName", getSupplier().getFinancialAgent().getBusinessName());
            if (getSupplier() != null && getSupplier().getFinancialAgent() != null && getSupplier().getFinancialAgent().getVatIdentificationNumber() != null) h.setAttribute("vatid", getSupplier().getFinancialAgent().getVatIdentificationNumber());
            if (getHotel().getAddress() != null) h.setAttribute("address", getHotel().getAddress());
            if (getHotel().getTelephone() != null) h.setAttribute("tel", getHotel().getTelephone());
            if (getHotel().getFax() != null) h.setAttribute("fax", getHotel().getFax());
            if (getSupplier() != null && getSupplier().getEmail() != null) h.setAttribute("email", getSupplier().getEmail());
            else if (getHotel().getEmail() != null) h.setAttribute("email", getHotel().getEmail());

            h.setAttribute("childStartAge", "" + getHotel().getChildStartAge());
            if (getHotel().getJuniorStartAge() != 0) h.setAttribute("juniorStartAge", "" + getHotel().getJuniorStartAge());
            h.setAttribute("adultStartAge", "" + getHotel().getAdultStartAge());
            if (getTerms() != null && getHotel().isYoungestFirst()) h.setAttribute("youngestFirst", "");

            Element rooms;
            xml.addContent(rooms = new Element("rooms"));
            getHotel().getRooms().stream().sorted((r1, r2) -> r1.getCode().compareTo(r2.getCode())).forEach(o -> rooms.addContent(new Element("room").setAttribute("code", "" + o.getCode()).setAttribute("name", "" + o.getName())));

            Element boards;
            xml.addContent(boards = new Element("boards"));
            getHotel().getBoards().stream().sorted((r1, r2) -> r1.getCode().compareTo(r2.getCode())).forEach(o -> boards.addContent(new Element("board").setAttribute("code", "" + o.getCode()).setAttribute("name", "" + o.getName())));

        }


        if (warranty) {

            Element w = null;
            xml.addContent(w = new Element("warranty"));

            w.setAttribute("settlementBasis", "" + warrantySettlementBasis);
            if (productionWarranty) w.setAttribute("productionWarranty", "" + productionWarranty);
            w.setAttribute("warrantyPercent", "" + warrantyPercent);
            if (extrasIncludedInWarranty) w.setAttribute("extrasIncludedInWarranty", "" + extrasIncludedInWarranty);
        }

        if (getTerms() != null) xml.addContent(getTerms().getXmlForPdf(em, getRoomData(), getBoardData()));
        else if (getParent() != null && getParent().getTerms() != null) {
            try {
                xml.addContent(new HotelContractPhoto(getTerms().serialize()).increment(getIncrementPercent()).getXmlForPdf(em, new HashMap<>(), new HashMap<>()));
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<AbstractHotelOffer> offs = new ArrayList<>();
        for (AbstractHotelOffer o : offers) if (!offs.contains(o) && o.isActive() && o.isIncludedInContractPdf()) offs.add(o);
        Element eoffs;
        xml.addContent(eoffs = new Element("offers"));
        for (AbstractHotelOffer o : offs) {
            eoffs.addContent(o.toXml());
        }


        return doc;
    }

    private Map<String,Map<String,String>> getRoomData() {
        Map<String,Map<String,String>> d = new HashMap<>();

        for (Room r : hotel.getRooms()) {
            Map<String, String> rd;
            d.put(r.getCode(), rd = new HashMap<>());
            rd.put("capacity", "Min. pax = " + r.getMinPax() + (r.getMaxCapacities() != null?", max pax = " + r.getMaxCapacities().toString():"") + ",child discount need min. " + r.getMinAdultsForChildDiscount() + " adults.");
        }

        return d;
    }

    private Map<String,Map<String,String>> getBoardData() {
        return new HashMap<>();
    }

    @Override
    public String getXslfo(EntityManager em) {
        return AppConfig.get(em).getXslfoForHotelContract();
    }



    public static void main(String... args) throws Throwable {

        System.setProperty("appconf", "/home/miguel/quonext/quoon.properties");

        Helper.loadProperties();


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {


                HotelContract c = em.find(HotelContract.class, 24l);


                if( Desktop.isDesktopSupported() )
                {
                    new Thread(() -> {
                        try {
                            Desktop.getDesktop().browse( c.pdf().toURI() );
                        } catch (IOException | URISyntaxException e1) {
                            e1.printStackTrace();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }).start();
                }


            }
        });


    }

    @Override
    public LocalDate getBegining() {
        return getValidFrom();
    }

    @Override
    public LocalDate getEnding() {
        return getValidTo();
    }


    @Action(icon = VaadinIcons.COPY, order = 3)
    public static void duplicateSelected(EntityManager em, Set<HotelContract> seleccion, double incrementPercent) throws JDOMException, IOException {
        for (HotelContract x : seleccion) {
            HotelContract c = new HotelContract();
            c.setHotel(x.getHotel());
            x.getHotel().getContracts().add(c);
            c.setInventory(x.getInventory());
            c.setTitle("COPY OF " + x.getTitle());
            c.setSpecialTerms(x.getSpecialTerms());
            c.setPrivateComments(x.getPrivateComments());
            c.setType(x.getType());
            c.setValidFrom(x.getValidFrom());
            c.setValidTo(x.getValidTo());
            c.setSupplier(x.getSupplier());
            c.setProductLine(x.getProductLine());
            c.setBillingConcept(x.getBillingConcept());
            c.setOffice(x.getOffice());
            c.setCurrency(x.getCurrency());
            if (x.getTerms() != null) c.setTerms(new HotelContractPhoto(x.getTerms().serialize()).increment(incrementPercent));
            em.persist(c);
        }
    }

    @Action(icon = VaadinIcons.COPY, order = 4)
    public static void createSaleForSelected(EntityManager em, Set<HotelContract> seleccion, double incrementPercent) throws JDOMException, IOException {
        for (HotelContract x : seleccion) if (ContractType.PURCHASE.equals(x.getType())) {
            HotelContract c = new HotelContract();
            c.setHotel(x.getHotel());
            x.getHotel().getContracts().add(c);
            c.setInventory(x.getInventory());
            c.setParent(x);
            c.setTitle("SALE OF " + x.getTitle());
            c.setSpecialTerms(x.getSpecialTerms());
            c.setPrivateComments(x.getPrivateComments());
            c.setType(ContractType.SALE);
            c.setValidFrom(x.getValidFrom());
            c.setValidTo(x.getValidTo());
            c.setProductLine(x.getProductLine());
            c.setBillingConcept(x.getBillingConcept());
            c.setOffice(x.getOffice());
            c.setCurrency(x.getCurrency());
            if (x.getTerms() != null) c.setTerms(new HotelContractPhoto(x.getTerms().serialize()).increment(incrementPercent));
            em.persist(c);
        }
    }


    @Action(order = 5)
    public URL pdfWithoutPrices() throws Throwable {
        //String xslfo = "contract.xsl";

        URL[] url = new URL[1];


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                long t0 = new Date().getTime();


                try {


                    Document xml = toXml(em);

                    xml.getRootElement().setAttribute("noPriceDetail", "true");

                    try {
                        String archivo = UUID.randomUUID().toString();

                        File temp = (System.getProperty("tmpdir") == null)?File.createTempFile(archivo, ".pdf"):new File(new File(System.getProperty("tmpdir")), archivo + ".pdf");


                        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
                        System.out.println("Temp file : " + temp.getAbsolutePath());

                        FileOutputStream fileOut = new FileOutputStream(temp);
                        //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
                        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
                        System.out.println("xml=" + sxml);
                        fileOut.write(Helper.fop(new StreamSource(new StringReader(getXslfo(em))), new StreamSource(new StringReader(sxml))));
                        fileOut.close();

                        String baseUrl = System.getProperty("tmpurl");
                        if (baseUrl == null) {
                            url[0] = temp.toURI().toURL();
                        } else url[0] = new URL(baseUrl + "/" + temp.getName());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } catch (Exception e1) {
                    e1.printStackTrace();
                }


            }
        });


        return url[0];
    }


    @PostUpdate@PostPersist@PostRemove
    public void post() {
        WorkflowEngine.add(new Runnable() {
            @Override
            public void run() {
                try {
                    Helper.transact(em -> {

                        HotelContract h = em.find(HotelContract.class, getId());

                        if (h.getInventory() != null) h.getInventory().build(em);
                        if (h.getSecurityInventory() != null) h.getSecurityInventory().build(em);

                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof  HotelContract && getId() == ((HotelContract)obj).getId());
    }

    @PostLoad
    public void postLoad() {
        dueDates.sort((c1, c2) -> c1.getDate().compareTo(c2.getDate()));
        forecast.sort((c1, c2) -> c1.getStart().compareTo(c2.getStart()));
    }

    @PrePersist@PreUpdate
    public void pre() throws Error {
        super.pre();

        setAveragePrice(calculateAveragePrice());
    }

    private double calculateAveragePrice() {

        double total = 0;
        double units = 0;

        if (getTerms() != null) for (LinearFare f : getTerms().getFares()) for (LinearFareLine l : f.getLines()) {
            for (DatesRange r : f.getDates()) {
                LocalDate desde = getValidFrom();
                LocalDate hasta = getValidTo();
                if (r.getStart() != null) desde = r.getStart();
                if (r.getEnd() != null) hasta = r.getEnd();
                total += DAYS.between(desde, hasta) * (l.getLodgingPrice() + 2 * l.getAdultPrice());
                units += DAYS.between(desde, hasta);
            }
        }

        /*
        if (getTerms() != null) for (Allotment a : getTerms().getAllotment()) {
            LocalDate desde = getValidFrom();
            LocalDate hasta = getValidTo();
            if (a.getStart() != null) desde = a.getStart();
            if (a.getEnd() != null) hasta = a.getEnd();
            units += DAYS.between(desde, hasta) * a.getQuantity();
        }
        */

        return Helper.roundEuros(units != 0?total / units:0);
    }


}
