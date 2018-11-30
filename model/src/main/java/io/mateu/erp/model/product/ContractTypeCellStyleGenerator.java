package io.mateu.erp.model.product;

import io.mateu.mdd.core.CSS;
import io.mateu.mdd.core.interfaces.ICellStyleGenerator;

public class ContractTypeCellStyleGenerator implements ICellStyleGenerator {
    @Override
    public String getStyles(Object row, Object value) {
        return (ContractType.SALE.equals(value))?CSS.SALE:CSS.PURCHASE;
    }

    @Override
    public boolean isContentShown() {
        return false;
    }
}
