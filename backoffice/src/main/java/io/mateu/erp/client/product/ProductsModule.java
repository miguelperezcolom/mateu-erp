package io.mateu.erp.client.product;

import io.mateu.erp.model.product.generic.Contract;
import io.mateu.erp.model.product.generic.*;
import io.mateu.erp.model.product.generic.Price;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.erp.model.product.transfer.*;
import io.mateu.ui.core.client.app.AbstractMenu;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.MDDAction;
import io.mateu.ui.mdd.client.MDDMenu;

import java.util.ArrayList;
import java.util.List;

public class ProductsModule extends AbstractModule {
    @Override
    public String getName() {
        return "Products";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(getHotelSubmenu());

        m.add(getTransferSubmenu());

        m.add(getGenericSubmenu());

        return m;
    }

    private AbstractMenu getGenericSubmenu() {
        AbstractMenu s = new AbstractMenu("Generic") {
            @Override
            public List<MenuEntry> getEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDAction("Products", Product.class));

                m.add(new MDDAction("Extras", Extra.class));

                m.add(new MDDAction("Contracts", Contract.class));

                m.add(new MDDAction("Product prices", Price.class));

                m.add(new MDDAction("Extra prices", ExtraPrice.class));

                return m;
            }
        };
        return s;
    }

    private AbstractMenu getTransferSubmenu() {
        AbstractMenu s = new AbstractMenu("Transfer") {
            @Override
            public List<MenuEntry> getEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDAction("Transfer points", TransferPoint.class));

                m.add(new MDDAction("Contracts", io.mateu.erp.model.product.transfer.Contract.class));

                m.add(new MDDAction("Prices", io.mateu.erp.model.product.transfer.Price.class));

                m.add(new MDDAction("Vehicles", Vehicle.class));

                m.add(new MDDAction("Price zones", Zone.class));

                m.add(new MDDMenu("Routes", "Routes", Route.class, "Stops", RouteStop.class, "Times", RouteTime.class));

                return m;

            }
        };
        return s;
    }

    private AbstractMenu getHotelSubmenu() {
        AbstractMenu s = new AbstractMenu("Hotel") {
            @Override
            public List<MenuEntry> getEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDAction("Hotels", Hotel.class));

                m.add(new MDDMenu("Coding", "Categories", HotelCategory.class, "Board codes", BoardType.class, "Room codes", RoomType.class));

                m.add(new MDDAction("Rooms", Room.class));

                m.add(new MDDAction("Boards", Board.class));

                m.add(new MDDAction("Stop sales", StopSalesView.class));

                m.add(new MDDAction("Inventory", InventoryListView.class));

                m.add(new MDDAction("Contracts", HotelContract.class));

                m.add(new MDDAction("Offers", AbstractHotelOffer.class));

                m.add(new MDDAction("Hotels without bookings", StopSalesView.class));

                m.add(new MDDAction("Zone remarks", StopSalesView.class));

                return m;
            }
        };
        return s;
    }
}
