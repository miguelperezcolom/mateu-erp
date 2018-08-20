package io.mateu.erp.dispo.interfaces.portfolio;

public interface IResource {

    public long getId();

    public String getName();

    public boolean isActive();

    public ResourceType getType();

    public long getIntegrationId();

    public String getForeignId();
}
