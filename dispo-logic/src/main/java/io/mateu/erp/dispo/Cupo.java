package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IInventory;
import io.mateu.erp.dispo.interfaces.product.IInventoryLine;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter@Setter
public class Cupo {

    private long id;

    private List<CupoDia> dias = new ArrayList<>();

    private CupoDia resumen = new CupoDia();


    public Cupo(IInventory inv, ParoDia paros, LocalDate checkIn, LocalDate checkOut, int totalNights) {

        for (int i = 0; i < totalNights; i++) {
            dias.add(new CupoDia());
        }

        for (IInventoryLine l : inv.getLines()) {
            if (l.getStart().isBefore(checkOut) && l.getEnd().compareTo(checkIn) >= 0) {

                int desde = (int) DAYS.between(checkIn, l.getStart());
                if (desde < 0) desde = 0;
                int hasta = (int) DAYS.between(checkIn, l.getEnd());
                if (hasta > totalNights) hasta = totalNights;

                for (int noche = desde; noche < hasta; noche++) {
                    CupoDia p = getDias().get(noche);
                    p.getDisponible().put(l.getRoomCode(), l.getQuantity()); //todo: no suma. repensar si lo dejamos así o si vamos acumulando
                }

            }
        }

        boolean primero = true;
        for (CupoDia c : getDias()) {
            Set<String> rcodes = (primero)?c.getDisponible().keySet():resumen.getDisponible().keySet();
            List<String> badrcodes = new ArrayList<>(rcodes);
            for (String rcode : rcodes) {
                if (primero || resumen.getDisponible().containsKey(rcode)) {
                    int d = c.getDisponible().get(rcode);
                    if (d > 0) {
                        resumen.getDisponible().put(rcode, (primero)?d:Math.min(resumen.getDisponible().get(rcode), d));
                        badrcodes.remove(rcode);
                    }
                }
            }
            rcodes.removeAll(badrcodes);
            primero = false;
        }

    }

}
