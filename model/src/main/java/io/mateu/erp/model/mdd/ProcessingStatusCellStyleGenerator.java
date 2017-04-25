package io.mateu.erp.model.mdd;

import io.mateu.erp.model.booking.ProcessingStatus;
import io.mateu.ui.core.shared.CellStyleGenerator;

/**
 * Created by miguel on 24/4/17.
 */
public class ProcessingStatusCellStyleGenerator implements CellStyleGenerator {
    @Override
    public String getStyle(Object o) {
        String s = null;
        if (o != null) {
            ProcessingStatus v = (ProcessingStatus) o;
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
}
