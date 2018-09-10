package io.mateu.erp.client.crm;

import com.vaadin.icons.VaadinIcons;
import io.mateu.mdd.core.app.AbstractArea;
import io.mateu.mdd.core.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

public class CRMArea extends AbstractArea {


    public CRMArea() {
        super(VaadinIcons.HANDSHAKE, "Biz");
    }

    @Override
    public List<AbstractModule> buildModules() {
        return Arrays.asList(new RevenueModule());
    }
}
