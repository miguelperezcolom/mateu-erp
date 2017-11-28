package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import io.mateu.erp.dispo.DispoRQ;
import io.mateu.erp.dispo.HotelAvailabilityRunner;
import io.mateu.erp.dispo.KeyValue;
import io.mateu.erp.dispo.ModeloDispo;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.hotel.Occupation;
import io.mateu.erp.model.booking.hotel.Option;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.world.City;
import io.mateu.erp.model.world.State;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import org.easytravelapi.HotelBookingService;
import org.easytravelapi.common.Amount;
import org.easytravelapi.common.CancellationCost;
import org.easytravelapi.common.Remark;
import org.easytravelapi.hotel.*;
import travel.caval._20091127.hotelbooking.AvailRQOccupation;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 27/7/17.
 */
public class HotelBookingServiceImpl implements HotelBookingService {

    @Override
    public GetAvailableHotelsRS getAvailableHotels(String token, String resorts, int checkIn, int checkout, String occupancies, boolean includeStaticInfo) throws Throwable {
        GetAvailableHotelsRS rs = new GetAvailableHotelsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        long idPos = Long.parseLong(System.getProperty("idpos", "1"));

        LocalDate formalizationDate = LocalDate.now();

        long idAgencia = 0;
        long idHotel = 0;
        String login = "";
        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(token)));
            idAgencia = Long.parseLong(creds.getAgentId());
            idHotel = Long.parseLong(creds.getHotelId());
            //rq.setLanguage(creds.getLan());
            login = creds.getLogin();
            //rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long t0 = System.currentTimeMillis();

        List<Long> idsHoteles = new ArrayList<>();

        if (idHotel > 0) idsHoteles.add(idHotel);
        else {

            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    List<State> l = new ArrayList<>();

                    for (String s : Splitter.on(',')
                            .trimResults()
                            .omitEmptyStrings()
                            .split(resorts)) {
                        l.add(em.find(State.class, Long.parseLong(s)));
                    };


                    List<Hotel> hoteles = new ArrayList<>();

                    for (State s : l) for (City c : s.getCities()) hoteles.addAll(c.getHotels());

                    System.out.println("" + hoteles.size() + " hoteles encontrados");

                    int numContratos = 0;
                    for (Hotel h : hoteles) {
                        numContratos += h.getContracts().size();
                    }

                    System.out.println("" + numContratos + " contratos encontrados");

                    for (Hotel h : hoteles) idsHoteles.add(h.getId());
                }
            });


        }

        long finalIdAgencia = idAgencia;
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = new ArrayList<>();
                for (long idHotel : idsHoteles) {
                    hoteles.add(em.find(Hotel.class, idHotel));
                }

                Actor a = em.find(Actor.class, finalIdAgencia);

                //System.out.println("" + hoteles.size() + " hoteles encontrados");

                ModeloDispo modelo = new ModeloDispo() {
                    @Override
                    public IHotelContract getHotelContract(long id) {
                        return em.find(HotelContract.class, id);
                    }
                };

                DispoRQ rq = new DispoRQ(formalizationDate, checkIn, checkout, getOccupancies(occupancies), includeStaticInfo);


                for (Hotel h : hoteles) {
                    AvailableHotel ah = new HotelAvailabilityRunner().check(a, h, finalIdAgencia, idPos, modelo, rq);
                    if (ah != null) rs.getHotels().add(ah);
                }


                //System.out.println(Helper.toJson(dispo));

            }
        });

        long t = System.currentTimeMillis();

        String msg = "" + rs.getHotels().size() + " hotels returned. It consumed " + (t - t0) + " ms in the server.";

        System.out.println(msg);

        rs.setMsg(msg);

        return rs;
    }

    public static List<? extends Occupancy> getOccupancies(String occupancies) {
        List<Occupancy> l = new ArrayList<>();
        for (String s : Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(occupancies.toLowerCase())) {

            //1x4-4-8

            //System.out.println("occ=" + s);

            Occupancy o;
            l.add(o = new Occupancy());
            o.setPaxPerRoom(Integer.parseInt(s.split("x")[1].split("-")[0]));
            o.setNumberOfRooms(Integer.parseInt(s.split("x")[0]));

            String[] sas = s.split("-");
            List<Integer> edades = new ArrayList<Integer>();
            if (sas.length > 1) {
                for (int i = 1; i < sas.length; i++) {
                    int edad = Integer.parseInt(sas[i]);
                    if (edad < 20) {
                        edades.add(edad);
                    }
                }
            }

            int[] edadesx = new int[edades.size()];
            for (int i = 0; i < edadesx.length; i++) edadesx[i] = edades.get(i);
            o.setAges(edadesx);

        }
        return l;
    }

    @Override
    public GetHotelPriceDetailsRS getHotelPriceDetails(String token, String key) {

        GetHotelPriceDetailsRS rs = new GetHotelPriceDetailsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Price details");

        long idPos = Long.parseLong(System.getProperty("idpos", "1"));

        long idAgencia = 0;
        String login = "";
        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(token)));
            idAgencia = Long.parseLong(creds.getAgentId());
            //rq.setLanguage(creds.getLan());
            login = creds.getLogin();
            //rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long t0 = System.currentTimeMillis();

        long finalIdAgencia = idAgencia;
        try {
            Helper.transact((JPATransaction) (em) -> {
                new HotelAvailabilityRunner().fillHotelPriceDetailsResponse(rs, finalIdAgencia, key, new ModeloDispo() {
                    @Override
                    public IHotelContract getHotelContract(long id) {
                        return em.find(HotelContract.class, id);
                    }
                });
            });
            long t = System.currentTimeMillis();

            String msg = "Price details. It consumed " + (t - t0) + " ms in the server.";

            System.out.println(msg);

            rs.setMsg(msg);

        } catch (Throwable throwable) {
            rs.setStatusCode(200);
            rs.setMsg("" + throwable.getClass().getName() + ":" + throwable.getMessage());
            throwable.printStackTrace();
        }




        return rs;
    }

    @Override
    public BookHotelRS bookHotel(String token, BookHotelRQ rq) {

        BookHotelRS rs = new BookHotelRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Booking confirmed");

        long idPos = Long.parseLong(System.getProperty("idpos", "1"));

        long idAgencia = 0;
        final UserData u = new UserData();
        String login = "";
        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(token)));
            idAgencia = Long.parseLong(creds.getAgentId());
            //rq.setLanguage(creds.getLan());
            login = creds.getLogin();
            u.setLogin(login);
            //rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long t0 = System.currentTimeMillis();

        long finalIdAgencia = idAgencia;
        try {

            Helper.transact((JPATransaction) (em) -> {
                rs.setBookingId("" + HotelService.createFromKey(u, new KeyValue(rq.getKey()), rq.getLeadName(), rq.getCommentsToProvider()));
            });
            long t = System.currentTimeMillis();

            String msg = "Booking confirmed with id " + rs.getBookingId() + ". It consumed " + (t - t0) + " ms in the server.";

            System.out.println(msg);

            rs.setMsg(msg);

        } catch (Throwable throwable) {
            rs.setStatusCode(500);
            rs.setMsg("" + throwable.getClass().getName() + ":" + throwable.getMessage());
            throwable.printStackTrace();
        }




        return rs;
    }

}
