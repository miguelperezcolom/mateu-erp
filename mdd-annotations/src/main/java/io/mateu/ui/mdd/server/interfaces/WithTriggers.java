package io.mateu.ui.mdd.server.interfaces;

/**
 * Created by miguel on 2/3/17.
 */
public interface WithTriggers {

    public void beforeSet(boolean isNew) throws Exception;

    public void afterSet(boolean isNew) throws Exception;

    public void beforeDelete() throws Exception;

    public void afterDelete() throws Exception;

}
