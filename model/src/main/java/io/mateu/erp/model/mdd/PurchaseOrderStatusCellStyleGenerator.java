package io.mateu.erp.model.mdd;

import io.mateu.erp.model.booking.PurchaseOrderStatus;
import io.mateu.ui.core.shared.CellStyleGenerator;

/**
 * Created by miguel on 24/4/17.
 */
public class PurchaseOrderStatusCellStyleGenerator implements CellStyleGenerator {
    @Override
    public String getStyle(Object o) {
        String s = null;
        if (o != null) {
            PurchaseOrderStatus v = (PurchaseOrderStatus) o;
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
