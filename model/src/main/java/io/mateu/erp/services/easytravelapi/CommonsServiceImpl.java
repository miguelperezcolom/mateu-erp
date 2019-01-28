package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.booking.File;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.parts.HotelBooking;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.erp.model.product.DataSheetImage;
import io.mateu.erp.model.product.FeatureValue;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import org.easytravelapi.CommonsService;
import org.easytravelapi.common.*;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

                AtomicReference<Integer> totalRecursos = new AtomicReference<>(0);

                for (io.mateu.erp.model.world.Country ec : (List<io.mateu.erp.model.world.Country>) em.createQuery("select s from " + io.mateu.erp.model.world.Country.class.getName() + " s order by s.order, s.isoCode").getResultList()) {

                    Country c;
                    rs.getCountries().add(c = new Country());

                    c.setResourceId("cou-" + ec.getIsoCode());
                    c.setName(new MultilingualText("es", ec.getName(), "en", ec.getName(), "it", ec.getName()));
                    c.setUrlFriendlyName("spain");


                    ec.getDestinations().stream().sorted((o1, o2) -> o1.getOrder() - o2.getOrder()).forEach(es -> {

                        State s;
                        c.getStates().add(s = new State());

                        s.setResourceId("des-" + es.getId());
                        s.setName(new MultilingualText("es", es.getName(), "en", es.getName(), "it", es.getName()));
                        s.setUrlFriendlyName(Helper.urlize(es.getName()));

                        es.getZones().stream().sorted((z1, z2) -> z1.getOrder() - z2.getOrder()).forEach(el -> {
                            City l;
                            s.getCities().add(l = new City());
                            l.setResourceId("zon-" + el.getId());
                            l.setName(new MultilingualText("es", el.getName()));
                            l.setUrlFriendlyName(Helper.urlize(el.getName()));

                            for (AbstractProduct p : el.getProducts()) if (p instanceof Hotel) {
                                Hotel eh = (Hotel) p;

                                Resource r;
                                l.getResources().add(r = new Resource());
                                r.setResourceId("hot-" + eh.getId());
                                r.setName(new MultilingualText("es", eh.getName(), "en", eh.getName(), "it", eh.getName()));
                                r.setLatitude(eh.getLat());
                                r.setLongitude(eh.getLon());
                                r.setType("hotel");
                                r.setDescription(new MultilingualText("es", eh.getDataSheet() != null && eh.getDataSheet().getDescription() != null?eh.getDataSheet().getDescription().get("es"):"No description"));

                                totalRecursos.getAndSet(totalRecursos.get() + 1);
                            }

                            for (TransferPoint p : el.getTransferPoints()) {

                                Resource r;
                                l.getResources().add(r = new Resource());
                                r.setResourceId("tp-" + p.getId());
                                r.setName(new MultilingualText("es", p.getName(), "en", p.getName()));
                                //r.setLatitude(p.getLat());
                                //r.setLongitude(p.getLon());
                                r.setType("transferpoint");
                                r.setDescription(new MultilingualText("es", p.getInstructions(), "en", p.getInstructions()));

                                totalRecursos.getAndSet(totalRecursos.get() + 1);
                            }
                        });

                    });

                }


                rs.setMsg("" + totalRecursos.get() + " resouces found.");

            }
        });

        return rs;
    }

    @Override
    public GetDataSheetRS getDataSheet(String token, String resourceId, String language) throws Throwable {

        GetDataSheetRS rs = new GetDataSheetRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Resouce id " + resourceId + " found");


        if (resourceId == null || "".equals(resourceId)) throw new Exception("Missing resource id");


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                if (resourceId.startsWith("hot")) {
                    Hotel h = em.find(Hotel.class, Long.parseLong(resourceId.substring("hot-".length())));

                    /*
                    case 'name' -->  nombre del hotel
case 'images/image' --> Imagenes genericas del hotel
case 'images/main' --> Imagen Principal del hotel
case 'images/map' --> Imagen mapa localizacion de hotel (si no hay utiliza longitud y latitud)
case 'description': --> descripcion del hotel
case 'longitude': -->longitud
case: 'latitude': --> latitud
                     */

                    Pair p;
                    rs.getValues().add(p = new Pair());
                    p.setKey("id");
                    p.setValue("" + h.getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("name");
                    p.setValue(h.getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("keys");
                    p.setValue("" + h.getKeys());
                    rs.getValues().add(p = new Pair());
                    p.setKey("stars");
                    p.setValue("" + h.getStars());
                    if (!Strings.isNullOrEmpty(h.getLon())) {
                        rs.getValues().add(p = new Pair());
                        p.setKey("longitude");
                        p.setValue(h.getLon());
                    }
                    if (!Strings.isNullOrEmpty(h.getLat())) {
                        rs.getValues().add(p = new Pair());
                        p.setKey("latitude");
                        p.setValue(h.getLat());
                    }
                    rs.getValues().add(p = new Pair());
                    p.setKey("city/id");
                    p.setValue("" + h.getZone().getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("city/name");
                    p.setValue(h.getZone().getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("state/id");
                    p.setValue("" + h.getZone().getDestination().getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("state/name");
                    p.setValue(h.getZone().getDestination().getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("country/code");
                    p.setValue(h.getZone().getDestination().getCountry().getIsoCode());
                    rs.getValues().add(p = new Pair());
                    p.setKey("country/name");
                    p.setValue(h.getZone().getDestination().getCountry().getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("category/code");
                    p.setValue(h.getCategory().getCode());
                    rs.getValues().add(p = new Pair());
                    p.setKey("category/name/en");
                    p.setValue(h.getCategory().getName().getEn());
                    rs.getValues().add(p = new Pair());
                    p.setKey("category/name/es");
                    p.setValue(h.getCategory().getName().getEs());

                    if (h.getDataSheet() != null) {
                        if (h.getDataSheet().getDescription() != null) {
                            rs.getValues().add(p = new Pair());
                            p.setKey("description");
                            p.setValue(h.getDataSheet().getDescription().get(language));
                        }

                        if (h.getDataSheet().getMainImage() != null) {
                            rs.getValues().add(p = new Pair());
                            p.setKey("images/main");
                            p.setValue(h.getDataSheet().getMainImage().toFileLocator().getUrl());
                        }

                        for (DataSheetImage i : h.getDataSheet().getImages())
                            if (i != null && i.getImage() != null) {
                                rs.getValues().add(p = new Pair());
                                p.setKey("images/image");
                                p.setValue(i.getImage().toFileLocator().getUrl());
                            }


                        for (FeatureValue i : h.getDataSheet().getFeatures())
                            if (i != null && i.getFeature() != null) {
                                rs.getValues().add(p = new Pair());
                                String k = "";
                                if (i.getFeature() != null && i.getFeature().getGroup() != null)
                                    k += "" + i.getFeature().getGroup().getName().get(language);
                                if (i.getFeature() != null && i.getFeature().getGroup() != null) {
                                    if (!"".equals(k)) k += "/";
                                    k += "" + i.getFeature().getName().get(language);
                                }
                                p.setKey(k);
                                p.setValue(i.getValue());
                            }


                    }

                } else if (resourceId.startsWith("exc")) {
                    Excursion h = em.find(Excursion.class, Long.parseLong(resourceId.substring("exc-".length())));

                    /*
                    case 'name' -->  nombre del hotel
case 'images/image' --> Imagenes genericas del hotel
case 'images/main' --> Imagen Principal del hotel
case 'images/map' --> Imagen mapa localizacion de hotel (si no hay utiliza longitud y latitud)
case 'description': --> descripcion del hotel
case 'longitude': -->longitud
case: 'latitude': --> latitud
                     */

                    Pair p;
                    rs.getValues().add(p = new Pair());
                    p.setKey("id");
                    p.setValue("" + h.getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("name");
                    p.setValue(h.getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("city/id");
                    p.setValue("" + h.getZone().getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("city/name");
                    p.setValue(h.getZone().getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("state/id");
                    p.setValue("" + h.getZone().getDestination().getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("state/name");
                    p.setValue(h.getZone().getDestination().getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("country/code");
                    p.setValue(h.getZone().getDestination().getCountry().getIsoCode());
                    rs.getValues().add(p = new Pair());
                    p.setKey("country/name");
                    p.setValue(h.getZone().getDestination().getCountry().getName());

                    if (h.getDataSheet() != null) {
                        if (h.getDataSheet().getDescription() != null) {
                            rs.getValues().add(p = new Pair());
                            p.setKey("description");
                            p.setValue(h.getDataSheet().getDescription().get(language));
                        }

                        if (h.getDataSheet().getMainImage() != null) {
                            rs.getValues().add(p = new Pair());
                            p.setKey("images/main");
                            p.setValue(h.getDataSheet().getMainImage().toFileLocator().getUrl());
                        }

                        for (DataSheetImage i : h.getDataSheet().getImages()) if (i != null && i.getImage() != null) {
                            rs.getValues().add(p = new Pair());
                            p.setKey("images/image");
                            p.setValue(i.getImage().toFileLocator().getUrl());
                        }


                        for (FeatureValue i : h.getDataSheet().getFeatures()) if (i != null && i.getFeature() != null) {
                            rs.getValues().add(p = new Pair());
                            String k = "";
                            if (i.getFeature() != null && i.getFeature().getGroup() != null) k += "" + i.getFeature().getGroup().getName().get(language);
                            if (i.getFeature() != null && i.getFeature().getGroup() != null)  {
                                if (!"".equals(k)) k += "/";
                                k += "" + i.getFeature().getName().get(language);
                            }
                            p.setKey(k);
                            p.setValue(i.getValue());
                        }


                    }

                } else if (resourceId.startsWith("tp")) {
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
                    p.setValue("" + h.getZone().getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("city/name");
                    p.setValue(h.getZone().getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("state/id");
                    p.setValue("" + h.getZone().getDestination().getId());
                    rs.getValues().add(p = new Pair());
                    p.setKey("state/name");
                    p.setValue(h.getZone().getDestination().getName());
                    rs.getValues().add(p = new Pair());
                    p.setKey("country/code");
                    p.setValue(h.getZone().getDestination().getCountry().getIsoCode());
                    rs.getValues().add(p = new Pair());
                    p.setKey("country/name");
                    p.setValue(h.getZone().getDestination().getCountry().getName());


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
                    //a.setValue(s.getTotalNetValue());
                    String desc = "Service file";
                    b.setServiceType("GENERIC");
                    if (s instanceof TransferService) {
                        desc = "Transfer service";
                        b.setServiceType("TRANSFER");
                    }
                    b.setServiceDescription(desc);
                    b.setStatus((s.isActive()) ? "OK" : "CANCELLED");

                    if (pos++ > 300) break;
                }

                rs.setMsg("" + l.size() + " files found.");
            }
        });


        return rs;
    }

    @Override
    public GetBookingRS getBooking(String token, String email, String bookingId) {
        final GetBookingRS rs = new GetBookingRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);


        try {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {


                    long id = 0;

                    try {
                        id = Long.parseLong(bookingId);
                    } catch (Exception e) {
                    }

                    io.mateu.erp.model.booking.Booking bkg = em.find(io.mateu.erp.model.booking.Booking.class, id);

                    if (bkg != null) {

                        if (!email.equalsIgnoreCase(bkg.getEmail())) {

                            rs.setStatusCode(404);
                            rs.setMsg("Email " + email + " not valid for booking " + bookingId + ".");

                        } else {
                            Booking b;
                            rs.setBooking(b = new Booking());

                            b.setBookingId("" + bkg.getId());
                            if (bkg.getAudit() != null) {
                                b.setCreated(bkg.getAudit().getCreated().format(DateTimeFormatter.ISO_DATE_TIME));
                                b.setCreatedBy(bkg.getAudit().getCreatedBy().getLogin());
                                b.setModified(bkg.getAudit().getModified().format(DateTimeFormatter.ISO_DATE_TIME));
                            }
                            b.setLeadName(bkg.getLeadName());
                            b.setStart(bkg.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            b.setEnd(bkg.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            Amount a;
                            b.setNetValue(a = new Amount());
                            a.setCurrencyIsoCode("EUR");
                            //a.setValue(s.getTotalNetValue());
                            String desc = "Service file";
                            b.setServiceType("GENERIC");
                            if (bkg instanceof HotelBooking) {
                                desc = "Hotel service";
                                b.setServiceType("HOTEL");
                            } else if (bkg instanceof TransferBooking) {
                                desc = "Transfer service";
                                b.setServiceType("TRANSFER");
                            }
                            b.setServiceDescription(desc);
                            b.setStatus((bkg.isActive()) ? "OK" : "CANCELLED");

                            rs.setMsg("Booking found.");
                        }


                    } else {
                        rs.setStatusCode(404);
                        rs.setMsg("Booking " + bookingId + " not found.");
                    }


                }
            });
        } catch (Throwable throwable) {
            rs.setStatusCode(500);
            rs.setMsg("" + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }


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
                    //a.setValue(s.getTotalNetValue());
                    String desc = "Service file";
                    b.setServiceType("GENERIC");
                    if (s instanceof TransferService) {
                        desc = "Transfer service";
                        b.setServiceType("TRANSFER");
                    }
                    b.setServiceDescription(desc);
                    b.setStatus((s.isActive()) ? "OK" : "CANCELLED");


                }

                rs.setMsg("File found.");
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

                File b = em.find(File.class, Long.parseLong(bookingId));

                b.cancel(em, b.getAudit().getModifiedBy());

                rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                rs.setStatusCode(200);
                rs.setMsg("File has been cancelled");

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


    public SearchPortfolioRS searchPortfolio(String token,
                                             String language,
                                             String query) throws Throwable {
        return new SearchPortfolioRS();
    }


    public GetLocatorRS getFromLocator(String token, String locatorid) throws Throwable {
        return new GetLocatorRS();
    }
}
