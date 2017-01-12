package io.mateu.erp.client.mateu;

import io.mateu.erp.shared.mateu.MetaData;
import io.mateu.ui.core.client.components.fields.CheckBoxField;
import io.mateu.ui.core.client.components.fields.DoubleField;
import io.mateu.ui.core.client.components.fields.IntegerField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.CalendarField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 12/1/17.
 */
public class MDDJPACRUDView extends BaseJPACRUDView {

    @Override
    public String getViewId() {
        return getEntityClassName();
    }

    private Data metadata;
    private String entityClassName;

    public MDDJPACRUDView(Data metadata) {
        this.metadata = metadata;
        this.entityClassName = metadata.getString("_entityClassName");
    }
    
    @Override
    public AbstractEditorView getNewEditorView() {
        return new JPAEditorView(this) {

            @Override
            public String getViewId() {
                return getEntityClassName() + "-" + getInitialId();
            }

            @Override
            public String getTitle() {
                return getEntityClassName().substring(getEntityClassName().lastIndexOf(".") + 1);
            }

            @Override
            public AbstractForm createForm() {
                ViewForm f = new ViewForm(this);
                buildFromMetadata(f);
                return f;
            }
        };
    }

    private void buildFromMetadata(ViewForm f) {
        for (Data d : getMetadata().getList("_fields")) {
            if (MetaData.FIELDTYPE_STRING.equals(d.getString("_type"))) {
                f.add(new TextField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_INTEGER.equals(d.getString("_type"))) {
                f.add(new IntegerField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_DOUBLE.equals(d.getString("_type"))) {
                f.add(new DoubleField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_BOOLEAN.equals(d.getString("_type"))) {
                f.add(new CheckBoxField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_DATE.equals(d.getString("_type"))) {
                f.add(new CalendarField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_ENTITY.equals(d.getString("_type"))) {
                f.add(new JPAComboBoxField(d.getString("_id"), d.getString("_label"), d.getString("_entityClassName")));
            }
        }
    }

    @Override
    public List<AbstractColumn> createExtraColumns() {
        List<AbstractColumn> cols = new ArrayList<>();
        int poscol = 1;
        for (Data d : getMetadata().getList("_fields")) if (!"id".equals(d.getString("_id")) &&  !MetaData.FIELDTYPE_ENTITY.equals(d.getString("_type"))) {
            cols.add(new TextColumn("col" + poscol++, d.getString("_label"), 200, false));
        }
        return cols;
    }

    @Override
    public String getSql() {
        String jpql = "select ";
        int poscol = 1;
        String orderField = null;
        for (Data d : getMetadata().getList("_fields")) if (!MetaData.FIELDTYPE_ENTITY.equals(d.getString("_type"))) {
            if (poscol++ > 1) jpql += ",";
            jpql += "x." + d.getString("_id");
            if (!"id".equals(d.getString("_id")) && orderField == null) orderField = d.getString("_id");
        }
        jpql += " from " + getEntityClassName() + " x";



        if (orderField != null) jpql += " order by x." + orderField;
        return jpql;
    }

    @Override
    public String getTitle() {
        return getEntityClassName().substring(getEntityClassName().lastIndexOf(".") + 1) + "s";
    }

    @Override
    public AbstractForm createForm() {
        ViewForm f = new ViewForm(this);
        buildFromMetadata(f);
        return f;
    }

    public Data getMetadata() {
        return metadata;
    }

    @Override
    public String getEntityClassName() {
        return entityClassName;
    }
}
