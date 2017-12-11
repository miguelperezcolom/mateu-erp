package io.mateu.erp.client.utils;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.client.views.AbstractSqlListView;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.shared.ERPService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 19/3/17.
 */
public class JPQLView extends AbstractSqlListView {
    @Override
    public String getSql() {
        return getForm().getData().getString("sql");
    }

    @Override
    public List<AbstractColumn> createColumns() {
        List<AbstractColumn> cols = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            cols.add(new TextColumn("col" + i, "col" + i, 60, false));
        }
        return cols;
    }

    @Override
    public String getTitle() {
        return "JPQL";
    }

    @Override
    public void build() {
        add(new TextField("sql"));
    }

    @Override
    public void rpc(Data parameters, AsyncCallback<Data> callback) {
        parameters.set("_sql", getSql());
        parameters.set("_rowsperpage", 100);
        ERPServiceAsync s = MateuUI.create(ERPService.class);
        s.selectPaginated(parameters, callback);
    }

    @Override
    public void search() {
        String sql = getForm().getData().getString("sql");
        if (sql != null && !"".equals(sql.trim())) super.search();
    }

}
