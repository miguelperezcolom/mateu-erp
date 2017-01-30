package io.mateu.erp.client.mateu;

import io.mateu.erp.shared.mateu.MetaData;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.components.fields.grids.CalendarField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.client.views.AbstractDialog;
import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

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
                buildFromMetadata(f, getMetadata().getData("_editorform"));
                return f;
            }
        };
    }

    private void buildFromMetadata(ViewForm f, Data metadata) {
        for (Data d : metadata.getList("_fields")) {
            AbstractField field = null;
            if (MetaData.FIELDTYPE_OUTPUT.equals(d.getString("_type"))) {
                f.add(field = new ShowTextField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_TEXTAREA.equals(d.getString("_type"))) {
                f.add(field = new TextAreaField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_STRING.equals(d.getString("_type"))) {
                f.add(field = new TextField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_INTEGER.equals(d.getString("_type"))) {
                f.add(field = new IntegerField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_DOUBLE.equals(d.getString("_type"))) {
                f.add(field = new DoubleField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_BOOLEAN.equals(d.getString("_type"))) {
                f.add(field = new CheckBoxField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_DATE.equals(d.getString("_type"))) {
                f.add(field = new CalendarField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_ENUM.equals(d.getString("_type"))) {
                f.add(field = new ComboBoxField(d.getString("_id"), d.getString("_label"), d.getPairList("_values")));
            } else if (MetaData.FIELDTYPE_ENTITY.equals(d.getString("_type"))) {
                f.add(field = new JPAComboBoxField(d.getString("_id"), d.getString("_label"), d.getString("_entityClassName")));
            } else if (MetaData.FIELDTYPE_PK.equals(d.getString("_type"))) {
                f.add(field = new PKField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_LIST.equals(d.getString("_type"))) {
                List<AbstractColumn> cols = new ArrayList<>();
                for (Data dc : d.getList("_cols")) {
                    if (MetaData.FIELDTYPE_STRING.equals(dc.getString("_type"))) {
                        cols.add(new TextColumn(dc.getString("_id"), dc.getString("_label"), 100, true));
                    } else if (MetaData.FIELDTYPE_ENTITY.equals(dc.getString("_type"))) {
                        cols.add(new JPAComboBoxColumn(dc.getString("_id"), dc.getString("_label"), dc.getString("_entityClassName")));
                    } else {
                        cols.add(new TextColumn(dc.getString("_id"), dc.getString("_label"), 100, true));
                    }
                }
                f.add(field = new GridField(d.getString("_id"), d.getString("_label"), cols));
            }
            if (field != null && d.containsKey("_required")) {
                field.setRequired(true);
            }
        }
    }

    @Override
    public List<AbstractColumn> createExtraColumns() {
        List<AbstractColumn> cols = new ArrayList<>();
        int poscol = 1;
        for (Data d : getMetadata().getData("_searchform").getList("_columns")) if (!MetaData.FIELDTYPE_ENTITY.equals(d.getString("_type")) && !MetaData.FIELDTYPE_ID.equals(d.getString("_type")) && !MetaData.FIELDTYPE_PK.equals(d.getString("_type"))) {
            cols.add(new TextColumn("col" + poscol++, d.getString("_label"), 200, false));
        }
        return cols;
    }

    @Override
    public String getSql() {
        String jpql = "select ";
        int poscol = 1;
        String orderField = null;
        for (Data d : getMetadata().getData("_searchform").getList("_columns")) if (MetaData.FIELDTYPE_ID.equals(d.getString("_type")) || MetaData.FIELDTYPE_PK.equals(d.getString("_type"))) {
            poscol++;
            jpql += "x." + d.getString("_id");
            break;
        }
        for (Data d : getMetadata().getData("_searchform").getList("_columns")) if (!MetaData.FIELDTYPE_ENTITY.equals(d.getString("_type")) && !MetaData.FIELDTYPE_ID.equals(d.getString("_type")) && !MetaData.FIELDTYPE_PK.equals(d.getString("_type"))) {
            if (poscol++ > 1) jpql += ",";
            jpql += "x." + d.getString("_id");
            if (orderField == null) orderField = d.getString("_id");
        }
        jpql += " from " + getEntityClassName() + " x";

        int posfilter = 0;
        Data sfd = getForm().getData();
        for (Data d : getMetadata().getData("_searchform").getList("_fields")) {
            if (!sfd.isEmpty(d.getString("_id"))) {
                Object v = sfd.get(d.getString("_id"));
                if (posfilter++ == 0) jpql += " where ";
                else jpql += " and ";
                if (v instanceof String) {
                    jpql += "lower(x." + d.getString("_id") + ")";
                    jpql += " like '%" + ((String) v).toLowerCase().replaceAll("'", "''") + "%' ";
                }
                else {
                    jpql += "x." + d.getString("_id");
                    if (v instanceof Pair) {
                        Object vv = ((Pair)v).getValue();
                        if ("enum".equals(d.getString("_type"))) {
                            jpql += " == '" + vv + "' ";
                        } else {
                            jpql += ".id";
                            if (v instanceof String) {
                                jpql += " == '" + vv + "' ";
                            } else {
                                jpql += " == " + vv + " ";
                            }
                        }
                    } else {
                        jpql += " == " + v + " ";
                    }
                }
            }
        }


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
        buildFromMetadata(f, getMetadata().getData("_searchform"));
        return f;
    }

    public Data getMetadata() {
        return metadata;
    }

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> as = super.createActions();
        as.add(new AbstractAction("Metadata") {
            @Override
            public void run() {

                System.out.println("metadata=" + getMetadata());

                MateuUI.openView(new AbstractDialog() {
                    @Override
                    public void onOk(Data data) {

                    }

                    @Override
                    public String getTitle() {
                        return "Metadata";
                    }

                    @Override
                    public Data initializeData() {

                        return new Data("_metadata", new Data(getMetadata()));
                    }

                    @Override
                    public AbstractForm createForm() {
                        return new ViewForm(this).setLastFieldMaximized(true).add(new DataViewerField("_metadata"));
                    }
                });
            }
        });
        return as;
    }

    @Override
    public String getEntityClassName() {
        return entityClassName;
    }
}
