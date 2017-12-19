package io.mateu.erp.services.easytravelapi;

import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import org.easytravelapi.CommonsService;
import org.easytravelapi.common.*;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by miguel on 27/7/17.
 */
public class CommonsServiceImpl implements CommonsService {

    @Override
    public GetPortfolioRS getPortfolio(String token) throws Throwable {
        final GetPortfolioRS rs = new GetPortfolioRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);



        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                int totalRecursos = 0;

                for (io.mateu.erp.model.world.Country ec : (List<io.mateu.erp.model.world.Country>) em.createQuery("select s from " + io.mateu.erp.model.world.Country.class.getName() + " s order by s.isoCode").getResultList()) {

                    Country c;
                    rs.getCountries().add(c = new Country());

                    c.setResourceId("cou_" + ec.getIsoCode());
                    c.setName(ec.getName());
                    c.setUrlFriendlyName("spain");


                    for (io.mateu.erp.model.world.State es : ec.getStates()) {

                        State s;
                        c.getStates().add(s = new State());

                        s.setResourceId("stt_" + es.getId());
                        s.setName(es.getName());
                        s.setUrlFriendlyName("majorca");

                        for (io.mateu.erp.model.world.City el : es.getCities()) {

                            City l;
                            s.getCities().add(l = new City());
                            l.setResourceId("cty_" + el.getId());
                            l.setName(el.getName());
                            l.setUrlFriendlyName("palma");

                            for (Hotel eh : el.getHotels()) {

                                Resource r;
                                l.getResources().add(r = new Resource());
                                r.setResourceId("hot_" + eh.getId());
                                r.setName(eh.getName());
                                r.setLatitude(eh.getLat());
                                r.setLongitude(eh.getLon());
                                r.setType("hotel");
                                r.setDescription("City hotel. 4 stars");

                                totalRecursos++;
                            }

                            for (TransferPoint p : el.getTransferPoints()) {

                                Resource r;
                                l.getResources().add(r = new Resource());
                                r.setResourceId("tp_" + p.getId());
                                r.setName(p.getName());
                                //r.setLatitude(p.getLat());
                                //r.setLongitude(p.getLon());
                                r.setType("transferpoint");
                                r.setDescription("City hotel. 4 stars");

                                totalRecursos++;
                            }

                        }

                    }

                }


