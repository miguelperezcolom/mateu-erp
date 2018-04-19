package io.mateu.erp.model.product.hotel;

import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.mdd.server.annotations.Action;
import io.mateu.ui.mdd.server.annotations.Parameter;
import io.mateu.ui.mdd.server.interfaces.CompositeView;
import io.mateu.ui.mdd.shared.MDDLink;

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

    @Action(name = "New")
    public static MDDLink createInventory(EntityManager em, @Parameter(name = "Hotel")@NotNull Hotel hotel, @Parameter(name = "Inventory name")@NotNull String name) {

        Inventory i = new Inventory();
        em.persist(i);
        i.setHotel(hotel);
        hotel.getInventories().add(i);
        i.setName(name);
        em.flush();

        return new MDDLink(InventoryLine.class, InventoryView.class, new Data("inventory", new Pair(i.getId(), i.getName())));
    }

}
