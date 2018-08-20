package io.mateu.erp.model.product.hotel;



import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Caption;
import io.mateu.mdd.core.app.MDDLink;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.data.Pair;
import io.mateu.mdd.core.interfaces.CompositeView;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

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

    @Action("New")
    public static MDDLink createInventory(EntityManager em, @NotNull Hotel hotel, @Caption("Inventory name")@NotNull String name) {

        Inventory i = new Inventory();
        em.persist(i);
        i.setHotel(hotel);
        hotel.getInventories().add(i);
        i.setName(name);
        em.flush();

        return new MDDLink(InventoryLine.class, InventoryView.class, new Data("inventory", new Pair(i.getId(), i.getName())));
    }

}
