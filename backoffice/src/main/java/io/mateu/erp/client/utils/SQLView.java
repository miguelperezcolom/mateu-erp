package io.mateu.erp.client.utils;

import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractSqlListView;
import io.mateu.ui.core.client.views.ViewForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 19/3/17.
 */
public class SQLView extends AbstractSqlListView {
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
    public void search() {
        String sql = getForm().getData().getString("sql");
        if (sql != null && !"".equals(sql.trim())) super.search();
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this).add(new TextField("sql"));
    }
}
