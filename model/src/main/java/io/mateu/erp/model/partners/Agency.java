package io.mateu.erp.model.partners;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.financials.CancellationRules;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.financials.PaymentTerms;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import io.mateu.erp.model.organization.Company;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.revenue.HandlingFee;
import io.mateu.erp.model.revenue.Markup;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Base64;
import java.util.Date;

@Entity@Getter@Setter
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Section("General")
    @NotNull
    @ListColumn
    private String name;

    @NotNull
    @ListColumn
    @ColumnWidth(100)
    private AgencyStatus status;

    @ManyToOne
    @ListColumn
    @ColumnWidth(150)
    @NoChart
    private AgencyGroup group;

    @ManyToOne
    private Market market;

    @ManyToOne
    @NotNull
    @ListColumn
    @ColumnWidth(100)
    @NoChart
    private Currency currency;

    @ManyToOne
    private FinancialAgent financialAgent;

    @ManyToOne@NotNull
    private Company company;


    private String fullAddress;

    private String telephone;

    private String email;

    private String comments;

    private boolean documentationRequired;



    // estos valores se actualizan cada 5 o 10 minutos....

    @ListColumn
    @KPI
    @Money
    private double bookings;

    @ListColumn
    @KPI
    @Money
    private double invoiced;

    @ListColumn
    @KPI
    @Money@Balance
    private double balance;

    // ... hasta aquí

    @Ignored
    private boolean updatePending;




    @Section("Product filters")
    private boolean onRequestAllowed;

    private boolean PVPAllowed;

    private boolean thridPartyAllowed;

    @Section("Revenue")
    @ManyToOne
    private Markup markup;

    @ManyToOne
    private HandlingFee handlingFee;

    @ManyToOne
    private CancellationRules cancellationRules;


    @Section("Invoicing")
    private boolean exportableToinvoicingApp;
    private String idInInvoicingApp;
    private boolean shuttleTransfersInOwnInvoice;



    public String createTempToken(PointOfSale pos) {
        return Base64.getEncoder().encodeToString(("{ \"created\": \"" + new Date() + "\", \"userId\": \"" + MDD.getCurrentUser().getLogin() + "\"" + ", \"agencyId\": \"" + getId() + "\"" + ", \"posId\": \"" + pos.getId() + "\"" + "}").getBytes());
    }


    @Override
    public String toString() {
        return getName();
    }


    @Action
    public void ratesPdf() {
        //todo: crear el tarifario
    }


    public Element toXml() {
        Element xml = new Element("actor");
        xml.setAttribute("id", "" + getId());
        xml.setAttribute("name", getName());
        if (getFinancialAgent() != null) {
            if (getFinancialAgent().getBusinessName() != null) xml.setAttribute("businessName", getFinancialAgent().getBusinessName());
            if (getFinancialAgent().getAddress() != null) xml.setAttribute("address", getFinancialAgent().getAddress());
            if (getFinancialAgent().getCity() != null) xml.setAttribute("resort", getFinancialAgent().getCity());
            if (getFinancialAgent().getPostalCode() != null) xml.setAttribute("postalCode", getFinancialAgent().getPostalCode());
            if (getFinancialAgent().getState() != null) xml.setAttribute("state", getFinancialAgent().getState());
            if (getFinancialAgent().getCountry() != null) xml.setAttribute("country", getFinancialAgent().getCountry());
            if (getFinancialAgent().getVatIdentificationNumber() != null) xml.setAttribute("vatIdentificationNumber", getFinancialAgent().getVatIdentificationNumber());
            if (getFinancialAgent().getEmail() != null) xml.setAttribute("email", getFinancialAgent().getEmail());
            if (getFinancialAgent().getTelephone() != null) xml.setAttribute("telephone", getFinancialAgent().getTelephone());
        }
        if (getEmail() != null) xml.setAttribute("email", getEmail());
        if (getComments() != null) xml.setAttribute("comments", getComments());

        return xml;
    }


    @PostUpdate@PostPersist
    public void post() {

        if (updatePending) {

            WorkflowEngine.add(() -> {

                System.out.println("Agency " + getId() + ".post().run()");
                try {
                    Helper.transact(em -> {
                        Agency b = em.merge(Agency.this);
                        if (b.isUpdatePending()) {


                            Object[] l = (Object[]) em.createQuery("select sum(x.valueInNucs), sum(x.balance) from " + Booking.class.getName() + " x where x.agency.id = " + b.getId()).getSingleResult();

                            if (l != null) {
                                b.setBookings((Double) l[0]);
                                b.setBalance((Double) l[1]);
                            }

                            Double v = (Double) em.createQuery("select sum(x.total) from " + IssuedInvoice.class.getName() + " x where x.agency.id = " + b.getId()).getSingleResult();

                            if (v != null) {
                                b.setInvoiced(v);
                            }

                            b.setUpdatePending(false);
                        }

                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            });

        }

    }



    @Override
    public boolean equals(Object obj) {
        return this == obj || (id != 0 && obj != null && obj instanceof Agency && id == ((Agency) obj).getId());
    }

}
