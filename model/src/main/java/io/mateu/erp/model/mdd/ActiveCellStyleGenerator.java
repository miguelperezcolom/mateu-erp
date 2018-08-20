package io.mateu.erp.model.mdd;

import io.mateu.mdd.core.interfaces.ICellStyleGenerator;

/**
 * Created by miguel on 24/4/17.
 */
public class ActiveCellStyleGenerator implements ICellStyleGenerator {

    @Override
    public String getStyles(Object row, Object value) {
        return (value != null && value instanceof Boolean && ((Boolean)value))?"success":"danger";
    }

    @Override
    public boolean isContentShown() {
        return false;
    }
}
