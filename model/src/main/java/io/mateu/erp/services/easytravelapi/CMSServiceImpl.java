package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Splitter;
import com.google.common.io.BaseEncoding;
import io.mateu.erp.dispo.DispoRQ;
import io.mateu.erp.dispo.HotelAvailabilityRunner;
import io.mateu.erp.dispo.ModeloDispo;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.world.City;
import io.mateu.erp.model.world.State;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import org.easytravelapi.CMSService;
import org.easytravelapi.cms.GetHotelAvailabilityCalendarRS;
import org.easytravelapi.cms.HotelAvailabilityCalendarDay;
import org.easytravelapi.cms.HotelAvailabilityCalendarMonth;
import org.easytravelapi.cms.HotelAvailabilityCalendarWeek;
import org.easytravelapi.common.CancelBookingRS;
import org.easytravelapi.hotel.AvailableHotel;
import org.easytravelapi.hotel.GetAvailableHotelsRS;
import org.easytravelapi.hotel.Occupancy;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CMSServiceImpl implements CMSService {
    @Override
    public GetHotelAvailabilityCalendarRS getHotelAvailabilityCalendar(String token, String resorts, int checkIn, int checkout, String occupancies) throws Throwable {
        final GetHotelAvailabilityCalendarRS rs = new GetHotelAvailabilityCalendarRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        long idPos = Long.parseLong(System.getProperty("idpos", "1"));

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

        if (idsHoteles.size() > 1) throw new Exception("You can only ask for the availability calendar for 1 hotel.");

        final int[] diasConDIsponibilidad = {0};

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

                List<? extends Occupancy> ocs = HotelBookingServiceImpl.getOccupancies(occupancies);


                LocalDate checkInLocalDate = io.mateu.erp.dispo.Helper.toDate(checkIn);

                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");


                for (Hotel h : hoteles) { // solo hay 1 hotel
                    for (int posmes = 0; posmes < 2; posmes++) {
                        int mesActual = checkInLocalDate.getMonthValue() + posmes;
                        LocalDate d = LocalDate.of(checkInLocalDate.getYear(), mesActual, 1);

                        HotelAvailabilityCalendarMonth cm;
                        rs.getMonths().add(cm = new HotelAvailabilityCalendarMonth(d.getYear(), d.getMonthValue()));

                        HotelAvailabilityCalendarWeek cw = null;
                        int semanaActual = -1;
                        while (d.getMonthValue() == mesActual) {

                            if (cw == null || d.getDayOfWeek() == DayOfWeek.MONDAY) {
                                cm.getWeeks().add(cw = new HotelAvailabilityCalendarWeek());
                            }

                            int nd = io.mateu.erp.dispo.Helper.toInt(d);

                            HotelAvailabilityCalendarDay cd;

                            cw.getDays().add(cd = new HotelAvailabilityCalendarDay(nd, d.format(df), "na"));

                            DispoRQ rq = new DispoRQ(nd, nd, ocs, false);
                            AvailableHotel ah = new HotelAvailabilityRunner().check(a, h, finalIdAgencia, idPos, modelo, rq, true);
                            if (ah != null) {
                                cd.setStyleName("av");
                                diasConDIsponibilidad[0]++;
                            }
                            d = d.plusDays(1);
                        }
                    }
                }


                //System.out.println(Helper.toJson(dispo));

            }
        });

        long t = System.currentTimeMillis();

        String msg = "Calendar shows " + diasConDIsponibilidad[0] + " available days. It consumed " + (t - t0) + " ms in the server.";

        System.out.println(msg);

        rs.setMsg(msg);

        return rs;
    }
}
