package io.mateu.erp.model.product.hotel;

import com.vaadin.data.provider.QuerySortOrder;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Tab;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.interfaces.RpcCrudView;
import io.mateu.mdd.core.reflection.ReflectionHelper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Getter@Setter
public class StopSalesView implements RpcCrudView<StopSalesView, StopSalesMonth, StopSalesLine> {


    // caché del resultado
    private static ThreadLocal<List<StopSalesMonth>> result = new ThreadLocal<>();



    @NotNull
    private Hotel hotel;

    private RoomType room;

    private Agency actor;

    private HotelContract contract;


    @Action("Enter stop sales")
    public void add(EntityManager em,
                    @Tab("General") @NotNull StopSalesAction action,
                    @NotNull LocalDate start,
                    @NotNull LocalDate end,
                    @Tab("Rooms") List<RoomType> rooms,
                    @Tab("Actors") List<Agency> agencies) throws Throwable {

        StopSalesOperation o = new StopSalesOperation();
        o.setStopSales(getHotel().getStopSales());
        em.persist(o);
        o.setCreated(LocalDateTime.now());
        o.setCreatedBy(em.find(io.mateu.erp.model.authentication.User.class, MDD.getUserData().getLogin()));
        o.setAction(action);
        o.getAgencies().addAll(agencies);
        o.getRooms().addAll(rooms);
        o.setStart(start);
        o.setEnd(end);
        o.setOnNormalInventory(true);
        o.setOnSecurityInventory(true);

        getHotel().getStopSales().build(em);

    }


    @Override
    public Object deserializeId(String s) {
        return null;
    }

    @Override
    public boolean isAddEnabled() {
        return false;
    }

    @Override
    public List<StopSalesMonth> rpc(StopSalesView filters, List<QuerySortOrder> sortOrders, int offset, int limit) throws Throwable {
        LocalDate desde = LocalDate.now();
        LocalDate hasta = LocalDate.of(desde.getYear(), desde.getMonthValue(), 1);
        hasta = LocalDate.of(hasta.getYear(), hasta.getMonth(), hasta.getDayOfMonth()).plusMonths(1).minusDays(1);

        Map<LocalDate, StopSalesLine> m = new HashMap<>();

        for (StopSalesLine l : getHotel().getStopSales().getLines()) if (!l.getEnd().isBefore(desde)) {
            for (LocalDate d = l.getStart(); !d.isAfter(l.getEnd()); d = d.plusDays(1)) if (!d.isBefore(desde)) {

                boolean incluir = true;

                if (getRoom() != null && l.getRooms().size() > 0 && !l.getRooms().contains(getRoom())) incluir = false;
                if (getActor() != null && l.getAgencies().size() > 0 && !l.getAgencies().contains(getActor())) incluir = false;
                if (getContract() != null && l.getContracts().size() > 0 && !l.getContracts().contains(getContract())) incluir = false;

                if (incluir) m.put(d, l);

                if (d.isAfter(hasta)) hasta = d;

            }
        }




        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM");

        List<StopSalesMonth> list = new ArrayList<>();

        int mes = -1;
        LocalDate hoy = LocalDate.now();
        StopSalesMonth data = null;

        for (LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) {
            if (mes != d.getMonthValue()) {
                list.add(data = new StopSalesMonth());
                data.setYear(d.getYear());
                data.setMonth(d.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
                data.setMonthValue(d.getMonthValue());
                mes = d.getMonthValue();
            }

            Data dx = new Data();
            dx.set("_text", "" + d.getDayOfMonth());
            DayClosingStatus s = DayClosingStatus.OPEN;
            StopSalesLine l = m.get(d);
            if (l != null) {
                s = DayClosingStatus.CLOSED;
                if (l.getRooms().size() > 0) s = DayClosingStatus.PARTIAL;
                dx.set("_id", l.getId());
            }
            String css = "o-open";
            if (DayClosingStatus.CLOSED.equals(s)) css = "o-closed";
            if (DayClosingStatus.PARTIAL.equals(s)) css = "o-partial";

            if (d.equals(hoy)) css += " o-today";
            else if (DayOfWeek.SATURDAY.equals(d.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(d.getDayOfWeek())) css += " o-weekend";

            dx.set("_css", css);
            try {
                ReflectionHelper.setValue("day_" + d.getDayOfMonth(), data, dx);
            } catch (Exception e) {
                MDD.alert(e);
            }
        }


        result.set(list);

        return list;
    }

    @Override
    public int gatherCount(StopSalesView filters) {
        int count = 0;
        if (result.get() != null) count = result.get().size();
        return count;
    }
}
