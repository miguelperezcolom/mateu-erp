package io.mateu.erp.client.utils;

import com.vaadin.icons.VaadinIcons;
import io.mateu.mdd.core.app.AbstractArea;
import io.mateu.mdd.core.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class UtilsArea extends AbstractArea {

    public UtilsArea() {
        super(VaadinIcons.TOOLBOX,"Utils");
    } // VaadinIcons.TOOLBOX

    @Override
    public List<AbstractModule> buildModules() {
        return Arrays.asList(new UtilsModule());
    }
}
