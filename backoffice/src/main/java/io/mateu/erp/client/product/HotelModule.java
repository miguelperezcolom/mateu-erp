package io.mateu.erp.client.product;

import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.MDDAction;
import io.mateu.ui.mdd.client.MDDMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class HotelModule extends AbstractModule {
    @Override
    public String getName() {
        return "Hotel";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Hotels", Hotel.class));

        m.add(new MDDMenu("Coding", "Categories", HotelCategory.class, "Board codes", BoardType.class, "Room codes", RoomType.class));

        m.add(new MDDAction("Rooms", Room.class));

        m.add(new MDDAction("Boards", Board.class));

        m.add(new MDDAction("Stop sales", StopSalesView.class));

        m.add(new MDDAction("Inventory", InventoryListView.class));

        m.add(new MDDAction("Contracts", HotelContract.class));

        m.add(new MDDAction("Offers", AbstractHotelOffer.class));

        return m;
    }
}
