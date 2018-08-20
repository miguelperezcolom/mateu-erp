package io.mateu.erp.client.management;


import io.mateu.mdd.core.app.AbstractArea;
import io.mateu.mdd.core.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ManagementArea extends AbstractArea {

    public ManagementArea() {
        super("Management");
    }

    @Override
    public List<AbstractModule> buildModules() {
        return Arrays.asList(new ManagementModule());
    }
}
