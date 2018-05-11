package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.ui.core.client.views.RPCView;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.GridData;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.Action;
import io.mateu.ui.mdd.server.annotations.Parameter;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Getter@Setter
public class StopSalesView implements RPCView<StopSalesMonth, StopSalesLine> {

    @NotNull
    private Hotel hotel;

    private RoomType room;

    private Actor actor;

    private HotelContract contract;


    @Override
    public GridData rpc() throws Throwable {

        LocalDate desde = LocalDate.now();
        LocalDate hasta = LocalDate.of(desde.getYear(), desde.getMonthValue(), 1);
        hasta = LocalDate.of(hasta.getYear(), hasta.getMonth(), hasta.getDayOfMonth()).plusMonths(1).minusDays(1);

        Map<LocalDate, StopSalesLine> m = new HashMap<>();

        for (StopSalesLine l : getHotel().getStopSales().getLines()) if (!l.getEnd().isBefore(desde)) {
            for (LocalDate d = l.getStart(); !d.isAfter(l.getEnd()); d = d.plusDays(1)) if (!d.isBefore(desde)) {

                boolean incluir = true;

                if (getRoom() != null && l.getRooms().size() > 0 && !l.getRooms().contains(getRoom())) incluir = false;
                if (getActor() != null && l.getActors().size() > 0 && !l.getActors().contains(getActor())) incluir = false;
                if (getContract() != null && l.getContracts().size() > 0 && !l.getContracts().contains(getContract())) incluir = false;

                if (incluir) m.put(d, l);

                if (d.isAfter(hasta)) hasta = d;

            }
        }



        GridData gd = new GridData();

        int mes = -1;

        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM");

        Data data = null;

        LocalDate hoy = LocalDate.now();

        for (LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) {
            if (mes != d.getMonthValue()) {
                gd.getData().add(data = new Data());
                data.set("year", d.getYear());
                data.set("month", d.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
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
            data.set("day_" + d.getDayOfMonth(), dx);
        }

        gd.setOffset(0);
        gd.setTotalLength(gd.getData().size());

        return gd;
    }


    @Action(name = "Enter stop sales")
    public void add(EntityManager em, UserData user,
                    @Tab("General") @Parameter(name = "Action") @NotNull StopSalesAction action,
                    @Parameter(name = "Start") @NotNull LocalDate start,
                    @Parameter(name = "End") @NotNull LocalDate end,
                    @Tab("Rooms") @Parameter(name = "Rooms") List<RoomType> rooms,
                    @Tab("Actors") @Parameter(name = "Actors") List<Actor> actors,
                    @Tab("Contracts") @Parameter(name = "Contracts") List<HotelContract> contracts) throws Throwable {

        StopSalesOperation o;
        getHotel().getStopSales().getOperations().add(o = new StopSalesOperation());
        em.persist(o);
        o.setCreated(LocalDateTime.now());
        o.setCreatedBy(em.find(io.mateu.erp.model.authentication.User.class, user.getLogin()));
        o.setAction(action);
        o.getActors().addAll(actors);
        o.getRooms().addAll(rooms);
        o.getContracts().addAll(contracts);
        o.setStart(start);
        o.setEnd(end);
        o.setOnNormalInventory(true);
        o.setOnSecurityInventory(true);

        getHotel().getStopSales().build(em);

    }



}
