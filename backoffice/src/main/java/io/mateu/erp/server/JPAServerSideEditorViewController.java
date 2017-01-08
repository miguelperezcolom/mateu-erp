package io.mateu.erp.server;

import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import io.mateu.ui.core.server.ServerSideEditorViewController;
import io.mateu.ui.core.shared.Data;
import org.apache.commons.beanutils.BeanUtils;

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
                    Method m = o.getClass().getMethod("getId");
                    id = m.invoke(o);
                    data.set("_id", id);
                }

                for (Method m : o.getClass().getMethods()) {
                    if (!m.getName().equals("setId") && m.getName().startsWith("set")) {
                        String n = m.getName().substring("set".length()).toLowerCase();
                        System.out.println("o." + m.getName() + "(" + data.get(m.getName().substring("set".length()).toLowerCase()) + ")");
                        //m.invoke(o, data.get(n));
                        BeanUtils.setProperty(o,n,data.get(n));
                    }
                }

            }
        });


        return data.get("_id");
    }

   public abstract Class getModelClass();
}
