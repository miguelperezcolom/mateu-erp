package io.mateu.erp.dispo.interfaces.common;

import io.mateu.erp.dispo.interfaces.integrations.IIntegration;

import java.util.List;

public interface IPartner {

    public long getId();

    public String getName();

    public boolean isActive();

    public List<? extends IIntegration> getIntegrations();

}
