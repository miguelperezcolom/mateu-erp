package io.mateu.erp.client.cms;

import io.mateu.erp.model.cms.Theme;
import io.mateu.erp.model.cms.Website;
import io.mateu.mdd.core.app.AbstractModule;
import io.mateu.mdd.core.app.MDDOpenCRUDAction;
import io.mateu.mdd.core.app.MenuEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class CMSModule extends AbstractModule {
    @Override
    public String getName() {
        return "CMS";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDOpenCRUDAction("Websites", Website.class));

        m.add(new MDDOpenCRUDAction("Themes", Theme.class));

        return m;
    }
}
