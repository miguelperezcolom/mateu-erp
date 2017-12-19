package io.mateu.erp.model.mdd;

import io.mateu.ui.core.shared.CellStyleGenerator;

/**
 * Created by miguel on 24/4/17.
 */
public class SentCellStyleGenerator implements CellStyleGenerator {
    @Override
    public String getStyle(Object o) {
        return (o != null && o instanceof Boolean && ((Boolean)o))?"success":"info";
    }

    @Override
    public boolean isContentShown() {
        return false;
    }
}
