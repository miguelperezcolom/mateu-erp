package io.mateu.erp.server.mateu;

import io.mateu.erp.model.financials.Customer;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import io.mateu.erp.shared.mateu.ERPService;
import io.mateu.erp.shared.mateu.MetaData;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import org.apache.commons.beanutils.BeanUtils;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

                Object id = data.get("_id");
                if (id != null) {
                    o = em.find(Class.forName(entityClassName), (id instanceof Integer)?new Long((Integer)id):id);
                } else {
                    o = Class.forName(entityClassName).newInstance();
                    em.persist(o);
                    em.flush(); // to get the id
                    Method m = o.getClass().getMethod("getId");
                    id = m.invoke(o);
                    data.set("_id", id);
                }

                for (Method m : o.getClass().getMethods()) {
                    if (!m.getName().equals("setId") && m.getName().startsWith("set")) {
                        String n = m.getName().substring("set".length());
                        if (n.length() > 1) n = n.substring(0, 1).toLowerCase() + n.substring(1);
                        else n = n.toLowerCase();
                        Object v = data.get(n);
                        if (v != null && m.getParameterTypes()[0].isAnnotationPresent(Entity.class)) {
                            v = em.find(m.getParameterTypes()[0], (v instanceof Pair)?((Pair)v).getValue():v);
                        }
                        System.out.println("o." + m.getName() + "(" + v + ")");
                        //m.invoke(o, data.get(n));
                        BeanUtils.setProperty(o,n,v);
                    }
                }

            }
        });


        return get(serverSideControllerKey, entityClassName, data.get("_id"));
    }

    @Override
    public Data get(String serverSideControllerKey, String entityClassName, Object id) throws IllegalAccessException, InstantiationException, Exception {
        Data data = new Data();

        Helper.notransact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                Object o = em.find(Class.forName(entityClassName), (id instanceof Integer)?new Long((Integer)id):id);

                data.set("_id", id);

                for (Method m : o.getClass().getMethods()) {
                    if (!m.getName().equals("getId") && m.getName().startsWith("get")) {
                        String n = m.getName().substring("get".length());
                        if (n.length() > 1) n = n.substring(0, 1).toLowerCase() + n.substring(1);
                        else n = n.toLowerCase();
                        Object v = m.invoke(o);
                        if (v != null) {
                            boolean ok = false;
                            ok |= v.getClass().isPrimitive();
                            ok |= v instanceof String;
                            ok |= v instanceof Integer;
                            ok |= v instanceof Double;
                            ok |= v instanceof Integer;
                            ok |= v instanceof Boolean;
                            if (v.getClass().isAnnotationPresent(Entity.class)) {
                                v = new Pair(em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(v), v.toString());
                                ok = true;
                            }
                            if (ok) data.set(n, v);
                        }
                        //data.set(n, BeanUtils.getProperty(o,n));
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

        List<Data> _fields = new ArrayList<>();

        Class c = Class.forName(entityClassName);

        for (Field f : c.getDeclaredFields()) {
            Data d = new Data();
            boolean upload = false;

            if (f.isAnnotationPresent(Id.class)) {
                d.set("_type", MetaData.FIELDTYPE_ID);
                upload = true;
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
                d.set("_type", MetaData.FIELDTYPE_ENTITY);
                d.set("_entityClassName", f.getType().getCanonicalName());
                upload = true;
            }
            if (upload) {
                d.set("_id", f.getName());
                d.set("_label", f.getName());
                _fields.add(d);
            }
        }

        data.set("_fields", _fields);

        return data;
    }

    public static void main(String... args) throws Exception {
        System.out.println(new ERPServiceImpl().getMetaData(Customer.class.getCanonicalName()));
    }

}
