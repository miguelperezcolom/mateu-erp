package io.mateu.erp.services.easytravelapi;

import org.easytravelapi.HotelBookingService;
import org.easytravelapi.common.Amount;
import org.easytravelapi.common.CancellationCost;
import org.easytravelapi.common.Remark;
import org.easytravelapi.hotel.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by miguel on 27/7/17.
 */
public class HotelBookingServiceImpl implements HotelBookingService {

    @Override
    public GetAvailableHotelsRS getAvailableHotels(String token, List<String> resorts, int checkIn, int checkout, List<Occupancy> occupancies, boolean includeStaticInfo) {
        GetAvailableHotelsRS rs = new GetAvailableHotelsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("215 hotels returned. It consumed 34 ms in the server.");


        String[] nombres = {"Java", "Javascript", ".Net", "Scala", "Go", "Kotlin"};

        for (int i = 0; i < 215; i++) {

            AvailableHotel h;
            rs.getHotels().add(h = new AvailableHotel());

            h.setHotelId("hot_" + i);
            h.setHotelName("Hotel " + nombres[i % nombres.length] + " " + i);

            h.setHotelCategoryId("4s");
            h.setHotelCategoryName("****");

            h.setLatitude("39.6359261");
            h.setLongitude("2.629556");

            {
                Option o;
                h.getOptions().add(o = new Option());
                Allocation a;
                o.getDistribution().add(a = new Allocation());
                a.setRoomId("DBL");
                a.setRoomName("Double Room");
                a.setNumberOfRooms(1);
                a.setPaxPerRoom(2);
                {
                    BoardPrice p;
                    o.getPrices().add(p = new BoardPrice());
                    p.setKey("5454646546542ECXSAEWUOIDWOEIDGWEDBWIED732732E");
                    p.setBoardBasisId("HB");
                    p.setBoardBasisName("Half board");
                    Amount n;
                    p.setNetPrice(n = new Amount());
                    n.setCurrencyIsoCode("EUR");
                    n.setValue(200.35);
                }
                {
                    BoardPrice p;
                    o.getPrices().add(p = new BoardPrice());
                    p.setKey("87893723idcyw8723879NKHDKBQEWBDEQW92394023DWFEW");
                    p.setBoardBasisId("FB");
                    p.setBoardBasisName("Full board");
                    Amount n;
                    p.setNetPrice(n = new Amount());
                    n.setCurrencyIsoCode("EUR");
                    n.setValue(500.15);
                    p.setOffer(true);
                    p.setOfferText("SPECIAL OFFER -30%");
                    p.setNonRefundable(true);
                }
            }

            {
                Option o;
                h.getOptions().add(o = new Option());
                Allocation a;
                o.getDistribution().add(a = new Allocation());
                a.setRoomId("SUI");
                a.setRoomName("Suite");
                a.setNumberOfRooms(1);
                a.setPaxPerRoom(2);
                {
                    BoardPrice p;
                    o.getPrices().add(p = new BoardPrice());
                    p.setKey("IGE7FT8473RG324RGBWDEGFL3WGF817FIUERQFVLEFQLFBLFY7747");
                    p.setBoardBasisId("HB");
                    p.setBoardBasisName("Half board");
                    Amount n;
                    p.setNetPrice(n = new Amount());
                    n.setCurrencyIsoCode("EUR");
                    n.setValue(850);
                }
                {
                    BoardPrice p;
                    o.getPrices().add(p = new BoardPrice());
                    p.setKey("WIYUFGLIWEFWIWHWWWEHQEURFPEY9Y4Q33HF9P9FH934HFH3F9ÑFRERF");
                    p.setBoardBasisId("FB");
                    p.setBoardBasisName("Full board");
                    Amount n;
                    p.setNetPrice(n = new Amount());
                    n.setCurrencyIsoCode("EUR");
                    n.setValue(1240.2);
                }
            }


        }


        return rs;
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
