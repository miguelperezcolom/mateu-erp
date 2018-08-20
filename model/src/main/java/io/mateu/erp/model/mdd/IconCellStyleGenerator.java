package io.mateu.erp.model.mdd;

import io.mateu.mdd.core.interfaces.ICellStyleGenerator;

/**
 * Created by miguel on 24/4/17.
 */
public class IconCellStyleGenerator implements ICellStyleGenerator {

    @Override
    public String getStyles(Object row, Object value) {
        return (value != null && value instanceof String)? (String) value :null;
    }

    @Override
    public boolean isContentShown() {
        return false;
    }
}
