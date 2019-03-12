package io.mateu.erp.model.product.hotel;

import com.vaadin.icons.VaadinIcons;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.util.Helper;
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

@Entity(name = "HotelStopSales")
@Getter
@Setter
@NewNotAllowed@Indelible
public class StopSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    @Output
    @NoChart
    @MainSearchFilter
    private Hotel hotel;

    @Ignored
    @OneToMany(mappedBy = "stopSales", cascade = CascadeType.ALL)
    private List<StopSalesLine> lines = new ArrayList<>();


    @Output
    @FullWidth
    private transient String calendar;

    public String getCalendar() {
        StringBuffer sb = new StringBuffer();

        LocalDate desde = LocalDate.now();
        desde = LocalDate.of(desde.getYear(), desde.getMonthValue(), 1);
        LocalDate hasta = LocalDate.of(desde.getYear(), desde.getMonth(), desde.getDayOfMonth()).plusMonths(1).minusDays(1);

        Map<LocalDate, DayClosingStatus> m = new HashMap<>();

        for (StopSalesLine l : getLines()) if (!l.getEnd().isBefore(desde)) {
            for (LocalDate d = l.getStart(); !d.isAfter(l.getEnd()); d = d.plusDays(1)) if (!d.isBefore(desde)) {
                DayClosingStatus s = DayClosingStatus.CLOSED;
                if (l.getRooms().size() > 0 || l.getAgencies().size() > 0) s = DayClosingStatus.PARTIAL;
                m.put(d, s);
                if (d.isAfter(hasta)) hasta = d;
            }
        }

        sb.append("<table style='font-family: arial;'>");

        int mes = -1;

        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM");

        for (LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) {
            if (mes != d.getMonthValue()) {
                if (mes != - 1) sb.append("</tr>");
                sb.append("<tr><td style='width:120px;'>" + d.format(f) + "</td>");
                mes = d.getMonthValue();
            }
            String s = "width: 20px;";
            DayClosingStatus v = m.get(d);
            String c = "";
            if (DayClosingStatus.OPEN.equals(v)) c += "o-open";
            else if (DayClosingStatus.CLOSED.equals(v)) c += "o-closed";
            else if (DayClosingStatus.PARTIAL.equals(v)) c += "o-partial";
            sb.append("<td style='" + s + "'><div class='" + c + "'>" + d.getDayOfMonth() + "</div></td>");
        }
        if (mes != - 1) sb.append("</tr>");

        sb.append("</table>");


        sb.append("<p>" + getLines().size() + " lines.</p>");

        return sb.toString();
    }


    public void build(EntityManager em) throws Throwable {

        //creamos la estructura

        StopSalesCube cube = new StopSalesCube(this);


        // grabamos la estructura

        cube.save(em);

    }

    @Override
    public String toString() {
        return (getHotel() != null)?getHotel().getName():"No hotel";
    }

    @Action(order = 1, style = "success")
    public StopSalesOperation openOrClose() {
        StopSalesOperation o = new StopSalesOperation();
        o.setStopSales(this);
        return o;
    }

    @Action(order = 2)
    public void rebuild() throws Throwable {
        Helper.transact(em -> em.find(StopSales.class, getId()).build(em));
    }

    @Action(icon = VaadinIcons.CALENDAR, order = 3)
    public StopSalesCalendar calendar() {
        return new StopSalesCalendar(getHotel());
    }
}
