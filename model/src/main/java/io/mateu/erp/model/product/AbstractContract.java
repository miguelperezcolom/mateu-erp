package io.mateu.erp.model.product;

import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.partners.PartnerGroup;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.organization.Company;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.CommissionTerms;
import io.mateu.erp.model.financials.PaymentTerms;
import io.mateu.erp.model.partners.Market;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.tour.Tour;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by miguel on 1/10/16.
 */
//@Entity
//@Table(name = "contract")
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Tab(value = "General", fullWith = true)
    @Embedded
    @Output
    private Audit audit;

    @FullWidth
    @NotNull
    @SearchFilter
    @ListColumn
    private String title;

    @NotNull
    @SearchFilter
    @ListColumn
    private ContractType type;

    @NotNull
    @ManyToOne
    private BillingConcept billingConcept;

    @ManyToOne
    @NotNull
    private ProductLine product;

    private boolean VATIncluded;

    @ManyToOne
    @NotNull
    private Currency currency;

    @NotNull
    @ListColumn
    private LocalDate validFrom;
    @NotNull
    @ListColumn
    @SameLine
    private LocalDate validTo;

    @ListColumn
    private LocalDate bookingWindowFrom;
    @ListColumn
    @SameLine
    private LocalDate bookingWindowTo;


    @TextArea
    private String specialTerms;

    @TextArea
    @SameLine
    private String privateComments;

    @Tab("Relations")
    @ManyToOne
    @SearchFilter
    @ListColumn
    private Partner supplier;

    @ManyToOne
    @NotInEditor
    @SearchFilter
    @ListColumn
    private Office office;


    @OneToMany
    private List<PartnerGroup> partnerGroups = new ArrayList<>();

    @OneToMany
    private List<Partner> partners = new ArrayList<>();

    @OneToMany
    private List<Market> markets = new ArrayList<>();

    @OneToMany
    private List<Company> companies = new ArrayList<>();

    @Tab("Tour")
    @OneToMany
    private List<Tour> tours = new ArrayList<>();

    @Tab("Signature")
    private String signedAt;

    private String signedBy;

    private String partnerSignatory;

    private String ownSignatory;

    private LocalDate signatureDate;

    @Tab("Commissions")
    @ManyToOne
    private CommissionTerms commissionTerms;

    @Tab("Payment")
    @ManyToOne
    private PaymentTerms paymentTerms;

    @Output
    private double averagePrice;

    @ManyToOne
    private CancellationRules cancellationRules;



    @PrePersist@PreUpdate
    public void pre() throws Error {
        if (ContractType.PURCHASE.equals(getType()) && getSupplier() == null) throw new Error("Supplier is required for purchase contracts");
    }

    @Override
    public String toString() {
        return getTitle();
    }


    public Document toXml(EntityManager em) {
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
                if (a.getName() != null) xml.addContent(new Element("contractor").setAttribute("name", a.getName()).setAttribute("bussinessName", (a.getFinancialAgent() != null && a.getFinancialAgent().getBusinessName() != null)?a.getFinancialAgent().getBusinessName():""));
            }
            if (getOffice() != null && getOffice().getName() != null && c.getBusinessName() != null) xml.addContent(new Element("hired").setAttribute("name", getOffice().getName()).setAttribute("bussinessName", c.getBusinessName()));
        } else {
            if (getOffice() != null && getOffice().getName() != null && c.getBusinessName() != null) xml.addContent(new Element("contractor").setAttribute("name", getOffice().getName()).setAttribute("bussinessName", c.getBusinessName()));
            if (getSupplier() != null && getSupplier().getName() != null) xml.addContent(new Element("hired").setAttribute("name", getSupplier().getName()).setAttribute("bussinessName", (getSupplier().getFinancialAgent() != null && getSupplier().getFinancialAgent().getBusinessName() != null)?getSupplier().getFinancialAgent().getBusinessName():""));
        }



        return new Document(xml);
    }


    @Action
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

    public abstract String getXslfo(EntityManager em);


}
