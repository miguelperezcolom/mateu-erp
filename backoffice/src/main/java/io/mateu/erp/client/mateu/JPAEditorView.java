package io.mateu.erp.client.mateu;

import io.mateu.erp.shared.mateu.ERPService;
import io.mateu.erp.shared.mateu.ERPServiceAsync;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.views.BaseEditorView;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 11/1/17.
 */
public abstract class JPAEditorView extends BaseEditorView {

    private final JPACRUDView crud;

    public JPAEditorView(JPACRUDView crud) {
        this.crud = crud;
    }

    public String getEntityClassName() {
        return crud.getEntityClassName();
    }

    @Override
    public String getServerSideControllerKey() {
        return "jpa";
    }

    @Override
    public void save(Data data, AsyncCallback<Data> callback) {
        ERPServiceAsync s = MateuUI.create(ERPService.class);
        s.set(this.getServerSideControllerKey(), getEntityClassName(), data, callback);
    }

    @Override
    public void load(Object id, AsyncCallback<Data> callback) {
        ERPServiceAsync s = MateuUI.create(ERPService.class);
        s.get(this.getServerSideControllerKey(), getEntityClassName(), id, callback);
    }

}
