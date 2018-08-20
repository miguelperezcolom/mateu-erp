package io.mateu.erp.model.mdd;

import io.mateu.erp.model.booking.ValidationStatus;
import io.mateu.mdd.core.interfaces.ICellStyleGenerator;

/**
 * Created by miguel on 24/4/17.
 */
public class ValidCellStyleGenerator implements ICellStyleGenerator {
    @Override
    public String getStyles(Object row, Object value) {
        String s = null;
        if (value != null && value instanceof ValidationStatus){
            s = "info";
            switch ((ValidationStatus)value) {
                case WARNING: s = "warning"; break;
                case VALID: s = "success"; break;
                case INVALID: s = "danger"; break;
            }
        }
        return s;
    }

    @Override
    public boolean isContentShown() {
        return false;
    }
}
