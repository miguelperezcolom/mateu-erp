package io.mateu.erp.server.admin;

import io.mateu.erp.model.partners.Partner;
import io.mateu.ui.mdd.server.JPAServerSideEditorViewController;

/**
 * Created by miguel on 7/1/17.
 */
public class CustomerController extends JPAServerSideEditorViewController {
    @Override
    public Class getModelClass() {
        return Partner.class;
    }

    @Override
    public String getKey() {
        return "customer";
    }
}
