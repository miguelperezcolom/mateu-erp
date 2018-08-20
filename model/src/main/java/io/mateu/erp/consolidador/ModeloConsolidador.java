package io.mateu.erp.consolidador;

import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.product.hotel.Hotel;

import java.util.List;

public interface ModeloConsolidador {

    public AuthToken getAuthToken(String token);

    public List<Hotel> getResources(List<String> resorts);
}
