package io.mateu.erp.model.mdd;

import io.mateu.erp.model.booking.ProcessingStatus;
import io.mateu.mdd.core.interfaces.ICellStyleGenerator;

/**
 * Created by miguel on 24/4/17.
 */
public class ProcessingStatusCellStyleGenerator implements ICellStyleGenerator {

    @Override
    public String getStyles(Object row, Object value) {
        String s = null;
        if (value != null) {
            ProcessingStatus v = (ProcessingStatus) value;
            switch (v) {
                case INITIAL:
                case DATA_OK: s = "info"; break;
                case PURCHASEORDERS_SENT:
                case PURCHASEORDERS_READY: s = "warning"; break;
                case PURCHASEORDERS_CONFIRMED: s = "success"; break;
                case PURCHASEORDERS_REJECTED: s = "danger"; break;
            }
        }
        return s;
    }

    @Override
    public boolean isContentShown() {
        return true;
    }
}
