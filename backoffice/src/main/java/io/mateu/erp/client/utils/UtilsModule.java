package io.mateu.erp.client.utils;

import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.tests.TestPopulator;
import io.mateu.mdd.core.app.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class UtilsModule extends AbstractModule {
    @Override
    public String getName() {
        return "Utils";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDOpenCRUDAction("Queue", AbstractTask.class));


        m.add(new MDDCallMethodAction("Populate with test data", TestPopulator.class, "populateEverything"));


        return m;
    }
}
