package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import io.mateu.ui.core.client.views.RPCView;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.GridData;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.ERPServiceImpl;
import io.mateu.ui.mdd.server.annotations.Action;
import io.mateu.ui.mdd.server.annotations.Output;
import io.mateu.ui.mdd.server.annotations.Parameter;
import io.mateu.ui.mdd.server.annotations.Tab;
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;
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
public class InventoryView implements RPCView<InventoryMonth, InventoryLine> {

    @NotNull
    private Inventory inventory;

    private RoomType room;


    @Override
    public GridData rpc() throws Throwable {

        LocalDate desde = LocalDate.now();
        LocalDate hasta = LocalDate.of(desde.getYear(), desde.getMonthValue(), 1);
        hasta = LocalDate.of(hasta.getYear(), hasta.getMonth(), hasta.getDayOfMonth()).plusMonths(1).minusDays(1);

        Map<LocalDate, Map<RoomType, InventoryLine>> m = new HashMap<>();

        List<RoomType> rooms = new ArrayList<>();

        for (InventoryLine l : getInventory().getLines()) if (!l.getEnd().isBefore(desde)) {
            for (LocalDate d = l.getStart(); !d.isAfter(l.getEnd()); d = d.plusDays(1)) if (!d.isBefore(desde)) {

                boolean incluir = true;

                if (getRoom() != null && !l.getRoom().equals(getRoom())) incluir = false;

                if (incluir) {
                    Map<RoomType, InventoryLine> mx = m.get(d);
                    if (mx == null) {
                        m.put(d, mx = new HashMap<>());
                    }
                    mx.put(l.getRoom(), l);

                    if (!rooms.contains(l.getRoom())) rooms.add(l.getRoom());
                }

                if (d.isAfter(hasta)) hasta = d;

            }
        }



        GridData gd = new GridData();

        int mes = -1;

        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM");

        LocalDate hoy = LocalDate.now();

        Map<RoomType, Data> mz = null;

        List<Map<RoomType, Data>> mzd = new ArrayList<>();

        for (LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) {
            if (mes != d.getMonthValue()) {
                mzd.add(mz = new HashMap<>());
                mes = d.getMonthValue();
            }


            Map<RoomType, InventoryLine> mx = m.get(d);

            if (mx != null) for (RoomType r : mx.keySet()) {
                Data data = mz.get(r);
                if (data == null) {
                    mz.put(r, data = new Data());
                    data.set("year", d.getYear());
                    data.set("month", d.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
                    data.set("room", r.getName().getEs());
                }

                Data dx = new Data();
                dx.set("_text", "0");
                DayClosingStatus s = null;

                InventoryLine l = mx.get(r);
                if (l != null) {
                    s = DayClosingStatus.OPEN;
                    if (l.getQuantity() < 3) s = DayClosingStatus.PARTIAL;
                    if (l.getQuantity() < 1) s = DayClosingStatus.CLOSED;
                    dx.set("_id", l.getId());
                    dx.set("_text", "" + l.getQuantity());
                }
                String css = null;
                if (s == null) css = "";
                else if (DayClosingStatus.OPEN.equals(s)) css = "o-open";
                else if (DayClosingStatus.CLOSED.equals(s)) css = "o-closed";
                else if (DayClosingStatus.PARTIAL.equals(s)) css = "o-partial";

                if (d.equals(hoy)) css += " o-today";
                else if (DayOfWeek.SATURDAY.equals(d.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(d.getDayOfWeek())) css += " o-weekend";

                dx.set("_css", css);
                data.set("day_" + d.getDayOfMonth(), dx);

            }


        }

        for (Map<RoomType, Data> mzx : mzd) {
            for (RoomType r : rooms) if (mzx.containsKey(r)) gd.getData().add(mzx.get(r));
        }


        gd.setOffset(0);
        gd.setTotalLength(gd.getData().size());

        return gd;
    }


    @Action(name = "Enter inventory")
    public void add(EntityManager em, UserData user,
                    @Parameter(name = "Action") @NotNull InventoryAction action,
                    @Parameter(name = "Start") @NotNull LocalDate start,
                    @Parameter(name = "End") @NotNull LocalDate end,
                    @Parameter(name = "Room") @NotNull RoomType room,
                    @Parameter(name = "Nr of rooms") @NotNull int quantity
                    ) throws Throwable {

        InventoryOperation o;
        getInventory().getOperations().add(o = new InventoryOperation());
        em.persist(o);
        o.setCreated(LocalDateTime.now());
        o.setCreatedBy(em.find(User.class, user.getLogin()));
        o.setAction(action);
        o.setRoom(room);
        o.setStart(start);
        o.setEnd(end);
        o.setQuantity(quantity);

        getInventory().build(em);

    }




}
