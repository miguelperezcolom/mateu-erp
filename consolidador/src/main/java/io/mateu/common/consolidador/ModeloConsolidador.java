package io.mateu.common.consolidador;

import io.mateu.erp.dispo.interfaces.auth.IAuthToken;
import io.mateu.erp.dispo.interfaces.portfolio.IResource;

import java.util.List;

public interface ModeloConsolidador {

    public IAuthToken getAuthToken(String token);

    public List<IResource> getResources(List<String> resorts);
}
