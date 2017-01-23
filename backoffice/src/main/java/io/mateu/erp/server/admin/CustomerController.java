package io.mateu.erp.server.admin;

import io.mateu.erp.model.financials.Customer;
import io.mateu.erp.server.JPAServerSideEditorViewController;

/**
 * Created by miguel on 7/1/17.
 */
public class CustomerController extends JPAServerSideEditorViewController {
    @Override
    public Class getModelClass() {
        return Customer.class;
    }

    @Override
    public String getKey() {
        return "customer";
    }
}