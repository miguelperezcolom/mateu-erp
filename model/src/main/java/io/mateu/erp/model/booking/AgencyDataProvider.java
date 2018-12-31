package io.mateu.erp.model.booking;

import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;

public class AgencyDataProvider extends JPQLListDataProvider {
    public AgencyDataProvider() throws Throwable {
        super("select x from " + Partner.class.getName() + " x where x.agency = true order by x.name");
    }
}
