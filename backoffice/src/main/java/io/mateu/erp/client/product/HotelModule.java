package io.mateu.erp.client.product;

import io.mateu.ui.core.client.app.*;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;

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
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("Hotels") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.Hotel", new MDDCallback());
            }
        });

        m.add(new AbstractMenu("Coding") {

            @Override
            public List<MenuEntry> getEntries() {

                List<MenuEntry> m = new ArrayList<>();

                m.add(new AbstractAction("Categories") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.HotelCategory", new MDDCallback());
                    }
                });


                m.add(new AbstractAction("Board codes") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.BoardType", new MDDCallback());
                    }
                });


                m.add(new AbstractAction("Room codes") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.RoomType", new MDDCallback());
                    }
                });

                return m;
            }
        });


        m.add(new AbstractAction("Rooms") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.Room", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Boards") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.Board", new MDDCallback());
            }
        });

        m.add(new AbstractMenu("Stop sales") {

            @Override
            public List<MenuEntry> getEntries() {

                List<MenuEntry> m = new ArrayList<>();

                m.add(new AbstractAction("Stop sales") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.StopSales", new MDDCallback());
                    }
                });

                m.add(new AbstractAction("Lines") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.StopSalesLine", new MDDCallback());
                    }
                });

                m.add(new AbstractAction("Log") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.StopSalesOperation", new MDDCallback());
                    }
                });

                return m;
            }
        });

        m.add(new AbstractMenu("Inventory") {

            @Override
            public List<MenuEntry> getEntries() {

                List<MenuEntry> m = new ArrayList<>();

                m.add(new AbstractAction("Inventories") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.Inventory", new MDDCallback());
                    }
                });


                m.add(new AbstractAction("Lines") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.InventoryLine", new MDDCallback());
                    }
                });


                m.add(new AbstractAction("Log") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.InventoryOperation", new MDDCallback());
                    }
                });


                return m;
            }
        });


        m.add(new AbstractAction("Contracts") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.contracting.HotelContract", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Offers") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer", new MDDCallback());
            }
        });

        return m;
    }
}
