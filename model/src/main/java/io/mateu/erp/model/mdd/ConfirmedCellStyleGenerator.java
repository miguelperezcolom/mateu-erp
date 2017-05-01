package io.mateu.erp.model.mdd;

import io.mateu.erp.model.booking.ServiceConfirmationStatus;
import io.mateu.ui.core.shared.CellStyleGenerator;

/**
 * Created by miguel on 24/4/17.
 */
public class ConfirmedCellStyleGenerator implements CellStyleGenerator {
    @Override
    public String getStyle(Object o) {
        String s = null;
        if (o != null && o instanceof ServiceConfirmationStatus){
            s = "info";
            switch ((ServiceConfirmationStatus)o) {
                case PENDING: s = "warning"; break;
                case CONFIRMED: s = "success"; break;
                case REJECTED: s = "danger"; break;
            }
        }
        return s;
    }
}
