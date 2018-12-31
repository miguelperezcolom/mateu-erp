package io.mateu.erp.services.easytravelapi;

import org.easytravelapi.ActivityBookingService;
import org.easytravelapi.activity.*;
import org.easytravelapi.common.Amount;
import org.easytravelapi.common.CancellationCost;
import org.easytravelapi.common.GetPortfolioRS;
import org.easytravelapi.common.Remark;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by miguel on 27/7/17.
 */
public class ActivityBookingServiceImpl implements ActivityBookingService {


    @Override
    public GetAvailableActivitiesRS getAvailableActivities(String token, int start, String resourceId, String language) {
        GetAvailableActivitiesRS rs = new GetAvailableActivitiesRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("3 activities found. It consumed 24 ms in the server.");


        {
            AvailableActivity a;
            rs.getAvailableActivities().add(a = new AvailableActivity());

            a.setActivityId("act-798789");
            a.setName("Vuelta a Mallorca");
            a.setDescription("Excursión muy interesante para ver los principales puntos de interés de la isla. Muy recomendada!");
            a.setImage("http://www.click-mallorca.com/imgdb/mapa_are1945.jpg");
            /*
            {
                ActivityDate d;
                a.getAvailableDates().add(d = new ActivityDate());
                d.setKey("berfgyerfer9634r34rg43lefr");
                d.setDate(20180615);
                d.setTime(915);
                Amount p;
                d.setRetailPrice(p = new Amount());
                p.setCurrencyIsoCode("EUR");
                p.setValue(200.01);
            }
            {
                ActivityDate d;
                a.getAvailableDates().add(d = new ActivityDate());
                d.setKey("berfgyerfer9634r34rg43lefr");
                d.setDate(20180615);
                d.setTime(1015);
                Amount p;
                d.setRetailPrice(p = new Amount());
                p.setCurrencyIsoCode("EUR");
                p.setValue(200.01);
            }
            {
                ActivityDate d;
                a.getAvailableDates().add(d = new ActivityDate());
                d.setKey("berfgyerfer9634r34rg43lefr");
                d.setDate(20180615);
                d.setTime(1115);
                Amount p;
                d.setRetailPrice(p = new Amount());
                p.setCurrencyIsoCode("EUR");
                p.setValue(200.01);
            }
            */

        }

        {
            AvailableActivity a;
            rs.getAvailableActivities().add(a = new AvailableActivity());

            a.setActivityId("act-79878e9");
            a.setName("Buceo en Cala Millor");
            a.setDescription("Excursión muy interesante para ver los principales puntos de interés de la isla. Muy recomendada!");
            a.setImage("http://www.click-mallorca.com/imgdb/imagen_listado_exc10309.png");
            /*
            {
                ActivityDate d;
                a.getAvailableDates().add(d = new ActivityDate());
                d.setKey("berfgyerfer9634der34rg43lefr");
                d.setDate(20180615);
                Amount p;
                d.setRetailPrice(p = new Amount());
                p.setCurrencyIsoCode("EUR");
                p.setValue(250.4);
            }
            {
                ActivityDate d;
                a.getAvailableDates().add(d = new ActivityDate());
                d.setKey("berfgyerfer9634der34rg43lefr");
                d.setDate(20180616);
                Amount p;
                d.setRetailPrice(p = new Amount());
                p.setCurrencyIsoCode("EUR");
                p.setValue(250.4);
            }
            {
                ActivityDate d;
                a.getAvailableDates().add(d = new ActivityDate());
                d.setKey("berfgyerfer9634der34rg43lefr");
                d.setDate(20180617);
                Amount p;
                d.setRetailPrice(p = new Amount());
                p.setCurrencyIsoCode("EUR");
                p.setValue(250.4);
            }
            */
        }


        {
            AvailableActivity a;
            rs.getAvailableActivities().add(a = new AvailableActivity());

            a.setActivityId("act-7987a89");
            a.setName("Aventuras en Jungle Park");
            a.setDescription("Excursión muy interesante para ver los principales puntos de interés de la isla. Muy recomendada!");
            a.setImage("http://www.click-mallorca.com/imgdb/imagen_listado_exc9234.png");
            /*
            {
                ActivityDate d;
                a.getAvailableDates().add(d = new ActivityDate());
                d.setKey("berfgyerfer9634r34rg433563lefr");
                d.setDate(20180615);
                Amount p;
                d.setRetailPrice(p = new Amount());
                p.setCurrencyIsoCode("EUR");
                p.setValue(100);
            }
            */
        }


        return rs;
    }

    @Override
    public GetActivityPriceDetailsRS getActivityPriceDetails(String token, String key, String language, int adults, int children, int vehicles, String supplements, String coupon) {
        GetActivityPriceDetailsRS rs = new GetActivityPriceDetailsRS();

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
            r.setText("This service must be paid in 24 hors. Otherwise it will be automatically cancelled.");
        }
        {
            Remark r;
            rs.getRemarks().add(r = new Remark());
            r.setType("WARNING");
            r.setText("You must present the voucher that you will receive by email, after payment.");
        }        {
            Remark r;
            rs.getRemarks().add(r = new Remark());
            r.setType("INFO");
            r.setText("Have a nice day");
        }

        return rs;
    }

    @Override
    public BookActivityRS bookActivity(String token, BookActivityRQ rq) {
        System.out.println("rq=" + rq);

        BookActivityRS rs = new BookActivityRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("File confirmed ok");

        rs.setBookingId("5643135431");


        return rs;
    }


    @Override
    public GetPortfolioRS getPortfolio(String token) throws Throwable {
        return null;
    }


    @Override
    public GetActivityPriceRS getExcursionPrice(String token, String key, String language, int adults, int children, int infants, String datekey, String variantkeykey, String shiftdate, String pickup, String supplements) throws Throwable {
        return null;
    }

    @Override
    public GetActivityRatesRS getActivityRates(String token, String key, String language) throws Throwable {
        return null;
    }

    @Override
    public GetAvailableActivitiesRS getFilteredActivities(String token, int start, String resourceId, String language, String minPrice, String maxPrice) throws Throwable {
        return null;
    }
}
