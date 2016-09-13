package mateu.erp.model.util;

import javax.persistence.EntityManager;

/**
 * Created by miguel on 13/9/16.
 */
public interface Transactionable {

    public void run(EntityManager em) throws Exception;

}
