package io.mateu.ui.mdd.server;

import io.mateu.ui.core.server.ServerSideEditorViewController;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import org.apache.commons.beanutils.BeanUtils;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.lang.reflect.Method;

/**
 * Created by miguel on 7/1/17.
 */
public abstract class JPAServerSideEditorViewController extends ServerSideEditorViewController {

    @Override
    public Data get(Object id) throws Exception {
        Data data = new Data();

        Helper.notransact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                Object o = em.find(getModelClass(), id);

                data.set("_id", id);

                for (Method m : o.getClass().getMethods()) {
                    if (!m.getName().equals("getId") && m.getName().startsWith("get")) {
                        String n = m.getName().substring("get".length()).toLowerCase();
                        Object v = m.invoke(o);
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
                            if (em.contains(v)) {
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
    public Object set(Data data) throws Exception {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                Object o = null;

                Object id = data.get("_id");
                if (id != null) {
                    o = em.find(getModelClass(), id);
                } else {
                    o = getModelClass().newInstance();
                    em.persist(o);
                    em.flush(); // to get the id
                    Method m = o.getClass().getMethod("getId");
                    id = m.invoke(o);
                    data.set("_id", id);
                }

                for (Method m : o.getClass().getMethods()) {
                    if (!m.getName().equals("setId") && m.getName().startsWith("set")) {
                        String n = m.getName().substring("set".length()).toLowerCase();
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


        return data.get("_id");
    }

   public abstract Class getModelClass();
}
