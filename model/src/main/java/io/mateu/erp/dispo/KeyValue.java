package io.mateu.erp.dispo;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.common.Amount;
import org.easytravelapi.hotel.Allocation;
import org.easytravelapi.hotel.BoardPrice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter@Setter
public class KeyValue {

    private int checkIn;

    private int checkOut;

    private long agencyId;

    private long hotelId;

    private long pointOfSaleId;

    private long saleContractId;

    private List<Allocation> allocation = new ArrayList<>();

    private BoardPrice boardPrice;

    public KeyValue(DispoRQ rq, long agencyId, long idPos, long hotelId, long saleContractId, List<Allocation> allocation, BoardPrice boardPrice) {
        this.checkIn = rq.getCheckIn();
        this.checkOut = rq.getCheckOut();
        this.hotelId = hotelId;
        this.agencyId = agencyId;
        this.pointOfSaleId = idPos;
        this.saleContractId = saleContractId;
        this.allocation = allocation;
        this.boardPrice = boardPrice;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ci:" + getCheckIn());
        sb.append(",co:" + getCheckOut());
        sb.append(",a:" + getAgencyId());
        sb.append(",h:" + getHotelId());
        sb.append(",pos:" + getPointOfSaleId());
        sb.append(",sc:" + getSaleContractId());
        int pos = 0;
        for (Allocation a : getAllocation()) {
            //sb.append(",al" + pos++ + ":" + a.getNumberOfRooms() + "x" + a.getRoomId().replaceAll(":", "__dospuntos__").replaceAll(",", "__coma__").replaceAll("x", "__equis__") + "x" + a.getPaxPerRoom());
            if (a.getAges() != null) {
                for (int age : a.getAges()) {
                    sb.append("/" + age);
                }
            }
        }
        sb.append(",r:" + boardPrice.getBoardBasisId().replaceAll(":", "__dospuntos__").replaceAll(",", "__coma__").replaceAll("x", "__equis__"));
        sb.append(",p:" + Helper.roundEuros(boardPrice.getNetPrice().getValue()) + " " + boardPrice.getNetPrice().getCurrencyIsoCode().replaceAll(":", "__dospuntos__").replaceAll(",", "__coma__").replaceAll("x", "__equis__"));
        return BaseEncoding.base64().encode(sb.toString().getBytes(Charsets.UTF_8));
    }

    public KeyValue() {

    }

    public KeyValue(String key) {
        key = new String(BaseEncoding.base64().decode(key), Charsets.UTF_8);
        String[] ps = key.split(",");
        Map<String, String> m = new HashMap<>();
        for (String p : ps) {
            String[] x = p.split(":");
            m.put(x[0], x[1]);
        }
        setCheckIn(Integer.parseInt(m.get("ci")));
        setCheckOut(Integer.parseInt(m.get("co")));
        setAgencyId(Long.parseLong(m.get("a")));
        setHotelId(Long.parseLong(m.get("h")));
        setPointOfSaleId(Long.parseLong(m.get("pos")));
        setSaleContractId(Long.parseLong(m.get("sc")));
        boardPrice = new BoardPrice();
        boardPrice.setBoardBasisId(m.get("r").replaceAll("__dospuntos__", ":").replaceAll("__coma__", ",").replaceAll( "__equis__", "x"));
        Amount v;
        boardPrice.setNetPrice(v = new Amount());
        String[] x = m.get("p").split(" ");
        v.setValue(Double.parseDouble(x[0]));
        v.setCurrencyIsoCode(x[1].replaceAll("__dospuntos__", ":").replaceAll("__coma__", ",").replaceAll( "__equis__", "x"));
        String s = "";
        for (int pos = 0; s != null; pos++) {
            s = m.get("al" + pos);
            if (!Strings.isNullOrEmpty(s)) {
                String[] ts = s.split("x");
                Allocation a;
                getAllocation().add(a = new Allocation());
                a.setNumberOfRooms(Integer.parseInt(ts[0]));
                //a.setRoomId(ts[1].replaceAll("__dospuntos__", ":").replaceAll("__coma__", ",").replaceAll( "__equis__", "x"));
                String[] xs = ts[2].split("/");
                a.setPaxPerRoom(Integer.parseInt(xs[0]));
                if (xs.length > 1) {
                    int[] ages = new int[xs.length - 1];
                    a.setAges(ages);
                    for (int pox = 1; pox < xs.length; pox++) {
                        ages[pox - 1] = Integer.parseInt(xs[pox]);
                    }
                }
            }
        }
    }
}
