package io.mateu.erp.client.admin;

import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class CustomerCRUD extends AbstractCRUDView {
    @Override
    public AbstractEditorView getNewEditorView() {
        return new BaseEditorView() {
            @Override
            public String getServerSideControllerKey() {
                return "customer";
            }

            @Override
            public String getTitle() {
                return "Customer";
            }

            @Override
            public AbstractForm createForm() {
                return new ViewForm(this).add(new TextField<String>("name", "Name"));
            }
        };
    }

    @Override
    public List<AbstractColumn> createExtraColumns() {
        return Arrays.asList(new TextColumn("col1", "Name", 200, false));
    }

    @Override
    public void delete(List<Data> list, AsyncCallback<Void> asyncCallback) {

    }

    @Override
    public String getSql() {
        return "select cusidcus, cusname from ma_customer order by cusname";
    }

    @Override
    public String getTitle() {
        return "Customers";
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this);
    }
}
