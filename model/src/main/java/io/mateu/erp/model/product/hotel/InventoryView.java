package io.mateu.erp.model.product.hotel;

import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Caption;
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
public class InventoryView implements RpcCrudView<InventoryView, InventoryMonth, InventoryLine> {

    // caché del resultado
    private static ThreadLocal<List<InventoryMonth>> result = new ThreadLocal<>();


    @NotNull
    private Inventory inventory;

    private RoomType room;


    @Override
    public List<InventoryMonth> rpc(InventoryView filters, int offset, int limit) {

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



        List<InventoryMonth> list = new ArrayList<>();

        int mes = -1;

        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM");

        LocalDate hoy = LocalDate.now();

        Map<RoomType, InventoryMonth> mz = null;

        List<Map<RoomType, InventoryMonth>> mzd = new ArrayList<>();

        for (LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) {
            if (mes != d.getMonthValue()) {
                mzd.add(mz = new HashMap<>());
                mes = d.getMonthValue();
            }


            Map<RoomType, InventoryLine> mx = m.get(d);

            if (mx != null) for (RoomType r : mx.keySet()) {
                InventoryMonth data = mz.get(r);
                if (data == null) {
                    mz.put(r, data = new InventoryMonth());
                    data.setYear(d.getYear());

                    data.setMonth(d.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
                    data.setMonthValue(d.getMonthValue());

                    data.setRoom(r.getName().getEs());
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
                try {
                    ReflectionHelper.setValue("day_" + d.getDayOfMonth(), data, dx);
                } catch (Exception e) {
                    MDD.alert(e);
                }

            }


        }

        for (Map<RoomType, InventoryMonth> mzx : mzd) {
            for (RoomType r : rooms) if (mzx.containsKey(r)) {
                list.add(mzx.get(r));
            }
        }


        result.set(list);

        return list;
    }

    @Override
    public int gatherCount(InventoryView inventoryView) {
        int count = 0;
        if (result.get() != null) count = result.get().size();
        return count;
    }


    @Action("Enter inventory")
    public void add(EntityManager em,
                    @NotNull InventoryAction action,
                    @NotNull LocalDate start,
                    @NotNull LocalDate end,
                    @NotNull RoomType room,
                    @Caption("Nr of rooms") @NotNull int quantity
                    ) throws Throwable {

        InventoryOperation o = new InventoryOperation();
        em.persist(o);
        o.setInventory(getInventory());
        o.setCreated(LocalDateTime.now());
        o.setCreatedBy(em.find(io.mateu.erp.model.authentication.User.class, MDD.getUserData().getLogin()));
        o.setAction(action);
        o.setRoom(room);
        o.setStart(start);
        o.setEnd(end);
        o.setQuantity(quantity);

        getInventory().build(em);

    }


    @Override
    public Object deserializeId(String s) {
        return null;
    }

    @Override
    public boolean isAddEnabled() {
        return false;
    }
}
