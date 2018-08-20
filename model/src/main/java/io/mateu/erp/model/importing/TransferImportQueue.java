package io.mateu.erp.model.importing;



import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by Antonia on 26/03/2017.
 */
public class TransferImportQueue {

    public static void main(String... args) throws Throwable {
        run();
    }

    public static void run() throws Throwable {
//hace un select de las tareas en estado pending
        //por cada una llama a execute()
        List<Object> ids = Helper.selectObjects("select x.id from TransferImportTask x " +
                " where  x.status = io.mateu.erp.model.importing.TransferImportTask.STATUS.PENDING");

        for (Object id : ids) {
            try {
                Helper.transact(new JPATransaction() {
                    @Override
                    public void run(EntityManager em) throws Exception {
                        TransferImportTask t = em.find(TransferImportTask.class, id);
                        t.execute(em);
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();

            }
        }
    }
}
