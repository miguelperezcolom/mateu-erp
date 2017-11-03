package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Splitter;
import com.google.common.io.BaseEncoding;
import io.mateu.erp.dispo.DispoRQ;
import io.mateu.erp.dispo.HotelAvailabilityRunner;
import io.mateu.erp.dispo.ModeloDispo;
import io.mateu.erp.model.booking.hotel.Occupation;
import io.mateu.erp.model.booking.hotel.Option;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.world.City;
import io.mateu.erp.model.world.State;
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

        List<Long> idsHoteles = new ArrayList<>();

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


        ModeloDispo modelo = new ModeloDispo() {
        };

        long finalIdAgencia = idAgencia;
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = new ArrayList<>();
                for (long idHotel : idsHoteles) {
                    hoteles.add(em.find(Hotel.class, idHotel));
                }

                //System.out.println("" + hoteles.size() + " hoteles encontrados");

                ModeloDispo modelo = new ModeloDispo() {
                };

                DispoRQ rq = new DispoRQ(checkIn, checkout, getOccupancies(occupancies), includeStaticInfo);


                for (Hotel h : hoteles) {
                    AvailableHotel ah = new HotelAvailabilityRunner().check(h, finalIdAgencia, 1, modelo, rq);
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

    private List<? extends Occupancy> getOccupancies(String occupancies) {
        List<Occupancy> l = new ArrayList<>();
        for (String s : Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(occupancies.toLowerCase())) {

            //1x4-4-8

            System.out.println("occ=" + s);

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

        {
            CancellationCost c;
            rs.getCancellationCosts().add(c = new CancellationCost());
            c.setGMTtime(LocalDateTime.of(2018, 06, 05, 12, 00).format(DateTimeFormatter.ISO_DATE_TIME));
            Amount a;
            c.setNet(a = new Amount());
            a.setCurrencyIsoCode("EUR");
            a.setValue(250.32);
        }

        {
            CancellationCost c;
            rs.getCancellationCosts().add(c = new CancellationCost());
            c.setGMTtime(LocalDateTime.of(2018, 07, 01, 12, 00).format(DateTimeFormatter.ISO_DATE_TIME));
            Amount a;
            c.setNet(a = new Amount());
            a.setCurrencyIsoCode("EUR");
            a.setValue(400);
        }

        {
            Remark r;
            rs.getRemarks().add(r = new Remark());
            r.setType("IMPORTANT");
            r.setText("This service must be paid in 24 hors. Otherwise it will be automatically cancelled and you may loose your rooms.");
        }
        {
            Remark r;
            rs.getRemarks().add(r = new Remark());
            r.setType("WARNING");
            r.setText("You will have to pay 3 euros per pax and night for the Ecotasa local tax in any hotel at Illes Balears.");
        }        {
            Remark r;
            rs.getRemarks().add(r = new Remark());
            r.setType("INFO");
            r.setText("Reception closed at night hours.");
        }

        return rs;
    }

    @Override
    public BookHotelRS bookHotel(String token, BookHotelRQ rq) {

        System.out.println("rq=" + rq);

        BookHotelRS rs = new BookHotelRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Booking confirmed ok");

        rs.setBookingId("5643135431");

        return rs;
    }

}
