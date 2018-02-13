package io.mateu.erp.model.product.hotel;

import io.mateu.ui.mdd.server.interfaces.CompositeView;

public class InventoryListView implements CompositeView<Inventory, InventoryView> {

    @Override
    public String getParams() {
        return "hotel, name";
    }

    @Override
    public String getCols() {
        return "id, hotel.name, name";
    }

    @Override
    public String getColHeaders() {
        return "Hotel, Inventory";
    }

    @Override
    public String getOrderCriteria() {
        return "x.hotel.name asc, x.name asc";
    }

    @Override
    public String getActionName() {
        return "Open inventory";
    }
}