package io.mateu.erp.client;

import io.mateu.erp.client.admin.AdminArea;
import io.mateu.erp.client.booking.BookingArea;
import io.mateu.erp.client.cms.CMSArea;
import io.mateu.erp.client.crm.CRMArea;
import io.mateu.erp.client.fieldBuilders.AmountFieldBuilder;
import io.mateu.erp.client.fieldBuilders.FareValueFieldBuilder;
import io.mateu.erp.client.fieldBuilders.FastMoneyFieldBuilder;
import io.mateu.erp.client.financial.FinancialArea;
import io.mateu.erp.client.management.ManagementArea;
import io.mateu.erp.client.operations.OperationsArea;
import io.mateu.erp.client.product.ProductArea;
import io.mateu.erp.client.utils.UtilsArea;
import io.mateu.erp.model.financials.Amount;
import io.mateu.erp.model.product.hotel.FareValue;
import io.mateu.mdd.core.app.AbstractArea;
import io.mateu.mdd.core.app.BaseMDDApp;
import io.mateu.mdd.core.model.config.AppConfig;
import io.mateu.mdd.core.model.population.Populator;
import io.mateu.mdd.core.reflection.FieldInterfaced;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.vaadinport.vaadin.components.fieldBuilders.AbstractFieldBuilder;
import org.javamoney.moneta.FastMoney;

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

    @Override
    public Class<? extends AppConfig> getAppConfigClass() {
        return io.mateu.erp.model.config.AppConfig.class;
    }

    @Override
    public Populator getPopulator() {
        return new io.mateu.erp.model.population.Populator();
    }

    @Override
    public AbstractFieldBuilder getFieldBuilder(FieldInterfaced field) {
        if (FareValue.class.equals(field.getType())) return new FareValueFieldBuilder();
        else if (Amount.class.equals(field.getType())) return new AmountFieldBuilder();
        else if (FastMoney.class.equals(field.getType())) return new FastMoneyFieldBuilder();
        else return super.getFieldBuilder(field);
    }
}
