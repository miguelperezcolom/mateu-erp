package io.mateu.erp.client.management;

import io.mateu.mdd.core.app.*;

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

        m.add(new MDDOpenEditorAction("Dashboard", Dashboard.class));

        m.add(new MDDOpenEditorAction("Status", StateOfAffairs.class));

        return m;
    }
}
