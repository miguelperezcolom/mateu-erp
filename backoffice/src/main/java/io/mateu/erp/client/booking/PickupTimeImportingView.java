package io.mateu.erp.client.booking;

import io.mateu.erp.client.booking.BookingServiceAsync;
import io.mateu.erp.shared.booking.BookingService;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.FileField;
import io.mateu.ui.core.client.views.AbstractView;

import java.util.List;

/**
 * Created by miguel on 13/5/17.
 */
public class PickupTimeImportingView extends AbstractView {

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> l = super.createActions();

        l.add(new AbstractAction("Process file") {
            @Override
            public void run() {
                ((BookingServiceAsync) MateuUI.create(BookingService.class)).importPickupTimeExcel(getForm().getData(), new Callback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        MateuUI.notifyInfo(result);
                    }
                });
            }
        });

        return l;
    }

    @Override
    public String getTitle() {
        return "Pickup time importing";
    }

    @Override
    public void build() {
        add(new FileField("file", "Excel file"));
    }
}
