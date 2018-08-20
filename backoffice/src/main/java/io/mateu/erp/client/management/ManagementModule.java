package io.mateu.erp.client.management;

import io.mateu.mdd.core.app.AbstractAction;
import io.mateu.mdd.core.app.AbstractModule;
import io.mateu.mdd.core.app.MDDExecutionContext;
import io.mateu.mdd.core.app.MenuEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ManagementModule extends AbstractModule {
    @Override
    public String getName() {
        return "Management";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("Control panel") {
            @Override
            public void run(MDDExecutionContext mddExecutionContext) {

            }
        });

        m.add(new AbstractAction("Sales report") {
            @Override
            public void run(MDDExecutionContext mddExecutionContext) {

            }
        });

        return m;
    }
}
