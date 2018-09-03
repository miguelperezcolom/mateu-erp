package io.mateu.erp.dispo.interfaces.common;

import io.mateu.erp.dispo.interfaces.integrations.IIntegration;
import io.mateu.erp.model.partners.PartnerStatus;

import java.util.List;

public interface IPartner {

    public long getId();

    public String getName();

    public List<? extends IIntegration> getIntegrations();

    PartnerStatus getStatus();
}
