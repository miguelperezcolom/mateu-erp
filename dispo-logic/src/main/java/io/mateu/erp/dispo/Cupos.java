package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IInventory;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class Cupos {

    private List<Cupo> cupos = new ArrayList<>();

    public Cupos(IHotel hotel, ParoDia paros, DispoRQ rq) {

        for (IInventory inv : hotel.getInventories()) {
            Cupo cupo = new Cupo(inv, paros, rq.getCheckInLocalDate(), rq.getCheckOutLocalDate(), rq.getTotalNights());
            if (true || cupo.getResumen().getDisponible().size() > 0) { // todo: podemos ahorrar proceso si no queremos on request
                cupos.add(cupo);
            }
        }

    }
}
