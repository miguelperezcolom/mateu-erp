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
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter@Setter
public class InventoryView implements RPCView<InventoryMonth, InventoryLine> {

    @NotNull
    @Output
    private Inventory inventory;

    private RoomType room;


    @Override
    public GridData rpc() throws Throwable {

        LocalDate desde = LocalDate.now();
        LocalDate hasta = LocalDate.of(desde.getYear(), desde.getMonthValue(), 1);
        hasta = LocalDate.of(hasta.getYear(), hasta.getMonth(), hasta.getDayOfMonth()).plusMonths(1).minusDays(1);

        Map<LocalDate, InventoryLine> m = new HashMap<>();

        for (InventoryLine l : getInventory().getLines()) if (!l.getEnd().isBefore(desde)) {
            for (LocalDate d = l.getStart(); !d.isAfter(l.getEnd()); d = d.plusDays(1)) if (!d.isBefore(desde)) {

                boolean incluir = true;

                if (getRoom() != null && !l.getRoom().equals(getRoom())) incluir = false;

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
                data.set("month", d.getMonthValue());
                mes = d.getMonthValue();
            }
            Data dx = new Data();
            dx.set("_text", "0");
            DayClosingStatus s = null;
            InventoryLine l = m.get(d);
            if (l != null) {
                s = DayClosingStatus.OPEN;
                if (l.getQuantity() > 2) s = DayClosingStatus.PARTIAL;
                if (l.getQuantity() > 2) s = DayClosingStatus.PARTIAL;
                dx.set("_id", l.getId());
                dx.set("_text", "" + l.getQuantity());
            }
            String css = null;
            if (s == null) css = "";
            else if (DayClosingStatus.CLOSED.equals(s)) css = "o-closed";
            else if (DayClosingStatus.CLOSED.equals(s)) css = "o-closed";
            else if (DayClosingStatus.PARTIAL.equals(s)) css = "o-partial";

            if (d.equals(hoy)) css += " o-today";
            else if (DayOfWeek.SATURDAY.equals(d.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(d.getDayOfWeek())) css += " o-weekend";

            dx.set("_css", css);
            data.set("day_" + d.getDayOfMonth(), dx);
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
                    @Parameter(name = "Room") @NotNull RoomType room) throws Throwable {

        InventoryOperation o;
        getInventory().getOperations().add(o = new InventoryOperation());
        em.persist(o);
        o.setCreated(LocalDateTime.now());
        o.setCreatedBy(em.find(User.class, user.getLogin()));
        o.setAction(action);
        o.setRoom(room);
        o.setStart(start);
        o.setEnd(end);

        getInventory().build(em);

    }



}
