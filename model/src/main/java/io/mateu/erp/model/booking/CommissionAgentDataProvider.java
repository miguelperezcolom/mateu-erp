package io.mateu.erp.model.booking;

import com.vaadin.data.provider.AbstractDataProvider;
import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;

public class CommissionAgentDataProvider extends JPQLListDataProvider {
    public CommissionAgentDataProvider() throws Throwable {
        super("select x from " + Partner.class.getName() + " x where x.commissionAgent = true order by x.name");
    }
}
