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
public class ProductModule extends AbstractModule {
    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("Packages") {
            @Override
            public void run() {

            }
        });
        m.add(new AbstractAction("Generic services") {
            @Override
            public void run() {

            }
        });

        m.add(new AbstractAction("Hotel") {
            @Override
            public void run() {

            }
        });

        m.add(new AbstractMenu("Transfer") {
            @Override
            public List<MenuEntry> getEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new AbstractAction("Transfer points") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.transfer.TransferPoint", new MDDCallback());
                    }
                });

                m.add(new AbstractAction("Contracts") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.transfer.Contract", new MDDCallback());
                    }
                });

                m.add(new AbstractAction("Prices") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.transfer.Price", new MDDCallback());
                    }
                });

                m.add(new AbstractAction("Vehicles") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.transfer.Vehicle", new MDDCallback());
                    }
                });

                m.add(new AbstractAction("Price zones") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.transfer.Zone", new MDDCallback());
                    }
                });

                return m;
            }
        });

        return m;
    }
}
