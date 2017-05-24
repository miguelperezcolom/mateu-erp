package io.mateu.erp.model.mdd;

import io.mateu.erp.model.booking.ServiceConfirmationStatus;
import io.mateu.erp.model.booking.ValidationStatus;
import io.mateu.ui.core.shared.CellStyleGenerator;

/**
 * Created by miguel on 24/4/17.
 */
public class ValidCellStyleGenerator implements CellStyleGenerator {
    @Override
    public String getStyle(Object o) {
        String s = null;
        if (o != null && o instanceof ValidationStatus){
            s = "info";
            switch ((ValidationStatus)o) {
                case WARNING: s = "warning"; break;
                case VALID: s = "success"; break;
                case INVALID: s = "danger"; break;
            }
        }
        return s;
    }
}
