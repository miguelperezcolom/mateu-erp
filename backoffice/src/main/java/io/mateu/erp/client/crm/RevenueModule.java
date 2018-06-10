package io.mateu.erp.client.crm;

import io.mateu.erp.model.financials.Abseiling;
import io.mateu.erp.model.financials.CommissionTerms;
import io.mateu.erp.model.financials.CreditLimit;
import io.mateu.erp.model.financials.PaymentTerms;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.partners.PartnerGroup;
import io.mateu.erp.model.partners.Market;
import io.mateu.erp.model.revenue.*;
import io.mateu.ui.core.client.app.AbstractMenu;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.MDDAction;

import java.util.ArrayList;
import java.util.List;

public class RevenueModule extends AbstractModule {
    @Override
    public String getName() {
        return "Revenue";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Products", Product.class));

        m.add(new MDDAction("Partners", Partner.class));

        m.add(new MDDAction("Partner groups", PartnerGroup.class));

        m.add(new AbstractMenu("Revenue") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new AbstractMenu("Markup") {
                    @Override
                    public List<MenuEntry> buildEntries() {
                        List<MenuEntry> m = new ArrayList<>();

                        m.add(new MDDAction("Markups", Markup.class));

                        m.add(new MDDAction("Markup lines", MarkupLine.class));

                        return m;
                    }
                });

                m.add(new AbstractMenu("Handling fee") {
                    @Override
                    public List<MenuEntry> buildEntries() {
                        List<MenuEntry> m = new ArrayList<>();

                        m.add(new MDDAction("Handling fee", HandlingFee.class));

                        m.add(new MDDAction("Handling fee lines", HandlingFeeLine.class));

                        return m;
                    }
                });

                return m;
            }
        });

        m.add(new MDDAction("Markets", Market.class));


        m.add(new MDDAction("CreditLimit", CreditLimit.class));

        m.add(new MDDAction("Abseiling", Abseiling.class));

        m.add(new MDDAction("Commission terms", CommissionTerms.class));

        m.add(new MDDAction("Payment terms", PaymentTerms.class));

        return m;
    }
}
