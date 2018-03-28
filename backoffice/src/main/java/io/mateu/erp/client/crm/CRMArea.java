package io.mateu.erp.client.crm;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

public class CRMArea extends AbstractArea {


    public CRMArea() {
        super("CRM");
    }

    @Override
    public List<AbstractModule> buildModules() {
        return Arrays.asList(new RevenueModule());
    }
}
