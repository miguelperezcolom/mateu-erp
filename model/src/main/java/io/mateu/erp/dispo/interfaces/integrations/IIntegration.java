package io.mateu.erp.dispo.interfaces.integrations;

public interface IIntegration {

    public long getId();

    public String getName();

    public String getBaseUrl();

    public boolean isActive();

    public boolean isProvidingHotels();

    int getMaxResourcesPerRequest();
}
