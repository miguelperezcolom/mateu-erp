package io.mateu.erp.model.mdd;

import io.mateu.erp.model.booking.ServiceConfirmationStatus;
import io.mateu.mdd.core.interfaces.ICellStyleGenerator;

/**
 * Created by miguel on 24/4/17.
 */
public class ConfirmedCellStyleGenerator implements ICellStyleGenerator {
    @Override
    public String getStyles(Object row, Object value) {
        String s = null;
        if (value != null && value instanceof ServiceConfirmationStatus){
            s = "info";
            switch ((ServiceConfirmationStatus)value) {
                case PENDING: s = "warning"; break;
                case CONFIRMED: s = "success"; break;
                case REJECTED: s = "danger"; break;
            }
        }
        return s;
    }

    @Override
    public boolean isContentShown() {
        return false;
    }
}
