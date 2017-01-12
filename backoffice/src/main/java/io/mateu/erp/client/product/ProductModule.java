package io.mateu.erp.client.product;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;

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

        m.add(new AbstractAction("Transfer") {
            @Override
            public void run() {

            }
        });

        return m;
    }
}
