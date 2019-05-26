package io.mateu.erp.model.invoicing;


import com.google.common.collect.Sets;
import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.parts.*;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.financials.LocalizationRule;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.payments.InvoicePaymentAllocation;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.erp.model.taxes.VATPercent;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.WizardPage;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class IssuedInvoice extends Invoice {


    @ManyToOne
    @NotNull
    @Output
    private Agency agency;

    @ManyToOne
    @NotNull
    @Output
    private InvoiceSerial serial;

    @KPI
    private boolean sent;

    @OneToMany(mappedBy = "chargedTo")@Ignored
    private List<PurchaseCharge> costs = new ArrayList<>();


    public IssuedInvoice() {
        super();
    }

    public IssuedInvoice(User u, Collection<BookingCharge> charges, boolean proforma, FinancialAgent issuer, FinancialAgent recipient, String invoiceNumber) throws Throwable {
        if (charges == null || charges.size() == 0) throw new Exception("Can not create invoices from an empty list of charges");

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
        Map<VAT, Double> totalRegimenEspecial = new HashMap<>();
        Map<VAT, Double> totalCosteRegimenEspecial = new HashMap<>();

        Map<Booking, Boolean> specialRegimeValuesPerBooking = new HashMap<>();
        Map<Booking, Boolean> includesHotelOrTransportPerBooking = new HashMap<>();
        Map<Booking, Boolean> specialRegimeProvidersPerBooking = new HashMap<>();
        for (BookingCharge c : charges) {
            specialRegimeValuesPerBooking.put(c.getBooking(), specialRegimeValuesPerBooking.getOrDefault(c.getBooking(), c.getBooking().isSpecialRegime() || (c.getBooking().getFile() != null && c.getBooking().getFile().isSpecialRegime())) || c.getBillingConcept().isSpecialRegime());
            includesHotelOrTransportPerBooking.put(c.getBooking(), includesHotelOrTransportPerBooking.getOrDefault(c.getBooking(), c.getBooking().isHotelOrTransportIncluded() || (c.getBooking().getFile() != null && c.getBooking().getFile().isHotelOrTransportIncluded())) || c.getBillingConcept().isHotelIncluded() || c.getBillingConcept().isTransportIncluded());
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

        for (BookingCharge c : charges) {

            if (inicializar) {

                setType((ChargeType.SALE.equals(c.getType()))?InvoiceType.ISSUED:InvoiceType.RECEIVED);

                setAudit(new Audit(u));

                setTotal(0);
                setCurrency(c.getCurrency());

                setIssueDate(LocalDate.now());
                setDueDate(LocalDate.now());


                if (this instanceof IssuedInvoice) {
                    if (c.getAgency().getFinancialAgent() == null) throw new Exception("If you want to create proformas or invoices you must set the financial agent for the agency " + c.getAgency().getName());
                    if (c.getAgency().getCompany() == null) throw new Exception("If you want to create proformas or invoices you must set the company for the agency " + c.getAgency().getName());
                    if (c.getAgency().getCompany().getFinancialAgent() == null) throw new Exception("If you want to create proformas or invoices you must set the financial agent for the company " + c.getAgency().getCompany().getName());

                    setRecipient(c.getAgency().getFinancialAgent());
                    setIssuer(c.getAgency().getCompany().getFinancialAgent());

                    if (!proforma) {
                        if (InvoiceType.ISSUED.equals(getType())) {
                            ((IssuedInvoice)this).setSerial(c.getAgency().getCompany().getBillingSerial());
                            if (((IssuedInvoice)this).getSerial() == null) throw new Exception("Missing invoice serial. Please set at company " + c.getAgency().getCompany().getName());
                            setNumber(((IssuedInvoice)this).getSerial().getPrefix() + "" + ((IssuedInvoice)this).getSerial().getNextNumber());
                        } else {
                            setNumber("PROFORMA");
                        }
                    }
                }

                setIssueDate(LocalDate.now());
                setTaxDate(LocalDate.now());


                inicializar = false;
            }

            getLines().add(new BookingInvoiceLine(this, (BookingCharge) c));
            if (!proforma) {
                c.setInvoice(this);
            }

            total += c.getTotal();


            VAT vat = null;
            if (LocalizationRule.CUSTOMER.equals(c.getBillingConcept().getLocalizationRule())) vat = recipient.getVat();
            else if (LocalizationRule.SERVICE.equals(c.getBillingConcept().getLocalizationRule())) {
                if (c.getBooking() instanceof HotelBooking) vat = getVatForResort(((HotelBooking) c.getBooking()).getHotel().getResort());
                else if (c.getBooking() instanceof TransferBooking) vat = getVatForResort(((TransferBooking) c.getBooking()).getOrigin().getResort());
                else if (c.getBooking() instanceof ExcursionBooking) vat = getVatForResort(((ExcursionBooking) c.getBooking()).getExcursion().getResort());
                else if (c.getBooking() instanceof CircuitBooking) vat = getVatForResort(((CircuitBooking) c.getBooking()).getCircuit().getResort());
                else if (c.getBooking() instanceof GenericBooking) vat = getVatForResort(((GenericBooking) c.getBooking()).getProduct().getResort());
                else if (c.getBooking() instanceof FreeTextBooking) vat = getVatForResort(((FreeTextBooking) c.getBooking()).getOffice().getResort());
                else vat = issuer.getVat();
            } else vat = issuer.getVat();

            if (issuer.getVat() != null && vatPercents.containsKey(c.getBillingConcept())) {
                if (specialRegimeValuesPerBooking.get(c.getBooking())) {
                    double antes = totalRegimenEspecial.getOrDefault(issuer.getVat(), 0d);
                    totalRegimenEspecial.put(issuer.getVat(), antes + c.getTotal());
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

        includesHotelOrTransportPerBooking.keySet().forEach(b -> {
            b.getServices().forEach(s -> {
                s.getPurchaseOrders().forEach(po -> {
                    po.getCharges().forEach(c -> {
                        if (c.getChargedTo() == null) {
                            try {
                                c.setChargedTo(IssuedInvoice.this);
                                getCosts().add(c);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                });
            });
        });


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

        if (this instanceof IssuedInvoice) {
            for (PurchaseCharge c : ((IssuedInvoice) this).getCosts()) {
                if (issuer.getVat() != null && vatPercents.containsKey(c.getBillingConcept())) {
                    if (specialRegimeValuesPerBooking.get(c.getPurchaseOrder().getService().getBooking())) {
                        double antes = totalCosteRegimenEspecial.getOrDefault(issuer.getVat(), 0d);
                        totalCosteRegimenEspecial.put(issuer.getVat(), antes + c.getTotal());
                    }
                }
            }
        }

        totalRegimenEspecial.keySet().forEach(v -> {
            double t = Helper.roundEuros(totalRegimenEspecial.get(v));
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


        setTotal(total);

        setRetainedPercent(0);
    }


    @Override
    public String getXslfo(EntityManager em)  {
        return AppConfig.get(em).getXslfoForIssuedInvoice();
    }


    @Action
    public static WizardPage issue() {
        return new IssueInvoicesParametersPage();
    }


    @Action(icon = VaadinIcons.ENVELOPE)
    public void send(EntityManager em) {
        setSent(true);
        em.merge(this);
    }


    @Action(icon = VaadinIcons.ENVELOPE)
    public static void sendSelection(EntityManager em, Set<IssuedInvoice> selection) {
        selection.forEach(i -> {
            i.setSent(true);
            em.merge(i);
        });
    }


    @Action(icon = VaadinIcons.ENVELOPE)
    public static SelfBillForm selfBill(Set<IssuedInvoice> selection) {
        return new SelfBillForm(selection);
    }


    @PostPersist
    public void post() {
        WorkflowEngine.add(() -> {

            try {

                Helper.transact(em -> {

                    IssuedInvoice i = em.find(IssuedInvoice.class, getId());

                    // mover pagos a la factura
                    List<Booking> bookings = new ArrayList<>();
                    i.getLines().forEach(l -> {
                        if (l instanceof BookingInvoiceLine) {
                            BookingInvoiceLine bil = (BookingInvoiceLine) l;
                            if (!bookings.contains(bil.getCharge().getBooking())) bookings.add(bil.getCharge().getBooking());
                        }
                    });
                    /*
                    bookings.forEach(b -> {
                        b.getPayments().forEach(a -> {
                            if (a.getInvoice() == null) {
                                a.setInvoice(i);
                                InvoicePaymentAllocation ipa;
                                i.getPayments().add(ipa = new InvoicePaymentAllocation());
                                ipa.setInvoice(i);
                                ipa.setPayment(a.getPayment());
                                ipa.setValue(a.getValue());
                                ipa.getPayment().getBreakdown().add(ipa);
                            }
                        });
                    });
                    */

                });

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        });
    }

}
