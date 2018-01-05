package io.mateu.erp.model.product.hotel;

import io.mateu.ui.mdd.server.annotations.FullWidth;
import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.Output;
import io.mateu.ui.mdd.server.annotations.ShowAsHtml;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(name = "HotelStopSales")
@Getter
@Setter
public class StopSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @Output
    private Hotel hotel;

    @Ignored
    @OneToMany(mappedBy = "stopSales", cascade = CascadeType.ALL)
    private List<StopSalesLine> lines = new ArrayList<>();


    @Ignored
    @OneToMany(mappedBy = "stopSales")
    @OrderBy("created")
    private List<StopSalesOperation> operations = new ArrayList<>();


    @ShowAsHtml("Calendar")
    @FullWidth
    public String getHtmlCalendar() {
        StringBuffer sb = new StringBuffer();

        LocalDate desde = LocalDate.now();
        desde = LocalDate.of(desde.getYear(), desde.getMonthValue(), 1);
        LocalDate hasta = LocalDate.of(desde.getYear(), desde.getMonth(), desde.getDayOfMonth()).plusMonths(1).minusDays(1);

        Map<LocalDate, DayClosingStatus> m = new HashMap<>();

        for (StopSalesLine l : getLines()) if (!l.getEnd().isBefore(desde)) {
            for (LocalDate d = l.getStart(); !d.isAfter(l.getEnd()); d = d.plusDays(1)) if (!d.isBefore(desde)) {
                DayClosingStatus s = DayClosingStatus.CLOSED;
                if (l.getRooms().size() > 0 || l.getActors().size() > 0) s = DayClosingStatus.PARTIAL;
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
            String c = "width: 20px;";
            DayClosingStatus v = m.get(d);
            String s = "";
            if (DayClosingStatus.OPEN.equals(v)) c += "o-open";
            else if (DayClosingStatus.CLOSED.equals(v)) c += "o-closed";
            else if (DayClosingStatus.PARTIAL.equals(v)) c += "o-partial";
            sb.append("<td style='" + c + "'><div class='" + s + "'>" + d.getDayOfMonth() + "</div></td>");
        }
        if (mes != - 1) sb.append("</tr>");

        sb.append("</table>");


        return sb.toString();
    }


    public void build(EntityManager em) {

        //creamos la estructura

        StopSalesCube cube = new StopSalesCube(this);


        // grabamos la estructura

        cube.save(em);

    }

}
