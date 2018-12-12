package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;

public class SupplierDataProvider extends JPQLListDataProvider {
    public SupplierDataProvider() throws Throwable {
        super("select x from " + Partner.class.getName() + " x where x.provider = true order by x.name");
    }
}
