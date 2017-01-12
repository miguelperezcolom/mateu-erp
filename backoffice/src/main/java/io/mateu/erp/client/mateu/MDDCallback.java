package io.mateu.erp.client.mateu;

import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 12/1/17.
 */
public class MDDCallback extends Callback<Data> {

    @Override
    public void onSuccess(Data result) {
        MateuUI.openView(new MDDJPACRUDView(result));
    }

}
