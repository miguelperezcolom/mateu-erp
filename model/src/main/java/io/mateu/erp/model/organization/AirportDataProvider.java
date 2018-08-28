package io.mateu.erp.model.organization;

import com.google.common.collect.Lists;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.product.transfer.TransferPointType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AirportDataProvider extends ListDataProvider {

    public AirportDataProvider() throws Throwable {
        super(createItems());
    }

    private static Collection createItems() throws Throwable {
        List l = new ArrayList();

        io.mateu.mdd.core.util.Helper.notransact(em -> l.addAll(em.createQuery("select x from " + TransferPoint.class.getName() + " x where x.type in :t order by x.name").setParameter("t", Lists.newArrayList(TransferPointType.AIRPORT, TransferPointType.PORT, TransferPointType.TRAINSTATION)).getResultList()));

        return l;
    }
}