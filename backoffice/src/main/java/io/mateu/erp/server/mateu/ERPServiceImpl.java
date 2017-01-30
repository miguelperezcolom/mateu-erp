package io.mateu.erp.server.mateu;

import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import io.mateu.erp.shared.mateu.ERPService;
import io.mateu.erp.shared.mateu.MetaData;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.mdd.serverside.annotations.*;
import org.apache.commons.beanutils.BeanUtils;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by miguel on 11/1/17.
 */
public class ERPServiceImpl implements ERPService {
    @Override
    public Object[][] select(String jpql) throws Exception {

        List<Object[]> r = new ArrayList<>();

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                Query q = em.createQuery(jpql);
                List rs = q.getResultList();
                for (Object o : rs) {
                    r.add((Object[]) o);
                }

            }
        });


        return r.toArray(new Object[0][]);
    }

    @Override
    public Object selectSingleValue(String jpql) throws Exception {
        return null;
    }

    @Override
    public Data selectPaginated(Data parameters) throws Exception {
        Data d = new Data();

        int rowsPerPage = parameters.getInt("_rowsperpage");
        int fromRow = rowsPerPage * parameters.getInt("_currentpageindex");
        String jpql = parameters.getString("_sql");

        d.getList("_data");

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                Query q = em.createQuery(jpql);
                q.setFirstResult(fromRow);
                q.setMaxResults(rowsPerPage);
                List rs = q.getResultList();
                for (Object o : rs) {
                    Object[] l = (Object[]) o;
                    Data r;
                    d.getList("_data").add(r = new Data());
                    if (l != null) for (int i = 0; i < l.length; i++) {
                        r.set((i == 0)?"_id":"col" + i, l[i]);
                    }
                }

                int numRows = q.getMaxResults();
                //d.set("_data_currentpageindex", from);
                d.set("_data_pagecount", numRows / rowsPerPage + ((numRows % rowsPerPage == 0)?0:1));
            }
        });


        return d;

    }

    @Override
    public int executeUpdate(String jpaql) throws Exception {
        final int[] r = {0};
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                r[0] = em.createQuery(jpaql).executeUpdate();
            }
        });
        return r[0];
    }

    @Override
    public Data set(String serverSideControllerKey, String entityClassName, Data data) throws Exception {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                Object o = null;

                Class cl = Class.forName(entityClassName);

                Field idField = null;
                boolean generated = false;
                for (Field f : cl.getDeclaredFields()) {
                    if (f.isAnnotationPresent(Id.class)) {
                        idField = f;
                        if (f.isAnnotationPresent(GeneratedValue.class)) {
                            generated = true;
                        }
                        break;
                    }
                }

                Object id = data.get("_id");
                if (id != null) {
                    o = em.find(Class.forName(entityClassName), (id instanceof Integer)?new Long((Integer)id):id);
                } else {
                    o = Class.forName(entityClassName).newInstance();
                    em.persist(o);
                    if (generated) {
                        em.flush(); // to get the id
                        Method m = o.getClass().getMethod(getGetter(idField));
                        id = m.invoke(o);
                    } else {
                        id = data.get(idField.getName());
                    }
                }

                data.set("_id", id);

                for (Field f : cl.getDeclaredFields()) {
                    boolean updatable = true;
                    if (f.isAnnotationPresent(Output.class)) {
                        updatable = false;
                    }

                    if (updatable) {
                        if (data.containsKey(f.getName())) {
                            Object v = data.get(f.getName());
                            if (v != null && v instanceof Pair) v = ((Pair)v).getValue();
                            if (f.getType().isAnnotationPresent(Entity.class)) {
                                Field parentField = null;
                                for (Field ff : f.getType().getDeclaredFields()) {
                                    if (ff.isAnnotationPresent(OneToMany.class)) {
                                        OneToMany a = ff.getAnnotation(OneToMany.class);
                                        if (f.getName().equals(a.mappedBy())) parentField = ff;
                                    }
                                }
                                if (v != null) {
                                    v = em.find(f.getType(), v);
                                    if (parentField != null) {
                                        List l = (List) v.getClass().getMethod(getGetter(parentField)).invoke(v);
                                        if (!l.contains(o)) l.add(o);
                                    }
                                } else {
                                    if (parentField != null) {
                                        Object current = o.getClass().getMethod(getGetter(f)).invoke(o);
                                        if (current != null) {
                                            List l = (List) current.getClass().getMethod(getGetter(parentField)).invoke(current);
                                            l.remove(o);
                                        }
                                    }
                                }
                            }
                            if (v != null && f.getType().isEnum()) {
                                for (Object x : f.getType().getEnumConstants()) {
                                    if (v.equals(x.toString())) {
                                        v = x;
                                        break;
                                    }
                                }
                            }
                            System.out.println("o." + getSetter(f) + "(" + v + ")");
                            //m.invoke(o, data.get(n));
                            BeanUtils.setProperty(o,f.getName(),v);
                        }
                    }
                }

            }
        });


        return get(serverSideControllerKey, entityClassName, data.get("_id"));
    }

    private String getGetter(Field f) {
        return (("boolean".equals(f.getType().getName()) || Boolean.class.equals(f.getType()))?"is":"get") + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
    }

    private String getSetter(Field f) {
        return "set" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
    }
    @Override
    public Data get(String serverSideControllerKey, String entityClassName, Object id) throws IllegalAccessException, InstantiationException, Exception {
        Data data = new Data();

        Helper.notransact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                Object o = em.find(Class.forName(entityClassName), (id instanceof Integer)?new Long((Integer)id):id);

                data.set("_id", id);

                for (Field f : o.getClass().getDeclaredFields()) {
                    boolean uneditable = false;
                    if (f.isAnnotationPresent(Output.class)) {
                        uneditable = false;
                    }

                    if (!uneditable) {
                        Object v = o.getClass().getMethod(getGetter(f)).invoke(o);
                        //Object v = BeanUtils.getProperty(o,f.getName());
                        if (v != null) {
                            boolean ok = false;
                            ok |= v.getClass().isPrimitive();
                            ok |= v instanceof String;
                            ok |= v instanceof Integer;
                            ok |= v instanceof Double;
                            ok |= v instanceof Integer;
                            ok |= v instanceof Boolean;
                            if (v.getClass().isAnnotationPresent(Embeddable.class)) {
                                Method mts;
                                if ((mts = v.getClass().getMethod("toString")) != null) {
                                    v = mts.invoke(v);
                                }
                                ok = true;
                            }
                            if (v.getClass().isAnnotationPresent(Entity.class)) {
                                v = new Pair(em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(v), v.toString());
                                ok = true;
                            }
                            if (f.getType().isEnum()) {
                                for (Object x : f.getType().getEnumConstants()) {
                                    if (x.equals(v)) {
                                        v = new Pair("" + x, "" + x);
                                        ok = true;
                                        break;
                                    }
                                }
                            }
                            if (ok) data.set(f.getName(), v);
                        }
                    }
                }

            }
        });

        return data;
    }

    @Override
    public Data getMetaData(String entityClassName) throws Exception {
        Data data = new Data();
        data.set("_entityClassName", entityClassName);

        Class c = Class.forName(entityClassName);

        List<Data> searchFormFields = new ArrayList<>();
        List<Data> listColumns = new ArrayList<>();
        List<Data> editorFormFields = new ArrayList<>();

        for (Field f : c.getDeclaredFields()) {
            if (f.isAnnotationPresent(SearchFilter.class)) {
                addField(searchFormFields, f);
            }
        }

        for (Field f : c.getDeclaredFields()) {
            if (!f.isAnnotationPresent(Ignored.class)) {
                addField(editorFormFields, f);
            }
        }

        for (Field f : c.getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(ListColumn.class) || f.isAnnotationPresent(SearchFilter.class)) {
                addColumn(listColumns, f);
            }
        }

        if (listColumns.size() <= 1) for (Field f : c.getDeclaredFields()) {
            addColumn(listColumns, f);
        }

        Data dsf;
        data.set("_searchform", dsf = new Data());
        dsf.set("_fields", searchFormFields);
        dsf.set("_columns", listColumns);
        Data def;
        data.set("_editorform", def = new Data());
        def.set("_fields", editorFormFields);


        return data;
    }

    private void addColumn(List<Data> listColumns, Field f) {
        addField(listColumns, f);
    }

    private void addField(List<Data> _fields, Field f) {
        if (!f.isAnnotationPresent(Ignored.class)) {

            Data d = new Data();
            boolean upload = false;

            if (f.isAnnotationPresent(Required.class)) {
                d.set("_required", true);
            }

            if (f.isAnnotationPresent(Output.class)) {
                d.set("_type", MetaData.FIELDTYPE_OUTPUT);
                upload = true;
            } else if (f.isAnnotationPresent(TextArea.class)) {
                d.set("_type", MetaData.FIELDTYPE_TEXTAREA);
                upload = true;
            } else if (f.isAnnotationPresent(Id.class)) {
                if (f.isAnnotationPresent(GeneratedValue.class)) {
                    d.set("_type", MetaData.FIELDTYPE_ID);
                    upload = true;
                } else {
                    d.set("_type", MetaData.FIELDTYPE_PK);
                    upload = true;
                }
            } else if ("int".equals(f.getType().getName()) || "long".equals(f.getType().getName()) || Integer.class.equals(f.getType())) {
                d.set("_type", MetaData.FIELDTYPE_INTEGER);
                upload = true;
            } else if (String.class.equals(f.getType())) {
                d.set("_type", MetaData.FIELDTYPE_STRING);
                upload = true;
            } else if (Date.class.equals(f.getType())) {
                d.set("_type", MetaData.FIELDTYPE_DATE);
                upload = true;
            } else if ("double".equals(f.getType().getName()) || Double.class.equals(f.getType())) {
                d.set("_type", MetaData.FIELDTYPE_DOUBLE);
                upload = true;
            } else if ("boolean".equals(f.getType().getName()) || Boolean.class.equals(f.getType())) {
                d.set("_type", MetaData.FIELDTYPE_BOOLEAN);
                upload = true;
            } else {
                boolean isEntity = false;
                for (Annotation a : f.getType().getAnnotations()) {
                    if (a.annotationType().equals(Entity.class)) {
                        isEntity = true;
                    }
                }
                if (isEntity) {
                    d.set("_type", MetaData.FIELDTYPE_ENTITY);
                    d.set("_entityClassName", f.getType().getCanonicalName());
                    upload = true;
                } else if (f.getType().isEnum()) {
                    d.set("_type", MetaData.FIELDTYPE_ENUM);
                    List<Pair> values = new ArrayList<>();
                    for (Object x : f.getType().getEnumConstants()) {
                        values.add(new Pair("" + x, "" + x));
                    }
                    d.set("_values", values);
                    upload = true;
                } else if (List.class.isAssignableFrom(f.getType())) {
                    d.set("_type", MetaData.FIELDTYPE_LIST);
                    List<Data> cols = new ArrayList<>();
                    ParameterizedType genericType = (ParameterizedType) f.getGenericType();
                    Class<?> genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
                    for (Field ff : genericClass.getDeclaredFields()) {
                        if (!ff.isAnnotationPresent(Id.class) && !ff.getType().equals(f.getDeclaringClass())) addColumn(cols, ff);
                    }
                    d.set("_cols", cols);
                    upload = true;
                }
            }
            if (upload) {
                d.set("_id", f.getName());
                d.set("_label", f.getName());
                _fields.add(d);
            }

        }
    }

    public static void main(String... args) throws Exception {
        System.out.println(new ERPServiceImpl().getMetaData(Actor.class.getCanonicalName()));
    }

}
