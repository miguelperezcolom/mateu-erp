package io.mateu.erp.model.product.hotel.contracting;

import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.HotelContractPhoto;
import io.mateu.erp.model.product.hotel.Inventory;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.CalendarLimiter;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.persistence.*;
import javax.persistence.Table;
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

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class HotelContract extends AbstractContract implements IHotelContract, CalendarLimiter {

    @Tab("Others")

    @SearchFilter
    @ManyToOne
    @NotNull
    private Hotel hotel;

    @ManyToOne
    @NotNull
    private Inventory inventory;

    @ManyToOne
    private HotelContract parent;

    @SearchFilter
    @ManyToMany(mappedBy = "contracts")
    private List<AbstractHotelOffer> offers = new ArrayList<>();

    @Tab("Warranty")
    private boolean warranty;

    @NotNull
    private WarrantySettlementBasis warrantySettlementBasis = WarrantySettlementBasis.NONE;

    private double warrantyPercent;

    private double extrasIncludedInWarranty;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotelContract")
    private List<Charge> warrantySettlements = new ArrayList<>();


    @Tab("Terms")
    @DoNotIncludeSeparator
    @Column(name = "terms_hotel")
    @Convert(converter = HotelContractPhotoConverter.class)
    @FullWidth
    private HotelContractPhoto terms;


    @Action()
    public URL pdf() throws Throwable {
        //String xslfo = "contract.xsl";

        URL[] url = new URL[1];


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                long t0 = new Date().getTime();


                try {


                    Document xml = toXml(em);

                    try {
                        String archivo = UUID.randomUUID().toString();

                        File temp = (System.getProperty("tmpdir") == null)?File.createTempFile(archivo, ".pdf"):new File(new File(System.getProperty("tmpdir")), archivo + ".pdf");


                        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
                        System.out.println("Temp file : " + temp.getAbsolutePath());

                        FileOutputStream fileOut = new FileOutputStream(temp);
                        //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
                        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
                        System.out.println("xml=" + sxml);
                        //fileOut.write(BaseServerSideApp.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForHotelContract())), new StreamSource(new StringReader(sxml))));
                        fileOut.write(Helper.fop(new StreamSource(Hotel.class.getResourceAsStream("contract.xsl")), new StreamSource(new StringReader(sxml))));
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


    private Document toXml(EntityManager em) {
        Element xml = new Element("contract");

        xml.setAttribute("id", "" + getId());
        if (getTitle() != null) xml.setAttribute("title", getTitle());
        xml.setAttribute("type", "" + getType());
        if (getBillingConcept() != null && getBillingConcept().getName() != null) xml.setAttribute("billingConcept", getBillingConcept().getName());
        if (getValidFrom() != null) xml.setAttribute("validFrom", getValidFrom().toString());
        if (getValidTo() != null) xml.setAttribute("validTo", getValidTo().toString());
        if (getBookingWindowFrom() != null) xml.setAttribute("bookingWindowFrom", getBookingWindowFrom().toString());
        if (getBookingWindowTo() != null) xml.setAttribute("bookingWindowTo", getBookingWindowTo().toString());
        if (getSpecialTerms() != null) xml.setAttribute("specialTerms", getSpecialTerms());
        xml.setAttribute("vat", (isVATIncluded())?"Included":"Not included");
        if (getAudit() != null) xml.setAttribute("audit", getAudit().toString());

        AppConfig c = AppConfig.get(em);
        if (c.getBusinessName() != null) xml.setAttribute("bussinessName", c.getBusinessName());
        if (c.getLogo() != null) {
            try {
                xml.setAttribute("logo", "" + c.getLogo().toFileLocator().getTmpPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getCurrency() != null) xml.setAttribute("currencyCode", getCurrency().getIsoCode());

        if (getOffice() != null) {
            Element o;
            xml.addContent(o = new Element("office"));
            if (getOffice().getName() != null) o.setAttribute("name", getOffice().getName());
        }


        if (getSupplier() != null) xml.addContent(getSupplier().toXml().setName("supplier"));

        if (getHotel() != null) {
            Element h;
            xml.addContent(h = new Element("hotel"));
            if (getHotel().getCategoryName() != null) h.setAttribute("category", getHotel().getCategoryName());
            if (getHotel().getName() != null) h.setAttribute("name", getHotel().getName());
            if (getSupplier() != null && getSupplier().getName() != null) h.setAttribute("bussinessName", getSupplier().getBusinessName());
            if (getSupplier() != null && getSupplier().getVatIdentificationNumber() != null) h.setAttribute("vatid", getSupplier().getVatIdentificationNumber());
            if (getHotel().getAddress() != null) h.setAttribute("address", getHotel().getAddress());
            if (getHotel().getTelephone() != null) h.setAttribute("tel", getHotel().getTelephone());
            if (getHotel().getFax() != null) h.setAttribute("fax", getHotel().getFax());

            h.setAttribute("childStartAge", "" + getHotel().getChildStartAge());
            h.setAttribute("juniorStartAge", "" + getHotel().getJuniorStartAge());
            h.setAttribute("adultStartAge", "" + getHotel().getAdultStartAge());
            if (getTerms() != null && getTerms().isYoungestFirst()) h.setAttribute("youngestFirst", "");

        }


        if (getSignedAt() != null) xml.setAttribute("signedAt", getSignedAt());
        if (getPartnerSignatory() != null) xml.setAttribute("partnerSignatory", getSignedAt());
        if (getOwnSignatory() != null) xml.setAttribute("ownSignatory", getSignedAt());
        if (getSignatureDate() != null) xml.setAttribute("signatureDate", getSignatureDate().format(DateTimeFormatter.BASIC_ISO_DATE));


        Element ts;
        xml.addContent(ts = new Element("targets"));
        for (Partner t : getPartners()) ts.addContent(t.toXml().setName("target"));

        if (ContractType.SALE.equals(getType())) {
            if (getPartners().size() > 0) {
                Partner a = getPartners().get(0);
                if (a.getName() != null && a.getBusinessName() != null) xml.addContent(new Element("contractor").setAttribute("name", a.getName()).setAttribute("bussinessName", a.getBusinessName()));
            }
            if (getOffice() != null && getOffice().getName() != null && c.getBusinessName() != null) xml.addContent(new Element("hired").setAttribute("name", getOffice().getName()).setAttribute("bussinessName", c.getBusinessName()));
        } else {
            if (getOffice() != null && getOffice().getName() != null && c.getBusinessName() != null) xml.addContent(new Element("contractor").setAttribute("name", getOffice().getName()).setAttribute("bussinessName", c.getBusinessName()));
            if (getSupplier() != null && getSupplier().getName() != null && getSupplier().getBusinessName() != null) xml.addContent(new Element("hired").setAttribute("name", getSupplier().getName()).setAttribute("bussinessName", getSupplier().getBusinessName()));
        }


        if (getTerms() != null) xml.addContent(getTerms().getXmlForPdf(em, new HashMap<>(), new HashMap<>()));


        return new Document(xml);
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
}
