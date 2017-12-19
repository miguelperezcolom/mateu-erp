package io.mateu.erp.client.utils;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDAction;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class UtilsModule extends AbstractModule {
    @Override
    public String getName() {
        return "Utils";
    }

    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Queue", AbstractTask.class));


        m.add(new AbstractAction("SQL") {
            @Override
            public void run() {

                MateuUI.openView(new SQLView());
            }
        });

        m.add(new AbstractAction("JPQL") {
            @Override
            public void run() {

                MateuUI.openView(new JPQLView());
            }
        });

        m.add(new AbstractAction("Populate with test data") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).runInServer(MateuUI.getApp().getUserData(),"io.mateu.erp.tests.TestPopulator", "populateEverything", null, new AsyncCallback<Object>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        MateuUI.alert("" + throwable.getClass().getName() + ": " + throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(Object o) {
                        MateuUI.notifyInfo("" + o);
                    }
                });
            }
        });


        return m;
    }
}
