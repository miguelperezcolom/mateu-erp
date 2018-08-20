package io.mateu.erp.client.booking;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class DayServiceStatus {

    private String text;
    private String css;
    private int pax;
    private int minEstadoProceso;
    private int maxEstadoValidacion;

    public DayServiceStatus(Object[] l, int col) {
        pax = (int) l[col++];
        minEstadoProceso = (int) l[col++];
        maxEstadoValidacion = (int) l[col++];
        text = "" + pax;
    }

}
