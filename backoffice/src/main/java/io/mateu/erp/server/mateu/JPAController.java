package io.mateu.erp.server.mateu;

import io.mateu.erp.server.JPAServerSideEditorViewController;

/**
 * Created by miguel on 11/1/17.
 */
public class JPAController extends JPAServerSideEditorViewController {
    @Override
    public Class getModelClass() {
        return null;
    }

    @Override
    public String getKey() {
        return "jpa";
    }
}
