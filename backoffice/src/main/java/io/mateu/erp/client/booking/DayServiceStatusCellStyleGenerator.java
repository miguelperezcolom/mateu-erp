package io.mateu.erp.client.booking;

import io.mateu.mdd.core.interfaces.ICellStyleGenerator;

public class DayServiceStatusCellStyleGenerator implements ICellStyleGenerator {
    @Override
    public String getStyles(Object row, Object value) {
        String css = "";

        DayServiceStatus s = (DayServiceStatus) value;

        if (s != null) {

            if (s.getPax() == 0) {
                css = null;
            } else {
                if (s.getMinEstadoProceso() == 450) css = "rojo";
                else if (s.getMinEstadoProceso() < 500) css = "naranja";
                else if (s.getMinEstadoProceso() >= 500) css = "verdemarino";

                css += " ";
                if (s.getMaxEstadoValidacion() == 0) css += "cell-valid";
                else if (s.getMaxEstadoValidacion() < 2) css += "cell-warning";
                else if (s.getMaxEstadoValidacion() >= 2) css += "cell-invalid";
            }

        }

        return css;
    }

    @Override
    public boolean isContentShown() {
        return true;
    }
}
