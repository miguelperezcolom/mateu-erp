package io.mateu.erp.model.booking.hotel;

import io.mateu.ui.core.shared.CellStyleGenerator;

public class BestDealCellStyleGenerator implements CellStyleGenerator {
    @Override
    public String getStyle(Object o) {
        return "notavailable".equalsIgnoreCase("" + o)?"danger":null;
    }

    @Override
    public boolean isContentShown() {
        return true;
    }
}
