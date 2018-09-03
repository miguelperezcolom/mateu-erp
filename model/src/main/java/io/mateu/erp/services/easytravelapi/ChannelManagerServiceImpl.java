package io.mateu.erp.services.easytravelapi;

import org.easytravelapi.ChannelManagerService;
import org.easytravelapi.channelManager.*;
import org.easytravelapi.common.Amount;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by miguel on 27/7/17.
 */
public class ChannelManagerServiceImpl implements ChannelManagerService {

    @Override
    public GetGrantedHotelsRS getGrantedHotels(String token) {
        GetGrantedHotelsRS rs = new GetGrantedHotelsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("10 hotels found");

        for (int i = 0; i < 10; i++) {
            GrantedHotel h;
            rs.getGrantedHotels().add(h = new GrantedHotel());
            h.setHotelId("hot_342" + i);
            h.setHotelName("Hotel Quonext-" + i);
            {
                RoomId r;
                h.getRoomIds().add(r = new RoomId());

                r.setId("DBL_1AD_HB");
                r.setDescription("Double room occupied by 1 adult in half board");

                r.setId("DBL_2AD_HB");
                r.setDescription("Double room occupied by 2 adults in half board");

                r.setId("DBL_2AD_FB");
                r.setDescription("Double room occupied by 2 adults in full board");

                r.setId("SUI_2AD_HB");
                r.setDescription("Suite room occupied by 2 adults in half board");

                r.setId("SUI_2AD_FB");
                r.setDescription("Suite room occupied by 2 adults in full board");

            }
        }


        return rs;
    }


    @Override
    public UpdateRS update(String token, UpdateRQ rq) {
        UpdateRS rs = new UpdateRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Done");

        return rs;
    }

    @Override
    public GetRoomingListRS getRoomingList(String token, int fromConfirmationDate, int toConfirmationDate, int fromStartDate, int toStartDate) {
        GetRoomingListRS rs = new GetRoomingListRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("120 files found.");


        String[] nombres = {"Java", "Javascript", ".Net", "Scala", "Go", "Kotlin"};

        for (int i = 0; i < 120; i++) {

            HotelBooking b;
            rs.getBookings().add(b = new HotelBooking());

            b.setBookingId("878997" + i);
            b.setCreated(LocalDateTime.of(2017, 03, 14, 23, 34, 15).format(DateTimeFormatter.ISO_DATE_TIME));
            b.setCreatedBy("MATEU");
            b.setModified(LocalDateTime.of(2017, 03, 14, 23, 34, 15).format(DateTimeFormatter.ISO_DATE_TIME));
            b.setLeadName("Sr " + nombres[i % nombres.length]);
            b.setStart("20180106");
            b.setEnd("20180112");
            b.setBookingId("9866230462GGWED76");
            Amount a;
            b.setNetValue(a = new Amount());
            a.setCurrencyIsoCode("EUR");
            a.setValue(750.42);
            b.setServiceType("HOTEL");
            b.setServiceDescription("Hotel " + nombres[i % nombres.length]);
            b.setStatus((i % 4 == 0)?"CANCELLED":"OK");

            {
                Stay s;
                b.getStays().add(s = new Stay());
                s.setStart(20180106);
                s.setEnd(20180112);
                s.setRoomId("DBL");
                s.setRoomName("Double");
                s.setBoardBasisId("HB");
                s.setBoardBasisName("Half board");
                s.setNumberOfRooms(1);
                s.setPaxPerRoom(2);
            }
        }



        return rs;
    }

    @Override
    public ConfirmServicesRS confirmServices(String token, ConfirmServicesRQ rq) {

        ConfirmServicesRS rs = new ConfirmServicesRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        rs.setMsg("Done");
        return rs;
    }

}
