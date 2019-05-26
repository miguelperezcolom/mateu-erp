package io.mateu.erp.model.invoicing;


import com.google.common.collect.Sets;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.parts.*;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.financials.LocalizationRule;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.erp.model.taxes.VATPercent;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Indelible;
import io.mateu.mdd.core.annotations.NewNotAllowed;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.interfaces.WizardPage;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class ReceivedInvoice extends Invoice {

    @ManyToOne
    @NotNull
    @Output
    private Provider provider;


    public ReceivedInvoice() {
        super();
        setType(InvoiceType.RECEIVED);
    }

    public ReceivedInvoice(User u, Collection<PurchaseCharge> charges, FinancialAgent issuer, FinancialAgent recipient, String invoiceNumber) throws Throwable {
        setType(InvoiceType.RECEIVED);
        if (charges == null || charges.size() == 0) throw new Exception("Can not create invoices from an empty list of charges");

        setProvider(charges.iterator().next().getProvider());
        setRecipient(recipient);
        setIssuer(issuer);
        setNumber(invoiceNumber);

        boolean inicializar = true;


        double total = 0;

        Map<VAT, Map<Double, Double>> vats = new HashMap<>();

        Map<BillingConcept, Double> vatPercents = new HashMap<>();
        if (issuer.getVat() != null) {
            for (VATPercent vp : issuer.getVat().getPercents()) {
                vatPercents.put(vp.getBillingConcept(), vp.getPercent());
            }
        }


        double totalExento = 0;
        Map<VAT, Double> totalCosteRegimenEspecial = new HashMap<>();

        Map<Booking, Boolean> specialRegimeValuesPerBooking = new HashMap<>();
        Map<Booking, Boolean> includesHotelOrTransportPerBooking = new HashMap<>();
        Map<Booking, Boolean> specialRegimeProvidersPerBooking = new HashMap<>();
        for (PurchaseCharge c : charges) {
            specialRegimeValuesPerBooking.put(c.getPurchaseOrder().getService().getBooking(), specialRegimeValuesPerBooking.getOrDefault(c.getPurchaseOrder().getService().getBooking(), c.getPurchaseOrder().getService().getBooking().isSpecialRegime() || (c.getPurchaseOrder().getService().getBooking().getFile() != null && c.getPurchaseOrder().getService().getBooking().getFile().isSpecialRegime())) || c.getBillingConcept().isSpecialRegime());
            includesHotelOrTransportPerBooking.put(c.getPurchaseOrder().getService().getBooking(), includesHotelOrTransportPerBooking.getOrDefault(c.getPurchaseOrder().getService().getBooking(), c.getPurchaseOrder().getService().getBooking().isHotelOrTransportIncluded() || (c.getPurchaseOrder().getService().getBooking().getFile() != null && c.getPurchaseOrder().getService().getBooking().getFile().isHotelOrTransportIncluded())) || c.getBillingConcept().isHotelIncluded() || c.getBillingConcept().isTransportIncluded());
        }
        specialRegimeValuesPerBooking.keySet().forEach(b -> {
            boolean hay = false;
            Set<Service> services = Sets.newHashSet(b.getServices());
            if (b.getFile() != null) b.getFile().getBookings().forEach(bx -> services.addAll(bx.getServices()));
            for (Service service : services) {
                if (service.getProvider() != null && service.getProvider().getFinancialAgent() != null) hay |= service.getProvider().getFinancialAgent().isSpecialRegime();
                else hay = true;
            }
            specialRegimeProvidersPerBooking.put(b, hay);
        });


        includesHotelOrTransportPerBooking.keySet().forEach(b -> {
            if (!specialRegimeValuesPerBooking.get(b)) specialRegimeValuesPerBooking.put(b, issuer.getVat() == null || specialRegimeProvidersPerBooking.get(b));
        });

        for (PurchaseCharge c : charges) {

            if (inicializar) {

                setType((ChargeType.SALE.equals(c.getType()))?InvoiceType.ISSUED:InvoiceType.RECEIVED);

                setAudit(new Audit(u));

                setTotal(0);
                setCurrency(c.getCurrency());

                setIssueDate(LocalDate.now());
                setDueDate(LocalDate.now());

                setIssueDate(LocalDate.now());
                setTaxDate(LocalDate.now());


                inicializar = false;
            }

            getLines().add(new PurchaseOrderInvoiceLine(this, (PurchaseCharge) c));
            c.setInvoice(this);

            total += c.getTotal();


            VAT vat = null;
            if (LocalizationRule.CUSTOMER.equals(c.getBillingConcept().getLocalizationRule())) vat = recipient.getVat();
            else if (LocalizationRule.SERVICE.equals(c.getBillingConcept().getLocalizationRule())) {
                if (c.getPurchaseOrder().getService().getBooking() instanceof HotelBooking) vat = getVatForResort(((HotelBooking) c.getPurchaseOrder().getService().getBooking()).getHotel().getResort());
                else if (c.getPurchaseOrder().getService().getBooking() instanceof TransferBooking) vat = getVatForResort(((TransferBooking) c.getPurchaseOrder().getService().getBooking()).getOrigin().getResort());
                else if (c.getPurchaseOrder().getService().getBooking() instanceof ExcursionBooking) vat = getVatForResort(((ExcursionBooking) c.getPurchaseOrder().getService().getBooking()).getExcursion().getResort());
                else if (c.getPurchaseOrder().getService().getBooking() instanceof CircuitBooking) vat = getVatForResort(((CircuitBooking) c.getPurchaseOrder().getService().getBooking()).getCircuit().getResort());
                else if (c.getPurchaseOrder().getService().getBooking() instanceof GenericBooking) vat = getVatForResort(((GenericBooking) c.getPurchaseOrder().getService().getBooking()).getProduct().getResort());
                else if (c.getPurchaseOrder().getService().getBooking() instanceof FreeTextBooking) vat = getVatForResort(((FreeTextBooking) c.getPurchaseOrder().getService().getBooking()).getOffice().getResort());
                else vat = issuer.getVat();
            } else vat = issuer.getVat();

            if (issuer.getVat() != null && vatPercents.containsKey(c.getBillingConcept())) {
                if (specialRegimeValuesPerBooking.get(c.getPurchaseOrder().getService().getBooking())) {
                    if (c.getChargedTo() == null) {
                        double antes = totalCosteRegimenEspecial.getOrDefault(issuer.getVat(), 0d);
                        totalCosteRegimenEspecial.put(issuer.getVat(), antes + c.getTotal());
                    }
                } else {
                    Map<Double, Double> m = vats.get(issuer.getVat());
                    if (m == null) vats.put(issuer.getVat(), m = new HashMap<>());
                    double p = vatPercents.get(c.getBillingConcept());
                    double v = m.containsKey(p)?m.get(p):0;
                    m.put(p, v + c.getTotal());
                }
            } else {
                totalExento += c.getTotal();
            }

        }


        for (VAT v : vats.keySet()) {

            for (double p : vats.get(v).keySet()) {
                VATLine l;
                getVATLines().add(l = new VATLine());

                l.setInvoice(this);
                l.setPercent(p);
                l.setTotal(Helper.roundEuros(vats.get(v).get(p)));
                l.setBase(Helper.roundEuros(100d * (l.getTotal() / (100d + p))));
                l.setVat(v);
            }

        }

        totalCosteRegimenEspecial.keySet().forEach(v -> {
            double t = 0;
            double c = Helper.roundEuros(totalCosteRegimenEspecial.get(v));
            if (t != 0 || c != 0) {
                VATLine l;
                getVATLines().add(l = new VATLine());
                l.setInvoice(this);
                l.setVat(v);
                l.setPercent(v.getSpecialRegimePercent());
                l.setBase(Helper.roundEuros(t - c));
                l.setTotal(Helper.roundEuros(l.getPercent() * (t - c) / 100d));
                l.setSpecialRegime(true);
            }
        });

        totalExento = Helper.roundEuros(totalExento);
        if (totalExento != 0) {
            VATLine l;
            getVATLines().add(l = new VATLine());
            l.setInvoice(this);
            l.setPercent(0);
            l.setTotal(0);
            l.setBase(totalExento);
            l.setVat(null);
            l.setExempt(true);
        }

        setTotal(total);

        setRetainedPercent(0);

    }

    @Override
    public String getXslfo(EntityManager em) {
        return AppConfig.get(em).getXslfoForIssuedInvoice();
    }


    @Action("Enter invoices")
    public static WizardPage enter() {
        return new EnterInvoicesWizardParametersPage();
    }

}
