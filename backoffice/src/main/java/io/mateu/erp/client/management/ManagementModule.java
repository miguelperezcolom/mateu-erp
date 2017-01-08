package io.mateu.erp.client.management;

import io.mateu.erp.client.admin.CustomerCRUD;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ManagementModule extends AbstractModule {
    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("Control panel") {
            @Override
            public void run() {
                MateuUI.openView(new CustomerCRUD());
            }
        });

        m.add(new AbstractAction("Sales report") {
            @Override
            public void run() {

            }
        });

        return m;
    }
}
