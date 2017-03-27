package io.mateu.ui.mdd.server;

import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.core.shared.PairList;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.interfaces.AuditRecord;
import io.mateu.ui.mdd.server.interfaces.Translated;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import io.mateu.ui.mdd.shared.ERPService;
import io.mateu.ui.mdd.shared.MetaData;
import org.apache.commons.beanutils.BeanUtils;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 11/1/17.
 */
public class ERPServiceImpl implements ERPService {
    @Override
    public Object[][] select(String jpql) throws Exception {

        System.out.println("jpql: " + jpql);

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
                    Data r;
                    d.getList("_data").add(r = new Data());
                    if (o.getClass().isArray()) {
                        Object[] l = (Object[]) o;
                        if (l != null) for (int i = 0; i < l.length; i++) {
                            r.set((i == 0)?"_id":"col" + i, l[i]);
                        }
                    } else {
                        r.set("_id", "" + o);
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
                if (jpaql.startsWith("delete")) {
                    for (Object o : em.createQuery(jpaql.replaceFirst("delete", "select x")).getResultList()) {
                        if (o instanceof WithTriggers) ((WithTriggers)o).beforeDelete();

                        for (Field f : getAllFields(o.getClass())) {
                            if (f.getType().isAnnotationPresent(Entity.class)) {
                                Object v = o.getClass().getMethod(getGetter(f)).invoke(o);
                                if (v != null) {
                                    Field parentField = null;
                                    for (Field ff : getAllFields(f.getType())) {
                                        try {
                                            if (ff.isAnnotationPresent(OneToMany.class) && ((ParameterizedType) ff.getGenericType()).getActualTypeArguments()[0].equals(o.getClass())) {
                                                OneToMany a = ff.getAnnotation(OneToMany.class);
                                                if (f.getName().equals(a.mappedBy())) parentField = ff;
                                            }
                                        } catch (Exception e) {

                                        }
                                    }
                                    if (parentField != null) {
                                        if (parentField.isAnnotationPresent(MapKey.class)) {
                                            String keyFieldName = parentField.getAnnotation(MapKey.class).name();
                                            Field keyField = o.getClass().getDeclaredField(keyFieldName);
                                            Object key = o.getClass().getMethod(getGetter(keyField)).invoke(o);
                                            Map m = (Map) v.getClass().getMethod(getGetter(parentField)).invoke(v);
                                            if (m.containsKey(key)) m.remove(key);
                                        } else {
                                            List l = (List) v.getClass().getMethod(getGetter(parentField)).invoke(v);
                                            l.remove(o);
                                        }

                                    }
                                }
                            }
                        }


                        em.remove(o);
                        if (o instanceof WithTriggers) ((WithTriggers)o).afterDelete();
                    }
                } else {
                    r[0] = em.createQuery(jpaql).executeUpdate();
                }
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
                for (Field f : getAllFields(cl)) {
                    if (f.isAnnotationPresent(Id.class)) {
                        idField = f;
                        if (f.isAnnotationPresent(GeneratedValue.class)) {
                            generated = true;
                        }
                        break;
                    }
                }

                boolean newInstance = false;
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
                    newInstance = true;
                }

                if (o instanceof WithTriggers) {
                    ((WithTriggers)o).beforeSet(newInstance);
                }

                data.set("_id", id);

                //auditoría
                for (Field f : getAllFields(cl)) if (AuditRecord.class.isAssignableFrom(f.getType())) {
                    AuditRecord a = (AuditRecord) o.getClass().getMethod(getGetter(f)).invoke(o);
                    if (a == null) {
                        BeanUtils.setProperty(o, f.getName(), a = (AuditRecord) f.getType().newInstance());
                    }
                    a.touch(em, data.getString("_user"));
                }


                for (Field f : getAllFields(cl)) {
                    boolean updatable = true;
                    if (AuditRecord.class.isAssignableFrom(f.getType()) || f.isAnnotationPresent(Output.class) || f.isAnnotationPresent(Ignored.class) || (!newInstance && f.isAnnotationPresent(Unmodifiable.class))) {
                        updatable = false;
                    }

                    if (updatable) {
                        if (data.containsKey(f.getName())) {
                            Object v = data.get(f.getName());
                            if (v != null && v instanceof Pair) v = ((Pair) v).getValue();
                            if (Translated.class.isAssignableFrom(f.getType())) {
                                Object current = o.getClass().getMethod(getGetter(f)).invoke(o);
                                if (current == null) {
                                    current = f.getType().newInstance();
                                    BeanUtils.setProperty(o, f.getName(), current);
                                    em.persist(current);
                                }
                                ((Translated) current).set((String) v);
                            } else {

                                if (f.isAnnotationPresent(ElementCollection.class)) {
                                    List<Object> l = new ArrayList<>();
                                    if (v != null) for (String x : ((String)v).split("\n")) {
                                        l.add(x);
                                    }
                                    v = l;
                                } else if (f.getType().isAnnotationPresent(Entity.class)) {
                                    Field parentField = null;
                                    for (Field ff : getAllFields(f.getType())) {
                                        try {
                                            if (ff.isAnnotationPresent(OneToMany.class) && ((ParameterizedType)ff.getGenericType()).getActualTypeArguments()[0].equals(o.getClass())) {
                                                OneToMany a = ff.getAnnotation(OneToMany.class);
                                                if (f.getName().equals(a.mappedBy())) parentField = ff;
                                            }
                                        } catch (Exception e) {

                                        }
                                    }
                                    if (v != null) {
                                        v = em.find(f.getType(), v);
                                        if (parentField != null) {
                                        /*
    @OneToMany(mappedBy="albergue", cascade = CascadeType.ALL)
    @MapKey(name="fecha")
                                         */
                                            if (parentField.isAnnotationPresent(MapKey.class)) {
                                                String keyFieldName = parentField.getAnnotation(MapKey.class).name();
                                                System.out.println("o = " + o);
                                                System.out.println("o.class = " + o.getClass().getName());
                                                System.out.println("keyFieldName = " + keyFieldName);
                                                Field keyField = o.getClass().getDeclaredField(keyFieldName);
                                                Object key = o.getClass().getMethod(getGetter(keyField)).invoke(o);
                                                System.out.println("key = " + key);
                                                System.out.println("parentField = " + parentField.getName());
                                                Map m = (Map) v.getClass().getMethod(getGetter(parentField)).invoke(v);
                                                System.out.println("m = " + m);
                                                if (!m.containsKey(key)) m.put(key, o);
                                            } else {
                                                List l = (List) v.getClass().getMethod(getGetter(parentField)).invoke(v);
                                                if (!l.contains(o)) l.add(o);
                                            }
                                        }
                                    } else {
                                        if (parentField != null) {
                                            Object current = o.getClass().getMethod(getGetter(f)).invoke(o);
                                            if (current != null) {
                                                if (parentField.isAnnotationPresent(MapKey.class)) {
                                                    String keyFieldName = parentField.getAnnotation(MapKey.class).name();
                                                    Field keyField = o.getClass().getDeclaredField(keyFieldName);
                                                    Object key = o.getClass().getMethod(getGetter(keyField)).invoke(o);
                                                    Map m = (Map) v.getClass().getMethod(getGetter(parentField)).invoke(v);
                                                    if (m.containsKey(key)) m.remove(key);
                                                } else {
                                                    List l = (List) current.getClass().getMethod(getGetter(parentField)).invoke(current);
                                                    l.remove(o);
                                                }
                                            }
                                        }
                                    }
                                } else if (v != null && f.getType().isEnum()) {
                                    for (Object x : f.getType().getEnumConstants()) {
                                        if (v.equals(x.toString())) {
                                            v = x;
                                            break;
                                        }
                                    }
                                } else if (List.class.isAssignableFrom(f.getType())) {
                                ParameterizedType genericType = (ParameterizedType) f.getGenericType();
                                Class<?> genericClass = (Class<?>) genericType.getActualTypeArguments()[0];

                                if (f.isAnnotationPresent(OwnedList.class)) {
                                    // todo: rellenar lista objetos...
                                } else {
                                    List<Object> l = new ArrayList<>();
                                    List<Pair> ll = (v instanceof PairList)?((PairList)v).getValues(): (List<Pair>) v;
                                    for (Pair p : ll) {
                                        l.add(em.find(genericClass, p.getValue()));
                                    }
                                    v = l;
                                }

                            }


                                System.out.println("o." + getSetter(f) + "(" + v + ")");
                                //m.invoke(o, data.get(n));
                                BeanUtils.setProperty(o, f.getName(), v);
                            }
                        }
                    }
                }

                if (o instanceof WithTriggers) {
                    ((WithTriggers)o).afterSet(newInstance);
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

                fill(em, id, data, o);

                for (Method m : o.getClass().getDeclaredMethods()) {
                    if ("toString".equals(m.getName())) {
                        data.set("_tostring", m.invoke(o));
                    }
                }

            }
        });

        return data;
    }

    private void fill(EntityManager em, Object id, Data data, Object o) throws Exception {
        if (id != null) data.set("_id", id);

        for (Field f : getAllFields(o.getClass())) if (!f.isAnnotationPresent(Ignored.class) && !(f.isAnnotationPresent(Id.class) && f.isAnnotationPresent(GeneratedValue.class))) {
            boolean uneditable = false;
            if (f.isAnnotationPresent(Output.class) || f.isAnnotationPresent(Unmodifiable.class)) {
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
                    ok |= v instanceof Long;
                    ok |= v instanceof Double;
                    ok |= v instanceof Integer;
                    ok |= v instanceof Boolean;
                    ok |= v instanceof LocalDate;
                    ok |= v instanceof LocalDateTime;
                    if (v.getClass().isAnnotationPresent(Embeddable.class)) {
                        Method mts;
                        if ((mts = v.getClass().getMethod("toString")) != null) {
                            v = mts.invoke(v);
                        }
                        ok = true;
                    }
                    if (Translated.class.isAssignableFrom(v.getClass())) {
                        v = ((Translated) v).get();
                        ok = true;
                    } else if (v.getClass().isAnnotationPresent(Entity.class)) {
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
                    if (f.isAnnotationPresent(ElementCollection.class)) {
                        StringBuffer sb = new StringBuffer();
                        boolean primero = true;
                        for (Object x : (List<Object>)v) {
                            if (primero) primero = false; else sb.append("\n");
                            sb.append(x);
                        }
                        v = sb.toString();
                        ok = true;
                    } else if (List.class.isAssignableFrom(f.getType())) {
                        ParameterizedType genericType = (ParameterizedType) f.getGenericType();
                        Class<?> genericClass = (Class<?>) genericType.getActualTypeArguments()[0];

                        if (f.isAnnotationPresent(OwnedList.class)) {
                            List<Data> dl = new ArrayList<>();

                            List l = (List) v;
                            for (Object x : l) {
                                Data dx = new Data();
                                fill(em, getId(x), dx, x);
                                dl.add(dx);
                            }

                            v = dl;
                        } else {
                            List<Pair> dl = new ArrayList<>();

                            List l = (List) v;
                            for (Object x : l) {
                                dl.add(new Pair(getId(x), "" + x));
                            }

                            PairList pl = new PairList();
                            pl.setValues(dl);
                            v = pl;
                        }

                        ok = true;
                    }

                    if (ok) data.set(f.getName(), v);
                }
            }
        }
    }

    private Object getId(Object o) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object id = null;
        for (Field f : o.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class)) {
                id = o.getClass().getMethod(getGetter(f)).invoke(o);
                break;
            }
        }
        return id;
    }


    @Override
    public Data getMetaData(String entityClassName) throws Exception {
        Data data = new Data();
        data.set("_entityClassName", entityClassName);

        Class c = Class.forName(entityClassName);

        List<Data> searchFormFields = new ArrayList<>();
        List<Data> listColumns = new ArrayList<>();
        List<Data> editorFormFields = new ArrayList<>();
        List<Data> staticActions = new ArrayList<>();
        List<Data> actions = new ArrayList<>();



        for (Field f : getAllFields(c)) {
            if (f.isAnnotationPresent(SearchFilter.class)) {
                addField(searchFormFields, new FieldInterfaced() {
                    @Override
                    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
                        return f.isAnnotationPresent(annotationClass);
                    }

                    @Override
                    public Class<?> getType() {
                        return f.getType();
                    }

                    @Override
                    public Class<?> getGenericClass() {
                        ParameterizedType genericType = (ParameterizedType) f.getGenericType();
                        Class<?> genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
                        return genericClass;
                    }

                    @Override
                    public Class<?> getDeclaringClass() {
                        return f.getDeclaringClass();
                    }

                    @Override
                    public String getName() {
                        return f.getName();
                    }

                    @Override
                    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                        return f.getAnnotation(annotationClass);
                    }
                });
            }
        }

        for (Field f : getAllFields(c)) {
            if (!f.isAnnotationPresent(Ignored.class) && !(f.isAnnotationPresent(Id.class) && f.isAnnotationPresent(GeneratedValue.class))) {
                addField(editorFormFields, new FieldInterfaced() {
                    @Override
                    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
                        return f.isAnnotationPresent(annotationClass);
                    }

                    @Override
                    public Class<?> getType() {
                        return f.getType();
                    }

                    @Override
                    public Class<?> getGenericClass() {
                        ParameterizedType genericType = (ParameterizedType) f.getGenericType();
                        Class<?> genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
                        return genericClass;
                    }

                    @Override
                    public Class<?> getDeclaringClass() {
                        return f.getDeclaringClass();
                    }

                    @Override
                    public String getName() {
                        return f.getName();
                    }

                    @Override
                    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                        return f.getAnnotation(annotationClass);
                    }
                });
            }
        }

        boolean hayListColumns = false;
        for (Field f : getAllFields(c)) {
            if (f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(ListColumn.class) || f.isAnnotationPresent(SearchFilter.class)) {
                hayListColumns |= f.isAnnotationPresent(ListColumn.class);
                addColumn(listColumns, f);
            }
        }

        if (!hayListColumns) {
            listColumns.clear();
            for (Field f : getAllFields(c)) {
                if (!(f.isAnnotationPresent(OneToMany.class) || f.isAnnotationPresent(MapKey.class) || f.isAnnotationPresent(ElementCollection.class) || f.isAnnotationPresent(NotInList.class)))
                    addColumn(listColumns, f);
            }
        }




        for (Method m : c.getDeclaredMethods()) {
            if (Modifier.isStatic(m.getModifiers())) {
                addMethod(staticActions, m);
            } else {
                addMethod(actions, m);
            }
        }




        Data dsf;
        data.set("_searchform", dsf = new Data());
        dsf.set("_fields", searchFormFields);
        dsf.set("_columns", listColumns);
        data.set("_actions", staticActions);
        Data def;
        data.set("_editorform", def = new Data());
        def.set("_fields", editorFormFields);
        def.set("_actions", actions);


        return data;
    }

    private List<Field> getAllFields(Class c) {
        List<Field> l = new ArrayList<>();

        if (c.getSuperclass() != null && c.getSuperclass().isAnnotationPresent(Entity.class)) l.addAll(getAllFields(c.getSuperclass()));

        for (Field f : c.getDeclaredFields()) l.add(f);

        return l;
    }

    @Override
    public Object runInServer(String className, String methodName, Data parameters) throws Exception {
        Class c = Class.forName(className);
        Method m = null;
        for (Method x : c.getDeclaredMethods()) if (x.getName().equals(methodName)) {
            m = x;
            break;
        }
        Object o = null;
        Object[] r = {null};

        List<Object> vs = new ArrayList<>();
        for (Parameter p : m.getParameters()) {
            if (p.isAnnotationPresent(Selection.class)) {
                vs.add(parameters.get("_selection"));
            } else {
                vs.add(parameters.get(p.getName()));
            }
        }
        Object[] args = vs.toArray();

        if (!Modifier.isStatic(m.getModifiers())) {
            Method finalM = m;
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Exception {
                    r[0] = finalM.invoke(em.find(c, parameters.get("_id")), args);
                }
            });
        } else r[0] = m.invoke(o, args);

        return r[0];
    }

    private void addMethod(List<Data> actions, Method m) {
        if (m.isAnnotationPresent(Action.class)) {
            List<Data> parameters = new ArrayList<>();
            for (Parameter p : m.getParameters()) {
                addField(parameters, new FieldInterfaced() {
                    @Override
                    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
                        return p.isAnnotationPresent(annotationClass);
                    }

                    @Override
                    public Class<?> getType() {
                        return p.getType();
                    }

                    @Override
                    public Class<?> getGenericClass() {
                        return (Class<?>) ((ParameterizedType)p.getParameterizedType()).getActualTypeArguments()[0];
                    }

                    @Override
                    public Class<?> getDeclaringClass() {
                        return m.getDeclaringClass();
                    }

                    @Override
                    public String getName() {
                        return p.getName();
                    }

                    @Override
                    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                        return p.getAnnotation(annotationClass);
                    }
                });
            }
            Data a = new Data();
            a.set("_entityClassName", m.getDeclaringClass().getName());
            a.set("_name", m.getAnnotation(Action.class).name());
            a.set("_methodname", m.getName());
            a.set("_parameters", parameters);
            Data def;
            a.set("_form", def = new Data());
            def.set("_fields", parameters);
            if (Void.class.equals(m.getReturnType())) a.set("_returntype", "void");
            else a.set("_returntype", m.getReturnType().getCanonicalName());
            actions.add(a);
        }
    }

    private void addColumn(List<Data> listColumns, Field f) {
        addField(listColumns, new FieldInterfaced() {
            @Override
            public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
                return f.isAnnotationPresent(annotationClass);
            }

            @Override
            public Class<?> getType() {
                return f.getType();
            }

            @Override
            public Class<?> getGenericClass() {
                ParameterizedType genericType = (ParameterizedType) f.getGenericType();
                Class<?> genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
                return genericClass;
            }

            @Override
            public Class<?> getDeclaringClass() {
                return f.getDeclaringClass();
            }

            @Override
            public String getName() {
                return f.getName();
            }

            @Override
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                return f.getAnnotation(annotationClass);
            }
        });
    }

    private void addField(List<Data> _fields, FieldInterfaced f) {
        if (!f.isAnnotationPresent(Ignored.class)) {

            Data d = new Data();
            boolean upload = false;

            d.set("_qlname", f.getName());

            if (f.isAnnotationPresent(Required.class)) {
                d.set("_required", true);
            }

            if (f.isAnnotationPresent(Tab.class)) {
                d.set("_starttab", f.getAnnotation(Tab.class).value());
            }

            if (f.isAnnotationPresent(EndTabs.class)) {
                d.set("_endtabs", true);
            }

            if (f.isAnnotationPresent(Unmodifiable.class)) {
                d.set("_unmodifiable", true);
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
            } else if (LocalDate.class.equals(f.getType())) {
                d.set("_type", MetaData.FIELDTYPE_DATE);
                upload = true;
            } else if (Date.class.equals(f.getType()) || LocalDateTime.class.equals(f.getType())) {
                d.set("_type", MetaData.FIELDTYPE_DATETIME);
                upload = true;
            } else if ("double".equals(f.getType().getName()) || Double.class.equals(f.getType())) {
                d.set("_type", MetaData.FIELDTYPE_DOUBLE);
                upload = true;
            } else if ("boolean".equals(f.getType().getName()) || Boolean.class.equals(f.getType())) {
                d.set("_type", MetaData.FIELDTYPE_BOOLEAN);
                upload = true;
            } else if (Translated.class.isAssignableFrom(f.getType())) {
                d.set("_type", MetaData.FIELDTYPE_STRING);
                upload = true;
            } else if (f.isAnnotationPresent(ElementCollection.class)) {
                d.set("_type", MetaData.FIELDTYPE_TEXTAREA);
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

                    for (Field ff : getAllFields(f.getType())) if ("name".equals(ff.getName()) || "title".equals(ff.getName())) d.set("_qlname", f.getName() + "." + ff.getName());

                    if (f.getType().isAnnotationPresent(UseIdToSelect.class)) {
                        d.set("_useidtoselect", true);
                        String ql = f.getType().getAnnotation(UseIdToSelect.class).ql();
                        if (ql != null && !"".equals(ql.trim())) d.set("_ql", ql);
                    } else if (f.getType().isAnnotationPresent(QLForCombo.class)) {
                        String ql = f.getType().getAnnotation(QLForCombo.class).ql();
                        if (ql != null && !"".equals(ql.trim())) d.set("_ql", ql);
                    }
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
                    if (f.isAnnotationPresent(OwnedList.class)) {
                        d.set("_type", MetaData.FIELDTYPE_GRID);
                        List<Data> cols = new ArrayList<>();

                        for (Field ff : f.getGenericClass().getDeclaredFields()) {
                            if (!ff.isAnnotationPresent(Id.class) && !ff.getType().equals(f.getDeclaringClass())) addColumn(cols, ff);
                        }
                        d.set("_cols", cols);
                    } else {
                        d.set("_type", MetaData.FIELDTYPE_LIST);
                        d.set("_entityClassName", f.getGenericClass().getCanonicalName());
                        if (f.getGenericClass().isAnnotationPresent(QLForCombo.class)) {
                            String ql = f.getGenericClass().getAnnotation(QLForCombo.class).ql();
                            if (ql != null && !"".equals(ql.trim())) d.set("_ql", ql);
                        }
                    }
                    upload = true;
                }
            }
            if (upload) {
                d.set("_id", f.getName());

                //ancho columna y alineado
                int ancho = 200;
                String alineado = "left";
                if ("int".equals(f.getType().getName()) || "long".equals(f.getType().getName()) || Integer.class.equals(f.getType())) {
                    ancho = 80;
                    alineado = "right";
                } else if (Date.class.equals(f.getType()) || LocalDate.class.equals(f.getType())) {
                    ancho = 122;
                } else if ("double".equals(f.getType().getName()) || Double.class.equals(f.getType())) {
                    ancho = 80;
                    alineado = "right";
                } else if ("boolean".equals(f.getType().getName()) || Boolean.class.equals(f.getType())) {
                    ancho = 60;
                }
                d.set("_width", ancho);
                d.set("_align", alineado);

                if (f.isAnnotationPresent(StartsLine.class)) {
                    d.set("_startsline", true);
                }

                if (f.isAnnotationPresent(Caption.class)) {
                    d.set("_label", f.getAnnotation(Caption.class).value());
                } else d.set("_label", capitalize(f.getName()));
                _fields.add(d);
            }

        }
    }

    private String capitalize(String s) {
        if (s == null || "".equals(s)) return s;
        String c = s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        ).toLowerCase();
        if (c.length() > 1) c = c.substring(0, 1).toUpperCase() + c.substring(1);

        return c;
    }

    public static void main(String... args) throws Exception {
        //System.out.println(new ERPServiceImpl().getMetaData(Actor.class.getCanonicalName()));
    }

}
