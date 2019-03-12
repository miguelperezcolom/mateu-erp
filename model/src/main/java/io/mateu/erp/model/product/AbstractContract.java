package io.mateu.erp.model.product;

import com.google.common.base.Strings;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.organization.Company;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.partners.AgencyGroup;
import io.mateu.erp.model.partners.Market;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.generic.Contract;
import io.mateu.erp.model.product.hotel.RatesType;
import io.mateu.erp.model.product.tour.Tour;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.authentication.Audit;
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
@Entity
@Table(name = "contract", indexes = { @Index(name = "i_contract_deprecated", columnList = "deprecated") })
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

    @NotNull
    @MainSearchFilter
    @ListColumn
    private String title;

    @SearchFilter
    @ListColumn
    @ColumnWidth(70)
    private boolean active;

    @NotNull
    @MainSearchFilter
    @ListColumn
    @CellStyleGenerator(ContractTypeCellStyleGenerator.class)
    @ColumnWidth(70)
    private ContractType type;

    private RatesType ratesType = RatesType.NET;

    private boolean mandatoryRates;


    @NotNull
    @ManyToOne
    private BillingConcept billingConcept;

    @ManyToOne
    @NotNull
    private ProductLine productLine;

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

    private LocalDate bookingWindowFrom;

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
    @NoChart
    private Provider supplier;

    public boolean isSupplierVisible() {
        return ContractType.PURCHASE.equals(getType());
    }

    @ManyToOne
    @SearchFilter
    @ListColumn
    @NotNull
    @ColumnWidth(120)
    private Office office;


    @OneToMany
    @UseChips
    private List<AgencyGroup> agencyGroups = new ArrayList<>();

    @OneToMany
    @JoinTable(name = "contract_bannedagencygroup")
    @UseChips
    private List<AgencyGroup> bannedAgencyGroups = new ArrayList<>();

    @OneToMany
    @UseChips
    private List<Agency> agencies = new ArrayList<>();

    @OneToMany
    @JoinTable(name = "contract_bannedagency")
    @UseChips
    private List<Agency> bannedAgencies = new ArrayList<>();

    @OneToMany
    @UseChips
    private List<Market> markets = new ArrayList<>();


    @OneToMany
    @JoinTable(name = "contract_bannedmarket")
    @UseChips
    private List<Market> bannedMarkets = new ArrayList<>();

    @OneToMany
    @UseChips
    private List<Company> companies = new ArrayList<>();

    @Tab("Tour")
    @OneToMany
    @UseChips
    private List<Tour> tours = new ArrayList<>();

    @Tab("Signature")
    private String signedAt;

    private String partnerSignatory;

    private String ownSignatory;

    private LocalDate signatureDate;

    @Tab("Commissions")
    @ManyToOne
    private CommissionTerms commissionTerms;

    @Tab("Payment")
    @ManyToOne
    private PaymentTerms paymentTerms;

    @ManyToOne
    @Tab("Cancellation")
    private CancellationRules cancellationRules;

    @Tab("Clauses")
    @OneToMany(cascade = CascadeType.ALL)
    @FullWidth
    @OrderColumn(name = "_order")
    private List<ContractClauseItem> clauses = new ArrayList<>();



    @KPI
    private double averagePrice;

    @KPI
    private double totalSales;

    @KPI
    private int totalBookings;

    @Ignored
    private boolean deprecated;




    @PreUpdate
    public void pre() throws Error {
        if (ContractType.PURCHASE.equals(getType()) && getSupplier() == null) throw new Error("Supplier is required for purchase contracts");
        clauses.sort((c1, c2) -> c1.getOrder() - c2.getOrder());
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
        xml.setAttribute("ratesType", "" + getRatesType());
        if (getBillingConcept() != null && getBillingConcept().getName() != null) xml.setAttribute("billingConcept", getBillingConcept().getName());
        if (getValidFrom() != null) xml.setAttribute("validFrom", getValidFrom().toString());
        if (getValidTo() != null) xml.setAttribute("validTo", getValidTo().toString());
        if (getBookingWindowFrom() != null) xml.setAttribute("bookingWindowFrom", getBookingWindowFrom().toString());
        if (getBookingWindowTo() != null) xml.setAttribute("bookingWindowTo", getBookingWindowTo().toString());

        if (!Strings.isNullOrEmpty(getSpecialTerms())) {
            Element st;
            xml.addContent(st = new Element("specialTerms"));
            for (String s : getSpecialTerms().split("\n")) {
                xml.addContent(st = new Element("line").setText(s));
            }
        }

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
            o.addContent(getOffice().getCompany().toXml());
        }

        if (ContractType.SALE.equals(getType())) {
            if (getAgencies().size() > 0) xml.addContent(getAgencies().get(0).toXml().setName("partner"));
        } else {
            if (getSupplier() != null) xml.addContent(getSupplier().toXml().setName("partner"));
        }


        if (getSignedAt() != null) xml.setAttribute("signedAt", getSignedAt());
        if (getPartnerSignatory() != null) xml.setAttribute("partnerSignatory", getPartnerSignatory());
        if (getOwnSignatory() != null) xml.setAttribute("ownSignatory", getOwnSignatory());
        if (getSignatureDate() != null) xml.setAttribute("signatureDate", getSignatureDate().format(DateTimeFormatter.ISO_DATE));


        Element ts;
        xml.addContent(ts = new Element("targets"));
        for (AgencyGroup t : getAgencyGroups()) ts.addContent(new Element("target").setAttribute("name", t.getName()));
        for (Agency t : getAgencies()) ts.addContent(new Element("target").setAttribute("name", t.getName()));
        for (Market t : getMarkets()) ts.addContent(new Element("target").setAttribute("name", t.getName()));
        for (Company t : getCompanies()) ts.addContent(new Element("target").setAttribute("name", t.getName()));

        xml.addContent(ts = new Element("bannedTargets"));
        for (AgencyGroup t : getBannedAgencyGroups()) ts.addContent(new Element("target").setAttribute("name", t.getName()));
        for (Agency t : getBannedAgencies()) ts.addContent(new Element("target").setAttribute("name", t.getName()));
        for (Market t : getBannedMarkets()) ts.addContent(new Element("target").setAttribute("name", t.getName()));

        if (ContractType.SALE.equals(getType())) {
            if (getAgencies().size() > 0) {
                Agency a = getAgencies().get(0);
                if (a.getName() != null) xml.addContent(new Element("contractor").setAttribute("name", a.getName()).setAttribute("bussinessName", (a.getFinancialAgent() != null && a.getFinancialAgent().getBusinessName() != null)?a.getFinancialAgent().getBusinessName():""));
            }
            if (getOffice() != null && getOffice().getName() != null && c.getBusinessName() != null) xml.addContent(new Element("hired").setAttribute("name", getOffice().getName()).setAttribute("bussinessName", c.getBusinessName()));
        } else {
            if (getOffice() != null && getOffice().getName() != null && c.getBusinessName() != null) xml.addContent(new Element("contractor").setAttribute("name", getOffice().getName()).setAttribute("bussinessName", c.getBusinessName()));
            if (getSupplier() != null && getSupplier().getName() != null) xml.addContent(new Element("hired").setAttribute("name", getSupplier().getName()).setAttribute("bussinessName", (getSupplier().getFinancialAgent() != null && getSupplier().getFinancialAgent().getBusinessName() != null)?getSupplier().getFinancialAgent().getBusinessName():""));
        }


        Element os;
        xml.addContent(os = new Element("offices"));
        ((List<Office>)em.createQuery("select x from " + Office.class.getName() + " x order by x.id").getResultList()).forEach(o -> os.addContent(new Element("office").setAttribute("name", "" + o.getName()).setAttribute("tel", o.getTelephone() != null?o.getTelephone():"").setAttribute("fax", o.getFax() != null?o.getFax():"").setAttribute("email", o.getEmail() != null?o.getEmail():"").setAttribute("address", o.getAddress() != null?o.getAddress():"")));


        Element clauses;
        xml.addContent(clauses = new Element("clauses"));
        getClauses().forEach(o -> clauses.addContent(new Element("clause").setAttribute("text", "" + o.getText())));


        if (paymentTerms != null) {
            Element payment;
            xml.addContent(payment = new Element("payment"));
            getPaymentTerms().getLines().forEach(o -> payment.addContent(new Element("line").setAttribute("referenceDate", "" + o.getReferenceDate()).setAttribute("release", "" + o.getRelease()).setAttribute("percent", "" + o.getPercent())));
        }

        if (cancellationRules != null) {
            Element cxrs;
            xml.addContent(cxrs = new Element("cancellation"));
            cancellationRules.getRules().forEach(o -> cxrs.addContent(new Element("line").setAttribute("start", o.getStart() != null?o.getStart().format(DateTimeFormatter.ISO_DATE):"").setAttribute("end", o.getEnd() != null?o.getEnd().format(DateTimeFormatter.ISO_DATE):"").setAttribute("release", "" + o.getRelease()).setAttribute("firstNights", "" + o.getFirstNights()).setAttribute("percent", "" + o.getPercent())));
        }

        return new Document(xml);
    }


    @Action(order = 1)
    public void addClauses(@NotNull ContractClauseGroup clauseGroup) throws Throwable {
        if (clauseGroup != null) {
            clauseGroup.getClauses().forEach(c -> clauses.add(new ContractClauseItem(c.getOrder(), c.getText())));
        }
    }


    @Action(order = 2)
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


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof  AbstractContract && id == ((AbstractContract)obj).id);
    }


    public boolean isValidForSale(Agency agency, LocalDate start, LocalDate end) {
        boolean ok = isActive();
        ok = ok && (ContractType.SALE.equals(type) || (agency.getMarkup() != null && agency.getMarkup().getLine(productLine) != null));
        ok = ok && (agencies.size() == 0 || agencies.contains(agency));
        ok = ok && (bannedAgencies.size() == 0 || !bannedAgencies.contains(agency));
        ok = ok && (agencyGroups.size() == 0 || agencyGroups.contains(agency.getGroup()));
        ok = ok && (bannedAgencyGroups.size() == 0 || !bannedAgencyGroups.contains(agency.getGroup()));
        ok = ok && (markets.size() == 0 || markets.contains(agency.getMarket()));
        ok = ok && (bannedMarkets.size() == 0 || !bannedMarkets.contains(agency.getMarket()));
        ok = ok && (companies.size() == 0 || companies.contains(agency.getCompany()));
        ok = ok && (!start.isBefore(validFrom) && !end.isAfter(validTo));
        return ok;
    }


    @PrePersist
    public void prePersist() {
        pre();
        if (this instanceof Contract) Accessor.get(Helper.getEMFromThreadLocal()).getGenericContracts().add((Contract) this);
        else if (this instanceof io.mateu.erp.model.product.transfer.Contract) Accessor.get(Helper.getEMFromThreadLocal()).getTransferContracts().add((io.mateu.erp.model.product.transfer.Contract) this);
        else if (this instanceof io.mateu.erp.model.product.tour.Contract) Accessor.get(Helper.getEMFromThreadLocal()).getTourContracts().add((io.mateu.erp.model.product.tour.Contract) this);
    }

    @PreRemove
    public void preRemove() {
        if (this instanceof Contract) Accessor.get(Helper.getEMFromThreadLocal()).getGenericContracts().remove((Contract) this);
        else if (this instanceof io.mateu.erp.model.product.transfer.Contract) Accessor.get(Helper.getEMFromThreadLocal()).getTransferContracts().remove((io.mateu.erp.model.product.transfer.Contract) this);
        else if (this instanceof io.mateu.erp.model.product.tour.Contract) Accessor.get(Helper.getEMFromThreadLocal()).getTourContracts().remove((io.mateu.erp.model.product.tour.Contract) this);
    }
}
