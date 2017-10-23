package io.mateu.erp.client.financial;

import io.mateu.erp.shared.financial.FinancialService;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.components.fields.DateField;
import io.mateu.ui.core.client.views.AbstractDialog;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class InvoicingModule extends AbstractModule {
    @Override
    public String getName() {
        return "Invoicing";
    }

    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();


        m.add(new AbstractAction("Isued invoices") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.invoicing.Invoice", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Received invoices") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.invoicing.Invoice", new MDDCallback());
            }
        });

        return m;
    }
}