                rs.setMsg("" + totalRecursos + " resouces found.");

            }
        });

        return rs;
    }

    @Override
    public GetDataSheetRS getDataSheet(String token, String resourceId) throws Throwable {

        GetDataSheetRS rs = new GetDataSheetRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Resouce id " + resourceId + " found");


        if (resourceId == null || "".equals(resourceId)) throw new Exception("Missing resource id");


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                if (resourceId.startsWith("hot_")) {
                    Hotel h = em.find(Hotel.class, Long.parseLong(resourceId.substring("hot_".length())));

                    Pair p;
                    rs.getValues().add(p = new Pair());
                    p.setKey("id");
                    p.setValue("" + h.getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("name");
                    p.setValue(h.getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("city/id");
                    p.setValue("" + h.getCity().getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("city/name");
                    p.setValue(h.getCity().getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("state/id");
                    p.setValue("" + h.getCity().getState().getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("state/name");
                    p.setValue(h.getCity().getState().getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("country/code");
                    p.setValue(h.getCity().getState().getCountry().getIsoCode());
                    rs.getValues().add(p = new Pair());
                    p.setKey("country/name");
                    p.setValue(h.getCity().getState().getCountry().getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("category/code");
                    p.setValue(h.getCategory().getCode());
                    rs.getValues().add(p = new Pair());
                    p.setKey("category/name/en");
                    p.setValue(h.getCategory().getName().getEn());
                    rs.getValues().add(p = new Pair());
                    p.setKey("category/name/es");
                    p.setValue(h.getCategory().getName().getEs());


                } else if (resourceId.startsWith("tp_")) {
                    TransferPoint h = em.find(TransferPoint.class, Long.parseLong(resourceId.substring("tp_".length())));

                    Pair p;
                    rs.getValues().add(p = new Pair());
                    p.setKey("id");
                    p.setValue("" + h.getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("name");
                    p.setValue(h.getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("city/id");
                    p.setValue("" + h.getCity().getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("city/name");
                    p.setValue(h.getCity().getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("state/id");
                    p.setValue("" + h.getCity().getState().getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("state/name");
                    p.setValue(h.getCity().getState().getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("country/code");
                    p.setValue(h.getCity().getState().getCountry().getIsoCode());
                    rs.getValues().add(p = new Pair());
                    p.setKey("country/name");
                    p.setValue(h.getCity().getState().getCountry().getName());


                } else throw new Exception("Unknown resource"); // todo: devolver un 404

            }
        });


        return rs;
    }

    @Override
    public GetBookingsRS getBookings(String token, int fromConfirmationDate, int toConfirmationDate, int fromStartDate, int toStartDate) throws Throwable {

        final GetBookingsRS rs = new GetBookingsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                int pos = 0;
                List<io.mateu.erp.model.booking.Service> l = em.createQuery("select s from " + Service.class.getName() + " s order by s.id").getResultList();
                for (io.mateu.erp.model.booking.Service s : l) {

                        Booking b;
                        rs.getBookings().add(b = new Booking());

                        b.setBookingId("" + s.getId());
                        b.setCreated(s.getAudit().getCreated().format(DateTimeFormatter.ISO_DATE_TIME));
                        b.setCreatedBy(s.getAudit().getCreatedBy().getLogin());
                        b.setModified(s.getAudit().getModified().format(DateTimeFormatter.ISO_DATE_TIME));
                        b.setLeadName(s.getBooking().getLeadName());
                        b.setStart(s.getStart().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                        b.setEnd(s.getFinish().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                        Amount a;
                        b.setNetValue(a = new Amount());
                        a.setCurrencyIsoCode("EUR");
                        a.setValue(s.getTotalNetValue());
                        String desc = "Service booking";
                        b.setServiceType("GENERIC");
                        if (s instanceof TransferService) {
                            desc = "Transfer service";
                            b.setServiceType("TRANSFER");
                        }
                        b.setServiceDescription(desc);
                        b.setStatus((s.isCancelled())?"CANCELLED":"OK");

                    if (pos++ > 300) break;
                }

                rs.setMsg("" + l.size() + " bookings found.");
            }
        });


        return rs;
    }

    @Override
    public GetBookingRS getBooking(String token, String bookingId) throws Throwable {
        final GetBookingRS rs = new GetBookingRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                io.mateu.erp.model.booking.Service s = em.find(Service.class, Long.parseLong(bookingId));
                {

                    Booking b;
                    rs.setBooking(b = new Booking());

                    b.setBookingId("" + s.getId());
                    b.setCreated(s.getAudit().getCreated().format(DateTimeFormatter.ISO_DATE_TIME));
                    b.setCreatedBy(s.getAudit().getCreatedBy().getLogin());
                    b.setModified(s.getAudit().getModified().format(DateTimeFormatter.ISO_DATE_TIME));
                    b.setLeadName(s.getBooking().getLeadName());
                    b.setStart(s.getStart().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    b.setEnd(s.getFinish().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    Amount a;
                    b.setNetValue(a = new Amount());
                    a.setCurrencyIsoCode("EUR");
                    a.setValue(s.getTotalNetValue());
                    String desc = "Service booking";
                    b.setServiceType("GENERIC");
                    if (s instanceof TransferService) {
                        desc = "Transfer service";
                        b.setServiceType("TRANSFER");
                    }
                    b.setServiceDescription(desc);
                    b.setStatus((s.isCancelled())?"CANCELLED":"OK");


                }

                rs.setMsg("Booking found.");
            }
        });


        return rs;

    }

    @Override
    public CancelBookingRS cancelBooking(String token, String bookingId) throws Throwable {

        final CancelBookingRS rs = new CancelBookingRS();

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                io.mateu.erp.model.booking.Booking b = em.find(io.mateu.erp.model.booking.Booking.class, Long.parseLong(bookingId));

                b.cancel(em, b.getAudit().getModifiedBy());

                rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                rs.setStatusCode(200);
                rs.setMsg("Booking has been cancelled");

            }
        });

        return rs;
    }

    @Override
    public String renewToken(String token, String user) throws Throwable {
        final String[] s = {""};

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                AuthToken t = em.find(AuthToken.class, token);

                t = t.renew(em);

                s[0] = t.getId();

            }
        });

        return s[0];
    }

    @Override
    public MealPlansListRS getMealPlans(String token) throws Throwable {
        return null;
    }
}
