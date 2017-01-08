package io.mateu.erp.client;

import io.mateu.erp.client.admin.AdminArea;
import io.mateu.erp.client.booking.BookingArea;
import io.mateu.erp.client.cms.CMSArea;
import io.mateu.erp.client.financial.FinancialArea;
import io.mateu.erp.client.management.ManagementArea;
import io.mateu.erp.client.product.ProductArea;
import io.mateu.erp.client.utils.UtilsArea;
import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.App;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
@App
public class ERPAtClientSide extends AbstractApplication {
    @Override
    public String getName() {
        return "Mateu ERP";
    }

    @Override
    public List<AbstractArea> getAreas() {
        return Arrays.asList(new AdminArea(), new CMSArea(), new ProductArea(), new BookingArea(), new FinancialArea(), new ManagementArea(), new UtilsArea());
    }
}
