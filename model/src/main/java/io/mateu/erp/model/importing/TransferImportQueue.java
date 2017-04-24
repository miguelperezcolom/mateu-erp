package io.mateu.erp.model.importing;

import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPAHelper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonia on 26/03/2017.
 */
public class TransferImportQueue {

    public static void main(String... args) throws Throwable {
        //hace un select de las tareas en estado pending
        //por cada una llama a execute()
        Object[] ids = JPAHelper.selectObjects("select x.id from TransferImportTask x " +
                //" where  x.status = io.mateu.erp.model.importing.TransferImportTask.STATUS.OK or x.status = io.mateu.erp.model.importing.TransferImportTask.STATUS.PENDING or x.status = io.mateu.erp.model.importing.TransferImportTask.STATUS.ERROR");
                " where x.status = io.mateu.erp.model.importing.TransferImportTask.STATUS.PENDING");
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
