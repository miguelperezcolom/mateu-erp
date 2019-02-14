package io.mateu.erp.model.population;

import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.generic.Contract;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;

import java.util.List;

/**
 * here we ill put anything that must be done due to model changes (e.g. add a new language)
 *
 *
 * Created by miguel on 13/9/16.
 */
public class Updater {

    public static void main(String[] args) throws Throwable {
        System.setProperty("appconf", "/home/miguel/work/demo.properties");

        update();

        WorkflowEngine.exit(0);
    }

    private static void update() throws Throwable {

        Helper.transact(em -> {

            Accessor.get(em).getGenericContracts().addAll((List<Contract>)em.createQuery("select x from " + Contract.class.getName() + " x").getResultList());

            Accessor.get(em).getTransferContracts().addAll((List<io.mateu.erp.model.product.transfer.Contract>)em.createQuery("select x from " + io.mateu.erp.model.product.transfer.Contract.class.getName() + " x").getResultList());

            Accessor.get(em).getTourContracts().addAll((List<io.mateu.erp.model.product.tour.Contract>)em.createQuery("select x from " + io.mateu.erp.model.product.tour.Contract.class.getName() + " x").getResultList());

        });

    }
}
