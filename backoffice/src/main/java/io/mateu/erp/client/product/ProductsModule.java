package io.mateu.erp.client.product;

import io.mateu.erp.model.booking.tickets.Ticket;
import io.mateu.erp.model.booking.tickets.TicketBook;
import io.mateu.erp.model.product.generic.*;
import io.mateu.erp.model.product.generic.Extra;
import io.mateu.erp.model.product.generic.Price;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.erp.model.product.tour.*;
import io.mateu.erp.model.product.transfer.*;
import io.mateu.mdd.core.app.AbstractMenu;
import io.mateu.mdd.core.app.AbstractModule;
import io.mateu.mdd.core.app.MDDMenu;
import io.mateu.mdd.core.app.MenuEntry;

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

        m.add(getGenericSubmenu());

        m.add(getHotelSubmenu());

        m.add(getTransferSubmenu());

        m.add(getPackagesSubmenu());

        return m;
    }

    private AbstractMenu getPackagesSubmenu() {
        AbstractMenu s = new AbstractMenu("Packages") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDMenu("Definition", "Variants", TourVariant.class, "Extras", TourExtra.class, "Costs", TourCost.class));

                m.add(new MDDMenu("Excursions", "Excursions", Excursion.class, "Zones", TourPriceZone.class, "Shifts", TourShift.class));

                m.add(new MDDMenu("Circuits", "Circuits", Circuit.class));

                m.add(new MDDMenu("Rates", "Contracts", io.mateu.erp.model.product.tour.Contract.class, "Prices", TourPrice.class));

                m.add(new MDDMenu("Tickets", "Books", TicketBook.class, "Tickets", Ticket.class));

                return m;
            }
        };
        return s;
    }

    private AbstractMenu getGenericSubmenu() {
        AbstractMenu s = new AbstractMenu("Generic") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDMenu("Definition", "Products", GenericProduct.class, "Extras", Extra.class));

                m.add(new MDDMenu("AI", "Allotment", AllotmentOnGeneric.class, "Stop sales", StopSaleOnGeneric.class, "Release", ReleaseOnGeneric.class));

                m.add(new MDDMenu("Rates", "Contracts", io.mateu.erp.model.product.generic.Contract.class, "Product prices", Price.class));

                return m;
            }
        };
        return s;
    }

    private AbstractMenu getTransferSubmenu() {
        AbstractMenu s = new AbstractMenu("Transfer") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDMenu("Definition", "Transfer points", TransferPoint.class, "Vehicles", Vehicle.class, "Extras", io.mateu.erp.model.product.transfer.Extra.class));

                m.add(new MDDMenu("Routes", "Routes", Route.class, "Stops", RouteStop.class, "Times", RouteTime.class));

                m.add(new MDDMenu("Rates", "Contracts", io.mateu.erp.model.product.transfer.Contract.class, "Prices", io.mateu.erp.model.product.transfer.Price.class, "Price zones", Zone.class));

                return m;

            }
        };
        return s;
    }

    private AbstractMenu getHotelSubmenu() {
        AbstractMenu s = new AbstractMenu("Hotel") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDMenu("Coding", "Categories", HotelCategory.class, "Board codes", BoardType.class, "Room codes", RoomType.class));

                m.add(new MDDMenu("Definition", "Hotels", Hotel.class, "Rooms", Room.class, "Boards", Board.class));

                m.add(new MDDMenu("AI", "Stop sales", StopSales.class, "Operations on stop sales", StopSalesOperation.class, "Inventory", Inventory.class, "Operations on inventory", InventoryOperation.class));

                //m.add(new MDDMenu("AI", "Stop sales", StopSalesView.class, "Inventory", InventoryListView.class));

                m.add(new MDDMenu("Prices", "Contracts", HotelContract.class, "Offers", AbstractHotelOffer.class));

                m.add(new MDDMenu("Operations", "Hotels without bookings", StopSalesView.class));

                return m;
            }
        };
        return s;
    }
}
