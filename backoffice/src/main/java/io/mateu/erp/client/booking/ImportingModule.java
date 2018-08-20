package io.mateu.erp.client.booking;

import io.mateu.erp.model.importing.TransferAutoImport;
import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.erp.model.importing.TransferImportTask;
import io.mateu.mdd.core.app.AbstractModule;
import io.mateu.mdd.core.app.MDDOpenCRUDAction;
import io.mateu.mdd.core.app.MenuEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ImportingModule extends AbstractModule {
    @Override
    public String getName() {
        return "Importing";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDOpenCRUDAction("Importing Queue", TransferImportTask.class));

        m.add(new MDDOpenCRUDAction("Auto imports", TransferAutoImport.class));

        m.add(new MDDOpenCRUDAction("Transfer requests", TransferBookingRequest.class));


        return m;
    }
}
