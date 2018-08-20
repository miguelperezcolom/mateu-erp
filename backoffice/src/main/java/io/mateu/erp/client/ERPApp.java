package io.mateu.erp.client;

import io.mateu.erp.client.admin.AdminArea;
import io.mateu.erp.client.booking.BookingArea;
import io.mateu.erp.client.cms.CMSArea;
import io.mateu.erp.client.crm.CRMArea;
import io.mateu.erp.client.financial.FinancialArea;
import io.mateu.erp.client.management.ManagementArea;
import io.mateu.erp.client.operations.OperationsArea;
import io.mateu.erp.client.product.ProductArea;
import io.mateu.erp.client.utils.UtilsArea;
import io.mateu.mdd.core.app.AbstractArea;
import io.mateu.mdd.core.app.BaseMDDApp;
import io.mateu.mdd.core.util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ERPApp extends BaseMDDApp {

    static {
        Helper.loadProperties();
    }


    @Override
    public String getName() {
        return System.getProperty("appname", "Mateu ERP");
    }

    @Override
    public List<AbstractArea> buildAreas() {
        List<AbstractArea> l = new ArrayList<>();
        l.add(new AdminArea());
        l.add(new CMSArea());
        l.add(new CRMArea());
        l.add(new ProductArea());
        l.add(new BookingArea());
        l.add(new OperationsArea());
        l.add(new FinancialArea());
        l.add(new ManagementArea());
        l.add(new UtilsArea());
        return l;
    }
}
