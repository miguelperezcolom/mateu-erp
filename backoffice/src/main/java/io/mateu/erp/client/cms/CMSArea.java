package io.mateu.erp.client.cms;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class CMSArea extends AbstractArea {

    public CMSArea() {
        super("CMS");
    }

    @Override
    public List<AbstractModule> getModules() {
        return Arrays.asList(new CMSModule());
    }
}
