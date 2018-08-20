package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IInventory;
import io.mateu.mdd.core.annotations.*;
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
    @Output
    @NotNull
    private Hotel hotel;

    @SearchFilter
    private String name;

    @Ignored
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<InventoryLine> lines = new ArrayList<>();

    @Ignored
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    @OrderBy("created")
    private List<InventoryOperation> operations = new ArrayList<>();

    @ShowAsHtml("Calendar")
    @FullWidth
    public String getHtmlCalendar() {
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


    public void build(EntityManager em) {

        //creamos la estructura

        InventoryCube cube = new InventoryCube(this);


        // grabamos la estructura

        cube.save(em);

    }
}
