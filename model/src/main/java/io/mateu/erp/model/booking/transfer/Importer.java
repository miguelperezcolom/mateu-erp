package io.mateu.erp.model.booking.transfer;

import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.ui.mdd.server.util.Helper;

import javax.persistence.EntityManager;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by miguel on 20/5/17.
 */
public class Importer {


    public static void importPickupTimes(EntityManager em, Object[][] l, PrintWriter pw) throws Throwable {
        int colref = -1;
        int colfecha = -1;
        int colhora = -1;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdh = new SimpleDateFormat("HH:mm");

        for (int fila = 0; fila < l.length; fila++) {
            int nofila = fila + 1;
            if (colref < 0 || colfecha < 0 || colhora < 0) {
                for (int col = 0; col < l[fila].length; col++) {
                    if ("po".equalsIgnoreCase("" + l[fila][col]) || "localizador".equalsIgnoreCase("" + l[fila][col])) colref = col;
                    if ("pickup date".equalsIgnoreCase("" + l[fila][col]) || "fecha servicio".equalsIgnoreCase("" + l[fila][col])) colfecha = col;
                    if ("pickup time".equalsIgnoreCase("" + l[fila][col]) || "hora recogida".equalsIgnoreCase("" + l[fila][col])) colhora = col;
                }
            } else {
                try {

                    if (l[fila].length < Helper.max(colref, colfecha, colhora)) throw new Exception("Missing columns");

                    String ref = (l[fila][colref] != null)?"" + l[fila][colref]:null;
                    Date fecha = null;
                    try {
                        fecha = (Date) l[fila][colfecha];
                    } catch (Exception e) {
                        fecha = sdf.parse((String) l[fila][colfecha]);
                    }
                    Date hora = null;
                    try {
                        hora = (Date) l[fila][colhora];
                    } catch (Exception e) {
                        hora = sdh.parse((String) l[fila][colhora]);
                    }

                    if (ref == null) pw.println("<span style='color: red;'>line " + nofila + ": missing ref</span>");
                    else if (fecha == null) pw.println("<span style='color: red;'>line " + nofila + ": missing pickup date</span>");
                    else if (hora == null) pw.println("<span style='color: red;'>line " + nofila + ": missing pickup time</span>");
                    else {

                        long id = (long) Double.parseDouble(ref.trim());
                        PurchaseOrder po = em.find(PurchaseOrder.class, id);

                        if (po != null) {

                            TransferService s = null;
                            if (po.getServices().size() > 0) s = (TransferService) po.getServices().get(0);

                            if (s != null) {
                                LocalDateTime pud = Helper.toLocalDateTime(fecha);
                                if (pud.getYear() < 1000) pud = pud.plusYears(2000);
                                LocalDateTime put = Helper.toLocalDateTime(hora);

                                s.setPickupTime(LocalDateTime.of(pud.getYear(), pud.getMonth(), pud.getDayOfMonth(), put.getHour(), put.getMinute()));

                                pw.println("line " + nofila + ": pickup time for service id " + id + " (" + s.getBooking().getLeadName() + ") setted to " + s.getPickupTime().format(dtf));
                            } else {
                                pw.println("<span style='color: red;'>line " + nofila + ": no service for purchase order with id " + ref + "</span>");
                            }

                        } else {
                            pw.println("<span style='color: red;'>line " + nofila + ": no purchase order with id " + ref + "</span>");
                        }



                    }
                } catch (Exception e) {
                    pw.println("<span style='color: red;'>line " + nofila + ": ERROR " + e.getClass().getName() + "(" + e.getMessage()+ ")</span>");
                }
                pw.println("<br/>");
            }
        }
        if (colref < 0) throw new Throwable("Missing ref col");
        if (colfecha < 0) throw new Throwable("Missing pickup date col");
        if (colhora < 0) throw new Throwable("Missing pickup time col");
        pw.println("<br/>");
        pw.println("<br/>");
        pw.println("****END OF FILE****");
        pw.println("<br/>");
        pw.println("<br/>");
    }

}
