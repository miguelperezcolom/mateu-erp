package io.mateu.erp.model.booking.hotel;

import io.mateu.mdd.core.interfaces.ICellStyleGenerator;

public class BestDealCellStyleGenerator implements ICellStyleGenerator {


    @Override
    public String getStyles(Object row, Object value) {
        return "notavailable".equalsIgnoreCase("" + value)?"danger":null;
    }

    @Override
    public boolean isContentShown() {
        return true;
    }
}
