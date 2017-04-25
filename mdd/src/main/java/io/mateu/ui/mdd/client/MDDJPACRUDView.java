package io.mateu.ui.mdd.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.Button;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.components.fields.grids.CalendarField;
import io.mateu.ui.core.client.components.fields.grids.columns.*;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.CellStyleGenerator;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.shared.ERPService;
import io.mateu.ui.mdd.shared.MDDLink;
import io.mateu.ui.mdd.shared.MetaData;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
    public boolean canDelete() {
        return getMetadata().isEmpty("_indelible");
    }

    @Override
    public AbstractEditorView getNewEditorView() {
           return getNewEditorView(getEntityClassName(), getMetadata().getData("_editorform"));
    }

    public AbstractEditorView getNewEditorView(String entityClassName, Data formMetaData) {

        return new JPAEditorView(this) {

                @Override
                public List<AbstractAction> createActions() {
                    List<AbstractAction> as = super.createActions();
                    for (Data da : formMetaData.getList("_actions")) if (da.isEmpty("_addasbutton")) {
                        as.add(createAction(this, da));
                    }
                    return as;
                }

            @Override
            public String getEntityClassName() {
                return entityClassName;
            }

            @Override
                public String getViewId() {
                    return entityClassName + "-" + getInitialId();
                }

                @Override
                public String getTitle() {
                    return entityClassName.substring(entityClassName.lastIndexOf(".") + 1);
                }

                @Override
                public AbstractForm createForm() {
                    ViewForm f = new ViewForm(this);
                    buildFromMetadata(this, f, formMetaData, false);
                    return f;
                }
            };

    }

    @Override
    public void openNew() {
        if (getMetadata().isEmpty("_subclasses")) {
            super.openNew();
        } else {
            List<Pair> options = new ArrayList<>();
            for (Data d : getMetadata().getList("_subclasses")) {
                options.add(new Pair(d.get("_type"), d.get("_name")));
            }

            MateuUI.openView(new AbstractDialog() {

                @Override
                public Data initializeData() {
                    Data d = super.initializeData();
                    if (options.size() > 0) d.set("type", options.get(0));
                    return d;
                }

                @Override
                public void onOk(Data data) {
                    if (!getForm().getData().isEmpty("type")) {
                        String type = (String) ((Pair)getForm().getData().get("type")).getValue();
                        for (Data d : getMetadata().getList("_subclasses")) {
                            if (type.equals(d.get("_type"))) {
                                openEditor(getNewEditorView(d.get("_type"), d.get("_editorform")));
                                break;
                            }
                        }
                    }
                }

                @Override
                public String getTitle() {
                    return "Choose object type to create";
                }

                @Override
                public AbstractForm createForm() {
                    return new ViewForm(this).add(new RadioButtonField("type", "Type", options).setRequired(true));
                }
            });

        }
    }

    @Override
    public void open(String propertyId, Data data) {
        if (getMetadata().isEmpty("_subclasses")) {
            super.open(propertyId, data);
        } else {
            Class type = null;
            for (String n : data.getPropertyNames()) {
                Object o = data.get(n);
                if (o instanceof Class) {
                    type = (Class) o;
                    break;
                }
            }
            for (Data d : getMetadata().getList("_subclasses")) {
                if (d.get("_type").equals(type.getCanonicalName())) {
                    openEditor(getNewEditorView(d.get("_type"), d.get("_editorform")).setInitialId(data.get(propertyId)));
                    break;
                }
            }
        }
    }

    private void buildFromMetadata(AbstractEditorView view, AbstractForm f, Data metadata, boolean buildingSearchForm) {
        buildFromMetadata(f, metadata.getList("_fields"), buildingSearchForm);
        for (Data da : metadata.getList("_actions")) {
            if (da.getBoolean("_addasbutton")) {
                AbstractAction a = createAction(view, da);
                f.add(new Button(da.getString("_name")) {

                    @Override
                    public void run() {
                        a.run();
                    }
                });
            }
        }
    }

    private void buildFromMetadata(AbstractForm f, List<Data> fieldsMetadata, boolean buildingSearchForm) {
        for (Data d : fieldsMetadata) {
            List<AbstractField> fields = new ArrayList<>();
            if (MetaData.FIELDTYPE_OUTPUT.equals(d.getString("_type"))) {
                fields.add(new ShowTextField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_TEXTAREA.equals(d.getString("_type"))) {
                fields.add(new TextAreaField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_STRING.equals(d.getString("_type"))) {
                fields.add(new TextField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_INTEGER.equals(d.getString("_type"))) {
                fields.add(new IntegerField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_LONG.equals(d.getString("_type"))) {
                fields.add(new LongField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_DOUBLE.equals(d.getString("_type"))) {
                fields.add(new DoubleField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_BOOLEAN.equals(d.getString("_type"))) {
                fields.add(new CheckBoxField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_DATE.equals(d.getString("_type"))) {
                if (buildingSearchForm) {
                    fields.add(new CalendarField(d.getString("_id") + "_from", d.getString("_label") + " from"));
                    fields.add(new CalendarField(d.getString("_id") + "_to", d.getString("_label") + " to"));
                } else {
                    fields.add(new CalendarField(d.getString("_id"), d.getString("_label")));
                }
            } else if (MetaData.FIELDTYPE_DATETIME.equals(d.getString("_type"))) {
                if (buildingSearchForm) {
                    fields.add(new CalendarField(d.getString("_id") + "_from", d.getString("_label") + " from"));
                    fields.add(new CalendarField(d.getString("_id") + "_to", d.getString("_label") + " to"));
                } else {
                    fields.add(new DateTimeField(d.getString("_id"), d.getString("_label")));
                }
            } else if (MetaData.FIELDTYPE_ENUM.equals(d.getString("_type"))) {
                fields.add(new ComboBoxField(d.getString("_id"), d.getString("_label"), d.getPairList("_values")));
            } else if (MetaData.FIELDTYPE_FILE.equals(d.getString("_type"))) {
                fields.add(new FileField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_ENTITY.equals(d.getString("_type"))) {
                if (d.getBoolean("_useidtoselect")) {
                    fields.add(new JPASelectByIdField(d.getString("_id"), d.getString("_label"), d.getString("_ql")) {

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
                                    buildFromMetadata(f, metadata.getData("_editorform").getList("_fields"), false);
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
                            if (metadata == null)
                                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData(d.getString("_entityClassName"), new Callback<Data>() {
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
                            if (metadata == null)
                                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData(d.getString("_entityClassName"), new Callback<Data>() {
                                    @Override
                                    public void onSuccess(Data result) {
                                        metadata = result;
                                        _edit(id);
                                    }
                                });
                            else _edit(id);
                        }

                        public void _edit(Object id) {
                            super.edit(id);
                        }
                    });
                } else if (d.getBoolean("_useautocompletetoselect")) {
                    fields.add(new JPAAutocompleteField(d.getString("_id"), d.getString("_label"), d.getString("_ql")));
                } else {
                    fields.add(new JPAComboBoxField(d.getString("_id"), d.getString("_label"), d.getString("_ql")));
                }
            } else if (MetaData.FIELDTYPE_PK.equals(d.getString("_type"))) {
                fields.add(new PKField(d.getString("_id"), d.getString("_label")));
            } else if (MetaData.FIELDTYPE_LIST.equals(d.getString("_type"))) {
                String ql = d.getString("_ql");
                if (ql == null) ql = "select x.id, x.name from " + d.getString("_entityClassName") + " x order by x.name";
                fields.add(new JPAListSelectionField(d.getString("_id"), d.getString("_label"), ql));
            } else if (MetaData.FIELDTYPE_GRID.equals(d.getString("_type"))) {
                List<AbstractColumn> cols = new ArrayList<>();
                for (Data dc : d.getList("_cols")) {
                    cols.add(new OutputColumn(dc.getString("_id"), dc.getString("_label"), 100));
                }
                fields.add(new GridField(d.getString("_id"), d.getString("_label"), cols) {
                    @Override
                    public AbstractForm getDataForm(Data initialData) {
                        AbstractForm f = new AbstractForm() {
                            @Override
                            public Data initializeData() {
                                return (initialData != null)?initialData:super.initializeData();
                            }
                        };
                        buildFromMetadata(f, d.getList("_cols"), false);
                        return f;
                    }
                });
            }
            if (d.containsKey("_required")) {
                for (AbstractField field : fields) field.setRequired(true);
            }
            if (d.containsKey("_startsline")) {
                for (AbstractField field : fields) field.setBeginingOfLine(true);
            }
            if (d.containsKey("_unmodifiable")) {
                for (AbstractField field : fields) field.setUnmodifiable(true);
            }
            for (AbstractField field : fields) f.add(field);
        }
    }

    @Override
    public List<AbstractColumn> createExtraColumns() {
        List<AbstractColumn> cols = new ArrayList<>();
        int poscol = 0;
        for (Data d : getMetadata().getData("_searchform").getList("_columns")) {
            if (poscol > 0 || (!MetaData.FIELDTYPE_ID.equals(d.getString("_type")) && !MetaData.FIELDTYPE_PK.equals(d.getString("_type")))) {
                OutputColumn col;
                cols.add(col = new OutputColumn("col" + poscol, d.getString("_label"), d.getInt("_width")));
                if ("center".equals(d.getString("_align"))) col.setAlignment(ColumnAlignment.CENTER);
                if ("right".equals(d.getString("_align"))) col.setAlignment(ColumnAlignment.RIGHT);
                if (!d.isEmpty("_cellstylegenerator")) try {
                    col.setStyleGenerator((CellStyleGenerator) Class.forName(d.getString("_cellstylegenerator")).newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            poscol++;
        }
        return cols;
    }

    @Override
    public String getSql() {

        // seleccionamos todos los campos y acumulamos los left joins los filtros y los ordenes
        List<Data> selects = new ArrayList<>();
        List<Data> wheres = new ArrayList<>();
        List<Data> orders = new ArrayList<>();

        // construimos la clausula select
        String jpql = "select ";
        int poscol = 0;
        List<String> orderFields = new ArrayList<>();

        List<String> leftJoins = new ArrayList<>();

        // primero buscamos el id o pk
        for (Data d : getMetadata().getData("_searchform").getList("_columns")){
            if (poscol++ > 0) jpql += ",";
            if (!d.isEmpty("_colql")) jpql += d.getString("_colql");
            else if (d.isEmpty("_qlname")) jpql += "x." + d.getString("_id");
            else jpql += "x." + d.getString("_qlname");

            if (!d.isEmpty("_leftjoin") && !leftJoins.contains(d.getString("_leftjoin"))) leftJoins.add(d.getString("_leftjoin"));
            if (!d.isEmpty("_order")) orderFields.add(d.getString("_ordercol"));
        }

        if (!getMetadata().isEmpty("_subclasses")) jpql += ", type(x) ";

        jpql += " from " + getEntityClassName() + " x";

        int i = 1;
        for (String lj : leftJoins) {
            jpql += " left outer join x." + lj + " x" + i++ + " ";
        }


        int posfilter = 0;
        String filters = "";
        Data sfd = getForm().getData();
        for (Data d : getMetadata().getData("_searchform").getList("_fields")) {
            if (MetaData.FIELDTYPE_DATE.equals(d.getString("_type"))) {
                String fx = "";
                LocalDate del = toLocalDate(sfd.get(d.getString("_id") + "_from"));
                LocalDate al = toLocalDate(sfd.get(d.getString("_id") + "_to"));
                DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (del != null) fx += "x." + d.getString("_qlname") + " >= {d '" + del.format(f) + "'}";
                if (al != null) {
                    if (!"".equals(fx)) fx += " and ";
                    fx += "x." + d.getString("_qlname") + " <= {d '" + al.format(f) + "'}";
                }
                if (!"".equals(fx)) {
                    if (posfilter++ == 0) filters += "";
                    else filters += " and ";

                    filters += " " + fx + " ";
                }
            } else if (MetaData.FIELDTYPE_DATETIME.equals(d.getString("_type"))) {
                String fx = "";
                LocalDate del = toLocalDate(sfd.get(d.getString("_id") + "_from"));
                LocalDate al = toLocalDate(sfd.get(d.getString("_id") + "_to"));
                DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (del != null) fx += "x." + d.getString("_qlname") + " >= {d '" + del.format(f) + "'}";
                if (al != null) {
                    al = al.plusDays(1);
                    if (!"".equals(fx)) fx += " and ";
                    fx += "x." + d.getString("_qlname") + " < {d '" + al.format(f) + "'}";
                }
                if (!"".equals(fx)) {
                    if (posfilter++ == 0) filters += "";
                    else filters += " and ";

                    filters += " " + fx + " ";
                }
            } else if (!sfd.isEmpty(d.getString("_id"))) {
                Object v = sfd.get(d.getString("_id"));
                if (d.getBoolean("_useidtoselect")) {
                    if (posfilter++ == 0) filters += "";
                    else filters += " and ";
                    filters += "x." + d.getString("_id");
                    filters += ".id";
                    if ("string".equalsIgnoreCase(d.getString("_idtype"))) {
                        filters += " = '" + v + "' ";
                    } else {
                        filters += " = " + v + " ";
                    }
                } else {
                    if (v instanceof String) {
                        if (posfilter++ == 0) filters += "";
                        else filters += " and ";
                        if (d.getBoolean("_exactmatch")) filters += "x." + d.getString("_qlname") + " = '" + ((String) v).toLowerCase().replaceAll("'", "''") + "'";
                        else {
                            filters += "lower(x." + d.getString("_qlname") + ")";
                            filters += " like '%" + ((String) v).toLowerCase().replaceAll("'", "''") + "%' ";
                        }
                    }
                    else {
                        if (v instanceof Pair) {
                            Object vv = ((Pair)v).getValue();
                            if (vv != null) {
                                if (posfilter++ == 0) filters += "";
                                else filters += " and ";
                                filters += "x." + d.getString("_qlname");
                                if ("enum".equals(d.getString("_type"))) {
                                    filters += " = " + d.getString("_enumtype") + "." + vv + " ";
                                } else {
                                    filters = filters.substring(0, filters.lastIndexOf("."));
                                    filters += ".id";
                                    if (v instanceof String) {
                                        filters += " = '" + vv + "' ";
                                    } else {
                                        filters += " = " + vv + " ";
                                    }
                                }
                            }
                        } else {
                            if (posfilter++ == 0) filters += "";
                            else filters += " and ";
                            filters += "x." + d.getString("_qlname");
                            filters += " = " + v + " ";
                        }
                    }
                }
            }
        }
        if (!"".equals(filters)) jpql += " where " + filters;


        if (orderFields.size() > 0) {
            jpql += " order by ";
            boolean primero = true;
            for (String of : orderFields) {
                if (primero) primero = false;
                else jpql += " , ";
                jpql += " x." + of;
            }
        }

        i = 1;
        for (String lj : leftJoins) {
            jpql = jpql.replaceAll("x\\." + lj.replaceAll("\\.", "\\.") + "\\.", "x" + i++ + ".");
        }

        return jpql;
    }

    private LocalDate toLocalDate(Object o) {
        LocalDate d = null;
        if (o != null) {
            if (o instanceof Date) {
                d = Instant.ofEpochMilli(((Date) o).getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            } else if (o instanceof LocalDate) {
                d = (LocalDate) o;
            }
        }
        return d;
    }

    @Override
    public String getTitle() {
        return getEntityClassName().substring(getEntityClassName().lastIndexOf(".") + 1) + "s";
    }

    @Override
    public AbstractForm createForm() {
        ViewForm f = new ViewForm(this);
        buildFromMetadata(f, getMetadata().getData("_searchform").getList("_fields"), true);
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

    private AbstractAction createAction(AbstractView v, Data da, MDDActionHelper h) {
        return new AbstractAction(da.getString("_name"), da.getBoolean("_callonenterkeypressed")) {
            @Override
            public void run() {

                Data parameters = new Data();

                h.complete(parameters);

                boolean needsParameters = false;

                for (Data dp : da.getList("_parameters")) {
                    String n = dp.getString("_id");
                    if (MetaData.FIELDTYPE_LISTDATA.equals(dp.getString("_type"))) {
                        parameters.set(n, getSelection());
                    } else if (MetaData.FIELDTYPE_DATA.equals(dp.getString("_type"))) {
                        parameters.set(n, v.getForm().getData());
                    } else if (MetaData.FIELDTYPE_USERDATA.equals(dp.getString("_type"))) {
                        parameters.set(n, MateuUI.getApp().getUserData());
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
                            buildFromMetadata(f, da.getData("_form").getList("_fields"), false);
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
        return createAction(v, da, new MDDActionHelper() {
            @Override
            public void onSuccess(Object result) {
                if (result instanceof URL) {
                    MateuUI.open((URL) result);
                } else {
                    v.search();
                }
            }

            @Override
            public void complete(Data parameters) {

            }
        });
    }

    private AbstractAction createAction(AbstractEditorView v, Data da) {
        return createAction(v, da, new MDDActionHelper() {
            @Override
            public void onSuccess(Object result) {
                if (result instanceof URL) {
                    MateuUI.open((URL) result);
                } else if (result instanceof MDDLink) {
                    MDDLink l = (MDDLink) result;
                    switch (l.getActionType()) {
                        case OPENEDITOR:
                            ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData(l.getEntityClassName(),  new MDDCallback(l.getData()) {
                                @Override
                                public void onSuccess(Data result) {
                                    MateuUI.openView(new MDDJPACRUDView(result) {
                                        @Override
                                        public Data initializeData() {
                                            return l.getData();
                                        }
                                    }.getNewEditorView().setInitialId(l.getData().get("_id")) );
                                }
                            });
                            break;
                        case OPENLIST:
                            ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData(l.getEntityClassName(), new MDDCallback(l.getData()));
                            break;
                            default: MateuUI.alert("Unkown operation " + l.getActionType());

                    }
                } else if (result instanceof Data) {
                    v.getForm().setData((Data) result);
                } else if (result instanceof Void || result == null) {
                    MateuUI.notifyDone("Done!");
                } else {
                    MateuUI.alert("" + result);
                }
                v.load();
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
