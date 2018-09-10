package io.mateu.erp.client.cms;


import com.vaadin.icons.VaadinIcons;
import io.mateu.mdd.core.app.AbstractArea;
import io.mateu.mdd.core.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class CMSArea extends AbstractArea {

    public CMSArea() {
        super(VaadinIcons.MOBILE_BROWSER, "CMS");
    }

    @Override
    public List<AbstractModule> buildModules() {
        return Arrays.asList(new CMSModule());
    }
}
