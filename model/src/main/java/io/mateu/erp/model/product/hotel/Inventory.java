package io.mateu.erp.model.product.hotel;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.dispo.interfaces.product.IInventory;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class Inventory implements IInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @SearchFilter
    @ManyToOne
    @NotNull
    @Unmodifiable
    @NoChart
    private Hotel hotel;

    @SearchFilter
    private String name;


    @ManyToOne
    private Inventory substractFrom;


    @DependsOn("hotel")
    public ListDataProvider<Inventory> getSubstractFromDataProvider() {
        List<Inventory> is = new ArrayList<>();
        if (hotel != null) {
            is.addAll(hotel.getInventories());
            is.remove(this);
        }
        return new ListDataProvider<Inventory>(is);
    }


    @Ignored
    @OneToMany(mappedBy = "substractFrom")
    private List<Inventory> dependantInventories = new ArrayList<>();


    private int returnRelease;


    @Ignored
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<InventoryLine> lines = new ArrayList<>();

    @Ignored
    @OneToMany(mappedBy = "inventory")
    private List<HotelContract> contracts = new ArrayList<>();

    @Ignored
    @OneToMany(mappedBy = "securityInventory")
    private List<HotelContract> securityContracts = new ArrayList<>();

    @Ignored
    @OneToMany(mappedBy = "inventory")
    private List<HotelBookingLine> bookings = new ArrayList<>();



    @Ignored
    private boolean updatePending = false;


    /*
    @FullWidth
    @Output
    private transient String calendar;
    */

    public String getCalendar() {
        StringBuffer sb = new StringBuffer();

        LocalDate desde = LocalDate.now();
        desde = LocalDate.of(desde.getYear(), desde.getMonthValue(), 1);
        LocalDate hasta = LocalDate.of(desde.getYear(), desde.getMonth(), desde.getDayOfMonth()).plusMonths(1).minusDays(1);

        Map<LocalDate, Map<RoomType, Integer>> m = new HashMap<>();
        List<RoomType> rooms = new ArrayList<>();

        for (InventoryLine l : getLines()) if (!l.getEnd().isBefore(desde)) {
            for (LocalDate d = l.getStart(); !d.isAfter(l.getEnd()); d = d.plusDays(1)) if (!d.isBefore(desde)) {
                Map<RoomType, Integer> s = m.get(d);
                if (s == null) {
                    s = new HashMap<>();
                    m.put(d, s);
                }

                int q = l.getQuantity();
                if (s.containsKey(l.getRoom())) {
                    q += s.get(l.getRoom());
                }
                s.put(l.getRoom(), q);

                if (!rooms.contains(l.getRoom())) rooms.add(l.getRoom());

                if (d.isAfter(hasta)) hasta = d;
            }
        }

        sb.append("<table style='font-family: arial;'>");

        int mes = -1;

        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM");

        int posRoom = 0;
        for (LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) {
            if (mes != d.getMonthValue()) {
                if (mes != - 1) {
                    sb.append("</tr>");
                    if (rooms.size() > 0) {
                        if (posRoom == rooms.size() - 1) {
                            posRoom = 0;
                        } else {
                            posRoom++;
                            d = LocalDate.of((d.getMonthValue() > 1)?d.getYear():d.getYear() - 1, (d.getMonthValue() > 1)?d.getMonthValue() - 1:12, 1);
                        }
                    }
                }
                sb.append("<tr><td style='width:120px;'>" + d.format(f) + "</td>");

                mes = d.getMonthValue();
                sb.append("<td style='width:200px;'>" + ((rooms.size() > 0)?acortar(rooms.get(posRoom).getName().getEs()):"---") + "</td>");
            }
            String c = "width: 30px;";
            Map<RoomType, Integer> s = m.get(d);

            String v = "-";
            if (s != null && rooms.size() > 0 && s.containsKey(rooms.get(posRoom))) v = "" + s.get(rooms.get(posRoom));

            sb.append("<td style='" + c + "'>" + v + "</td>");
        }
        if (mes != - 1) sb.append("</tr>");

        sb.append("</table>");



        return sb.toString();
    }

    private String acortar(String s) {
        return (s.length() > 30)?s.substring(0, 30):s;
    }


    public void build(EntityManager em) throws Throwable {

        System.out.println("**** building inventory " + getName());

        //creamos la estructura

        InventoryCube cube = new InventoryCube(this);


        // grabamos la estructura

        cube.save(em);

    }

    public List<InventoryOperation> getOperations() throws Throwable {
        return Helper.selectObjects("select x from " + InventoryOperation.class.getName() + " x where x.inventory.id = " + getId() + " order by x.id");
    }


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (id != 0 && obj != null && obj instanceof  Inventory && id == ((Inventory)obj).getId());
    }

    @Override
    public String toString() {
        return name;
    }



    @Action(order = 1, style = "success")
    public InventoryOperation modify() {
        InventoryOperation o = new InventoryOperation();
        o.setInventory(this);
        return o;
    }

    @Action(order = 2)
    public void rebuild() throws Throwable {
        Helper.transact(em -> em.find(Inventory.class, getId()).build(em));
    }

    @Action(icon = VaadinIcons.CALENDAR, order = 3)
    public InventoryCalendar calendar() {
        return new InventoryCalendar(this);
    }


    @PostPersist@PostUpdate
    public void post() {
        if (updatePending) WorkflowEngine.add(() -> {
            try {
                if (updatePending) Helper.transact(em -> {
                    Inventory i = em.merge(this);
                    i.build(em);
                    i.setUpdatePending(false);
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
