package io.mateu.erp.client.mateu;

import io.mateu.erp.shared.mateu.ERPService;
import io.mateu.erp.shared.mateu.ERPServiceAsync;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.SqlComboBoxField;
import io.mateu.ui.core.client.components.fields.grids.columns.SqlComboBoxColumn;
import io.mateu.ui.core.shared.AsyncCallback;

/**
 * Created by miguel on 12/1/17.
 */
public class JPAComboBoxColumn extends SqlComboBoxColumn {
    private String entityClassName;

    public JPAComboBoxColumn(String id, String label, String entityClassName) {
        super(id, label, 100, null); this.entityClassName = entityClassName;
    }

    public String getJpql() {
        return "select x.id, x.name from " + entityClassName + " x order by x.name";
    }

    public JPAComboBoxColumn setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
        return this;
    }

    public void call(AsyncCallback<Object[][]> callback) {
        ((ERPServiceAsync)MateuUI.create(ERPService.class)).select(getJpql(),callback);
    }

}
