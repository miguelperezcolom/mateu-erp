package io.mateu.erp.model.invoicing;

import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.interfaces.ListView;
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;

import javax.persistence.EntityManager;
import java.util.List;

public class InvoicerView implements ListView<Charge> {

    @Override
    public String getParams() {
        return "billingConcept, booking.id, booking.agency, booking.start, booking.finish";
    }

    @Override
    public String getColHeaders() {
        return "Agency, Office, Currency, Total, Total";
    }

    @Override
    public String getCols() {
        return "booking.agency.name, office.name, currency.isoCode, sum(total), sum(totalInAccountingCurrency)";
    }

    @Override
    public String getSums() {
        return "totalInAccountingCurrency";
    }

    @Override
    public String buildQuery(EntityManager em, UserData user, Data parameters) {
        return null;
    }

    @Override
    public String getAdditionalFilters(EntityManager em, UserData user, Data parameters) {
        return "x.invoice is null";
    }


    public MDDLink invoice(EntityManager em, UserData user, Data parametros, List<Data> seleccion) {

        // abrir proforma

        // generar facturas

        return new MDDLink(Invoice.class, ActionType.OPENLIST, new Data("number", "lista con los ids de las facturas generadas"));
    }

}
