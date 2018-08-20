package io.mateu.erp.dispo.interfaces.product;

import java.util.List;

public interface IInventory {
    public List<? extends IInventoryLine> getLines();
}
