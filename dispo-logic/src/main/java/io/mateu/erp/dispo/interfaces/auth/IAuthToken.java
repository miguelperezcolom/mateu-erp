package io.mateu.erp.dispo.interfaces.auth;

import io.mateu.erp.dispo.interfaces.common.IActor;

public interface IAuthToken {

    public boolean isActive();

    public String getId();

    public IActor getActor();

}
