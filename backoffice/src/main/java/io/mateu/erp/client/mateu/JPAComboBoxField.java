package io.mateu.erp.client.mateu;

import io.mateu.erp.shared.mateu.ERPService;
import io.mateu.erp.shared.mateu.ERPServiceAsync;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.AbstractField;
import io.mateu.ui.core.client.components.fields.SqlComboBoxField;
import io.mateu.ui.core.shared.AsyncCallback;

/**
 * Created by miguel on 12/1/17.
 */
public class JPAComboBoxField  extends SqlComboBoxField {
    private String entityClassName;

    public JPAComboBoxField(String id) {
        super(id);
    }

    public JPAComboBoxField(String id, String label) {
        super(id, label);
    }

    public JPAComboBoxField(String id, String label, String entityClassName) {
        super(id, label); this.entityClassName = entityClassName;
    }

    public String getJpql() {
        return "select x.id, x.name from " + entityClassName + " x order by x.name";
    }

    public JPAComboBoxField setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
        return this;
    }

    public void call(AsyncCallback<Object[][]> callback) {
        ((ERPServiceAsync)MateuUI.create(ERPService.class)).select(getJpql(),callback);
    }

}
