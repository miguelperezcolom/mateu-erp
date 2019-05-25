package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.CommissionAgentUser;
import io.mateu.erp.model.booking.File;
import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.erp.model.booking.parts.ExcursionBooking;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.payments.*;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import org.easytravelapi.AgentAccessService;
import org.easytravelapi.activity.PaymentMethod;
import org.easytravelapi.agent.*;
import org.easytravelapi.cms.ActivityCheckItem;
import org.easytravelapi.cms.GetLoginRQ;
import org.easytravelapi.cms.GetLoginRS;
import org.easytravelapi.common.Amount;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AgentAccessServiceImpl implements AgentAccessService {
    @Override
    public GetPlainListRS getPlainList(String token, int date) throws Throwable {
        GetPlainListRS rs = new GetPlainListRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        Helper.transact(em -> {

            LocalDate d = LocalDate.now();


            List<Excursion> excursions = em.createQuery("select x from " + Excursion.class.getName() + " x").getResultList();

            for (Excursion excursion : excursions) {

                {
                    ActivityCheckItem ci = new ActivityCheckItem();
                    ci.setActivityId("" + excursion.getId());
                    ci.setName(excursion.getName());
                    if (excursion.getDataSheet() != null) {
                        if (excursion.getDataSheet().getDescription() != null) ci.setDescription(excursion.getDataSheet().getDescription().getEs());
                        if (excursion.getDataSheet().getMainImage() != null) ci.setImage(excursion.getDataSheet().getMainImage().toFileLocator().getUrl());
                    }

                    rs.getActivity().add(ci);
                }


                for (int i = 0; i < 7; i++) {

                    LocalDate dx = d.plusDays(i);

                    for (ManagedEvent me : excursion.getEvents()) {
                        if (me.getDate().equals(dx)) {

                            PlainActivityItem ai;
                            rs.getPlainActivities().add(ai = new PlainActivityItem());

                            ai.setId("" + me.getId());
                            ai.setActivityId("" + excursion.getId());
                            ai.setDescription("" + excursion.getName() + " " + dx.format(DateTimeFormatter.ISO_DATE) + " " + me.getShift().getName());
                            ai.setHtmlDescription("" + excursion.getName() + "<br/>" + dx.format(DateTimeFormatter.ISO_DATE) + " " + me.getShift().getName());
                            ai.setRetailPrice(new Amount("EUR", 89.45));

                            break;
                        }
                    }

                }

            }

            List<Currency> currencies = em.createQuery("select x from " + Currency.class.getName() + " x").getResultList();

            for (Currency currency : currencies) {
                CurrencyChange ce;
                rs.getCurrencies().add(ce = new CurrencyChange());
                ce.setName(currency.getIsoCode());
                ce.setValue((float) currency.getExchangeRateToNucs());
            }

            List<MethodOfPayment> methods = em.createQuery("select x from " + MethodOfPayment.class.getName() + " x").getResultList();

            for (MethodOfPayment method : methods) {
                PaymentMethod m;
                rs.getPaymentMethods().add(m = new PaymentMethod());
                m.setCurrencyIsoCode("EUR");
                m.setKey("" + method.getId());
                m.setName(method.getName());
            }

        });

        return rs;
    }

    @Override
    public GetLoginRS login(String token, GetLoginRQ rq) throws Throwable {
        GetLoginRS rs = new GetLoginRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        if (rq.getUser() == null || rq.getPassword() == null) {
            rs.setStatusCode(500);
            rs.setMessage("Login and passsord are required");
        } else {
            Helper.transact(em -> {
                User u = em.find(CommissionAgentUser.class, rq.getUser().toLowerCase().trim());
                if (u.checkPassword(rq.getPassword())) {
                    rs.setMessage("Hello " + u.getName());
                    rs.setLogged(true);
                    rs.setAuthUser(u.getLogin());
                } else {
                    rs.setStatusCode(500);
                    rs.setMessage("Wrong password");
                }
            });
        }


        return rs;
    }

    @Override
    public UpdateBookingRS updateBookings(String token, GetUpdatedCartsRQ rq) throws Throwable {
        UpdateBookingRS rs = new UpdateBookingRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        Helper.transact(em -> {

            CommissionAgentUser u = em.find(CommissionAgentUser.class, rq.getUserId().toLowerCase().trim());


            //rs.setCompanyData(cd);

            Currency eur = em.find(Currency.class, "EUR");

            for (CartList cartList : rq.getCartList()) {


                CompanyData cd = new CompanyData();
                cd.setName("Demo");
                cd.setContactEmail("miguelperezcolom@gmail.com");
                cd.setContactPhone("54646787979");
                cd.setLegalText("Bla bla bla");


                File f = null;

                if (cartList.getCart().length > 1) {
                    f = new File();
                    f.setAgency(u.getAgency());
                    f.setCurrency(eur);
                    f.setAudit(new Audit(u));
                    f.setLeadName(cartList.getPersonaldata().getTitular());
                    f.setComments("Created from rep app");
                    em.persist(f);
                }

                ExcursionBooking b = null;

                for (Cart cart : cartList.getCart()) {

                    b = new ExcursionBooking();

                    if (f != null) {
                        f.getBookings().add(b);
                        b.setFile(f);
                    }

                    b.setAudit(new Audit(u));
                    b.setAgency(u.getAgency());
                    b.setCurrency(b.getAgency().getCurrency());

                    b.setManagedEvent(em.find(ManagedEvent.class, new Long(cart.getId())));
                    b.setExcursion((Excursion) b.getManagedEvent().getTour());
                    b.setVariant(b.getExcursion().getVariants().get(0));
                    b.setPax(1);


                    if (!Strings.isNullOrEmpty(cart.getQrCode())) b.setQrCode(cart.getQrCode());


                    //Price p = em.find(Price.class, new Long(String.valueOf(data.get("priceId"))));

                    b.setShift(b.getManagedEvent().getShift());
                    b.setLanguage(b.getShift().getLanguages().iterator().next());
                    //b.setLanguage(em.find(Excursion.class, new Long(String.valueOf(data.get("activity"))))); //todo: añadir idioma excursión
                    //b.setPickup(em.find(Excursion.class, new Long(String.valueOf(data.get("activity"))))); //todo: añadir pickup a la excursión


                    LocalDate fecha = b.getManagedEvent().getDate();

                    b.setStart(fecha);
                    b.setEnd(fecha);


                    b.setAgencyReference(cartList.getPersonaldata().getTitular());
                    if (b.getAgencyReference() == null) b.setAgencyReference("");
                    b.setSpecialRequests("");
                    b.setEmail(cartList.getPersonaldata().getEmail());
                    b.setLeadName(cartList.getPersonaldata().getTitular());
                    b.setPrivateComments("");
                    b.setPos(u.getPointOfSale());
                    b.setTelephone(cartList.getPersonaldata().getTelefono());

                    em.persist(b);
                }

                for (Payments payment : cartList.getPayments()) {

                    double v = 0;
                    try {
                        v = Double.parseDouble(payment.getAmount());
                    } catch (Exception e) {
                        e.printStackTrace();
                        v = 100;
                    }

                    Payment p = new Payment();
                    p.setDate(LocalDate.now());
                    p.setAgent(u.getCommissionAgent().getFinancialAgent());
                    p.setAccount(u.getBank());
                    PaymentLine pl;
                    p.getLines().add(pl = new PaymentLine());
                    pl.setPayment(p);
                    pl.setCurrency(eur);
                    pl.setValue(v);
                    pl.setMethodOfPayment(em.find(MethodOfPayment.class, Long.parseLong(payment.getPaymentMethod().getKey())));
                    pl.setCurrencyExchange(1);
                    pl.setValueInNucs(v);
                    if (f != null) {
                        FilePaymentAllocation a;
                        f.getPayments().add(a = new FilePaymentAllocation());
                        a.setFile(f);
                        a.setValue(v);
                        a.setPayment(p);
                        p.getBreakdown().add(a);
                    } else if (b != null) {
                        BookingPaymentAllocation a;
                        b.getPayments().add(a = new BookingPaymentAllocation());
                        a.setBooking(b);
                        a.setPayment(p);
                        p.getBreakdown().add(a);
                    }
                    em.persist(p);
                }

            }

        });

        return rs;
    }
}
