package io.mateu.erp.model.mdd;

import io.mateu.erp.model.booking.PurchaseOrderStatus;
import io.mateu.mdd.core.interfaces.ICellStyleGenerator;

/**
 * Created by miguel on 24/4/17.
 */
public class PurchaseOrderStatusCellStyleGenerator implements ICellStyleGenerator {

    @Override
    public String getStyles(Object row, Object value) {
        String s = null;
        if (value != null) {
            PurchaseOrderStatus v = (PurchaseOrderStatus) value;
            switch (v) {
                case PENDING: s = "info"; break;
                case CONFIRMED: s = "success"; break;
                case REJECTED: s = "danger"; break;
            }
        }
        return s;
    }

    @Override
    public boolean isContentShown() {
        return true;
    }
}
