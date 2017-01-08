package io.mateu.erp.client.admin;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class AdminArea extends AbstractArea {

    public AdminArea() {
        super("Admin");
    }

    @Override
    public List<AbstractModule> getModules() {
        return Arrays.asList(new AdminModule());
    }
}
