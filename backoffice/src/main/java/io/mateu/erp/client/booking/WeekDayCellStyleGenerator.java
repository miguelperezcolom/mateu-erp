package io.mateu.erp.client.booking;

import io.mateu.mdd.core.interfaces.ICellStyleGenerator;

public class WeekDayCellStyleGenerator implements ICellStyleGenerator {
    @Override
    public String getStyles(Object row, Object value) {
        String css = null;

        String s = "" + value;

        if (s.endsWith("SAT") || s.endsWith("SUN")) css = "warning";

        return css;
    }

    @Override
    public boolean isContentShown() {
        return true;
    }
}
