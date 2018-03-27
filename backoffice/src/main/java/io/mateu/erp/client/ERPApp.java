package io.mateu.erp.client;

import io.mateu.erp.client.admin.AdminArea;
import io.mateu.erp.client.booking.BookingArea;
import io.mateu.erp.client.cms.CMSArea;
import io.mateu.erp.client.crm.CRMArea;
import io.mateu.erp.client.financial.FinancialArea;
import io.mateu.erp.client.management.ManagementArea;
import io.mateu.erp.client.product.ProductArea;
import io.mateu.erp.client.utils.UtilsArea;
import io.mateu.erp.model.util.Helper;
import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.MateuUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ERPApp extends AbstractApplication {

    static {
        Helper.loadProperties();
    }


    @Override
    public String getName() {
        return System.getProperty("appname", "Mateu ERP");
    }

    @Override
    public List<AbstractArea> getAreas() {
        List<AbstractArea> l = new ArrayList<>();
        l.add(new AdminArea());
        l.add(new CMSArea());
        l.add(new CRMArea());
        l.add(new ProductArea());
        l.add(new BookingArea());
        l.add(new FinancialArea());
        l.add(new ManagementArea());
        l.add(new UtilsArea());
        return l;
    }
}
