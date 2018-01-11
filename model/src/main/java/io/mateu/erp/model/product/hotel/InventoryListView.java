package io.mateu.erp.model.product.hotel;

import io.mateu.ui.mdd.server.interfaces.CompositeView;

public class InventoryListView implements CompositeView<Inventory, InventoryView> {

    @Override
    public String getParams() {
        return "hotel, name";
    }

    @Override
    public String getCols() {
        return "hotel, name";
    }

    @Override
    public String getOrderCriteria() {
        return "x.hotel.name asc, x.name asc";
    }
}
