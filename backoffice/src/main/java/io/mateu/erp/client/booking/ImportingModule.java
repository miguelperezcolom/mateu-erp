package io.mateu.erp.client.booking;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonia on 02/04/2017.
 */
public class ImportingModule extends AbstractModule {
    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        /*
        m.add(new AbstractAction("Importing Queue") {
            @Override
            public void run() {
                MateuUI.openView(new ImportingQueueView());
            }
        });
*/

        m.add(new AbstractAction("Importing Queue") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.importing.TransferImportTask", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Auto imports") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.importing.TransferAutoImport", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Transfer requests") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.importing.TransferBookingRequest", new MDDCallback());
            }
        });


        return m;
    }
}
