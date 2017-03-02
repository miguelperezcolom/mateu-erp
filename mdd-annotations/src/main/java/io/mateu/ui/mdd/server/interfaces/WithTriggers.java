package io.mateu.ui.mdd.server.interfaces;

/**
 * Created by miguel on 2/3/17.
 */
public interface WithTriggers {

    public void beforeSet();

    public void afterSet();

    public void beforeDelete();

    public void afterDelete();

}
