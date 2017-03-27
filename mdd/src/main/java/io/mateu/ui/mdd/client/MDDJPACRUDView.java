package io.mateu.ui.mdd.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.Label;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.components.fields.grids.CalendarField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.ColumnAlignment;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.client.data.ChangeListener;
import io.mateu.ui.core.client.views.AbstractDialog;
import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.mdd.shared.ERPService;
import io.mateu.ui.mdd.shared.MetaData;

import java.net.URL;
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
            public List<AbstractAction> createActions() {
                List<AbstractAction> as = super.createActions();
                for (Data da : getMetadata().getData("_editorform").getList("_actions")) {
                    as.add(createAction(this, da));
                }
                return as;
            }

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
                field = new ShowTextField(d.getString("_id"), d.getString("_label"));
            } else if (MetaData.FIELDTYPE_TEXTAREA.equals(d.getString("_type"))) {
                field = new TextAreaField(d.getString("_id"), d.getString("_label"));
            } else if (MetaData.FIELDTYPE_STRING.equals(d.getString("_type"))) {
                field = new TextField(d.getString("_id"), d.getString("_label"));
            } else if (MetaData.FIELDTYPE_INTEGER.equals(d.getString("_type"))) {
                field = new IntegerField(d.getString("_id"), d.getString("_label"));
            } else if (MetaData.FIELDTYPE_DOUBLE.equals(d.getString("_type"))) {
                field = new DoubleField(d.getString("_id"), d.getString("_label"));
            } else if (MetaData.FIELDTYPE_BOOLEAN.equals(d.getString("_type"))) {
                field = new CheckBoxField(d.getString("_id"), d.getString("_label"));
            } else if (MetaData.FIELDTYPE_DATE.equals(d.getString("_type"))) {
                field = new CalendarField(d.getString("_id"), d.getString("_label"));
            } else if (MetaData.FIELDTYPE_DATETIME.equals(d.getString("_type"))) {
                field = new DateTimeField(d.getString("_id"), d.getString("_label"));
            } else if (MetaData.FIELDTYPE_ENUM.equals(d.getString("_type"))) {
                field = new ComboBoxField(d.getString("_id"), d.getString("_label"), d.getPairList("_values"));
            } else if (MetaData.FIELDTYPE_ENTITY.equals(d.getString("_type"))) {
                if (d.getBoolean("_useidtoselect")) {
                    String ql = d.getString("_ql");
                    if (ql == null) ql = "select x.id, x.name from " + d.getString("_entityClassName") + " x where x.id = xxxx";
                    field = new JPASelectByIdField(d.getString("_id"), d.getString("_label"), ql) {

                        Data metadata = null;

                        @Override
                        public AbstractEditorView getEditor() {
                            JPAEditorView editor = new JPAEditorView(null) {

                                public JPAEditorView get() {
                                    return this;
                                }

                                @Override
                                public String getEntityClassName() {
                                    return d.getString("_entityClassName");
                                }

                                @Override
                                public List<AbstractAction> createActions() {
                                    List<AbstractAction> as = super.createActions();
                                    for (Data da : metadata.getData("_editorform").getList("_actions")) {
                                        as.add(createAction(this, da));
                                    }
                                    return as;
                                }

                                @Override
                                public String getViewId() {
                                    return d.getString("_entityClassName") + "-" + getInitialId();
                                }

                                @Override
                                public String getTitle() {
                                    return d.getString("_entityClassName").substring(getEntityClassName().lastIndexOf(".") + 1);
                                }

                                @Override
                                public AbstractForm createForm() {
                                    ViewForm f = new ViewForm(get());
                                    buildFromMetadata(f, metadata.getData("_editorform"));
                                    return f;
                                }
                            };
                            return editor;
                        }

                        @Override
                        public Pair getPair(Data editorData) {
                            return new Pair(editorData.get("_id"), editorData.get("_tostring"));
                        }

                        @Override
                        public void createNew() {
                            if (metadata == null) ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData(d.getString("_entityClassName"), new Callback<Data>() {
                                @Override
                                public void onSuccess(Data result) {
                                    metadata = result;
                                    _createNew();
                                }
                            });
                            else _createNew();
                        }

                        public void _createNew() {
                            super.createNew();
                        }

                        @Override
                        public void edit(Object id) {
                            if (metadata == null) ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData(d.getString("_entityClassName"), new Callback<Data>() {
                                @Override
                                public void onSuccess(Data result) {
                                    metadata = result;
                                    _edit(id);                              }
                            });
                            else _edit(id);
                        }

                        public void _edit(Object id) {
                            super.edit(id);
                        }
                    };
                } else {
                    String ql = d.getString("_ql");
                    if (ql == null) ql = "select x.id, x.name from " + d.getString("_entityClassName") + " x order by x.name";
                    field = new JPAComboBoxField(d.getString("_id"), d.getString("_label"), ql);
                }
            } else if (MetaData.FIELDTYPE_PK.equals(d.getString("_type"))) {
                field = new PKField(d.getString("_id"), d.getString("_label"));
            } else if (MetaData.FIELDTYPE_LIST.equals(d.getString("_type"))) {
                String ql = d.getString("_ql");
                if (ql == null) ql = "select x.id, x.name from " + d.getString("_entityClassName") + " x order by x.name";
                field = new JPAListSelectionField(d.getString("_id"), d.getString("_label"), ql);
            } else if (MetaData.FIELDTYPE_GRID.equals(d.getString("_type"))) {
                List<AbstractColumn> cols = new ArrayList<>();
                for (Data dc : d.getList("_cols")) {
                    if (MetaData.FIELDTYPE_STRING.equals(dc.getString("_type"))) {
                        cols.add(new TextColumn(dc.getString("_id"), dc.getString("_label"), 100, true));
                    } else if (MetaData.FIELDTYPE_ENTITY.equals(dc.getString("_type"))) {
                        String ql = dc.getString("_ql");
                        if (ql == null) ql = "select x.id, x.name from " + dc.getString("_entityClassName") + " x order by x.name";
                        cols.add(new JPAComboBoxColumn(dc.getString("_id"), dc.getString("_label"), ql));
                    } else {
                        cols.add(new TextColumn(dc.getString("_id"), dc.getString("_label"), 100, true));
                    }
                }
                field = new GridField(d.getString("_id"), d.getString("_label"), cols);
            }
            if (field != null && d.containsKey("_required")) {
                field.setRequired(true);
            }
            if (field != null && d.containsKey("_startsline")) {
                field.setBeginingOfLine(true);
            }
            if (field != null && d.containsKey("_unmodifiable")) {
                System.out.println("field " + field.getId() + " is unmodifiable");
                field.setUnmodifiable(true);
            }
            if (field != null) f.add(field);
        }
    }

    @Override
    public List<AbstractColumn> createExtraColumns() {
        List<AbstractColumn> cols = new ArrayList<>();
        int poscol = 1;
        for (Data d : getMetadata().getData("_searchform").getList("_columns")) if (!MetaData.FIELDTYPE_ID.equals(d.getString("_type")) && !MetaData.FIELDTYPE_PK.equals(d.getString("_type"))) {
            TextColumn col;
            cols.add(col = new TextColumn("col" + poscol++, d.getString("_label"), d.getInt("_width"), false));
            if ("center".equals(d.getString("_align"))) col.setAlignment(ColumnAlignment.CENTER);
            if ("right".equals(d.getString("_align"))) col.setAlignment(ColumnAlignment.RIGHT);
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
        for (Data d : getMetadata().getData("_searchform").getList("_columns")) if (!MetaData.FIELDTYPE_ID.equals(d.getString("_type")) && !MetaData.FIELDTYPE_PK.equals(d.getString("_type"))) {
            if (poscol++ > 1) jpql += ",";
            jpql += "x." + d.getString("_qlname");
            if (orderField == null) orderField = d.getString("_qlname");
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
                            jpql += " = '" + vv + "' ";
                        } else {
                            jpql += ".id";
                            if (v instanceof String) {
                                jpql += " = '" + vv + "' ";
                            } else {
                                jpql += " = " + vv + " ";
                            }
                        }
                    } else {
                        jpql += " = " + v + " ";
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

    @Override
    public Data getMetadata() {
        return metadata;
    }

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> as = super.createActions();
        for (Data da : getMetadata().getList("_actions")) {
            as.add(createAction(this, da));
        }
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

    private AbstractAction createAction(Data da, MDDActionHelper h) {
        return new AbstractAction(da.getString("_name")) {
            @Override
            public void run() {

                Data parameters = new Data();

                h.complete(parameters);

                boolean needsParameters = false;

                for (Data dp : da.getList("_parameters")) {
                    String n = dp.getString("_name");
                    if ("_selection".equals(n)) {
                        parameters.set(n, getSelection());
                    } else needsParameters = true;
                }

                if (needsParameters) {
                    MateuUI.openView(new AbstractDialog() {

                        @Override
                        public Data initializeData() {
                            return parameters;
                        }

                        @Override
                        public void onOk(Data data) {
                            ((ERPServiceAsync)MateuUI.create(ERPService.class)).runInServer(da.getString("_entityClassName"), da.getString("_methodname"), getForm().getData(), new Callback<Object>() {
                                @Override
                                public void onSuccess(Object result) {
                                    h.onSuccess(result);
                                }
                            });
                        }

                        @Override
                        public String getTitle() {
                            return da.getString("_name");
                        }

                        @Override
                        public AbstractForm createForm() {
                            ViewForm f = new ViewForm(this);
                            buildFromMetadata(f, da.getData("_form"));
                            return f;
                        }
                    });
                } else ((ERPServiceAsync)MateuUI.create(ERPService.class)).runInServer(da.getString("_entityClassName"), da.getString("_methodname"), parameters, new Callback<Object>() {
                    @Override
                    public void onSuccess(Object result) {
                        h.onSuccess(result);
                    }
                });

            }
        };

    }

    private AbstractAction createAction(MDDJPACRUDView v, Data da) {
        return createAction(da, new MDDActionHelper() {
            @Override
            public void onSuccess(Object result) {
                    v.search();
            }

            @Override
            public void complete(Data parameters) {

            }
        });
    }

    private AbstractAction createAction(AbstractEditorView v, Data da) {
        return createAction(da, new MDDActionHelper() {
            @Override
            public void onSuccess(Object result) {
                if (result instanceof URL) {
                    MateuUI.open((URL) result);
                } else {
                    MateuUI.alert("" + result);
                }
            }

            @Override
            public void complete(Data parameters) {
                parameters.set("_id", v.getForm().getData().get("_id"));
            }
        });
    }

    @Override
    public String getEntityClassName() {
        return entityClassName;
    }
}
