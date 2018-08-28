package io.mateu.erp.client.crm;

import io.mateu.erp.model.financials.Rebate;
import io.mateu.erp.model.financials.CommissionTerms;
import io.mateu.erp.model.financials.CreditLimit;
import io.mateu.erp.model.financials.PaymentTerms;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.partners.PartnerGroup;
import io.mateu.erp.model.partners.Market;
import io.mateu.erp.model.revenue.*;
import io.mateu.mdd.core.app.*;

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

        m.add(new MDDOpenCRUDAction("Product lines", ProductLine.class));

        m.add(new MDDOpenCRUDAction("Partners", Partner.class));

        m.add(new MDDOpenCRUDAction("Partner groups", PartnerGroup.class));

        m.add(new AbstractMenu("Revenue") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new AbstractMenu("Markup") {
                    @Override
                    public List<MenuEntry> buildEntries() {
                        List<MenuEntry> m = new ArrayList<>();

                        m.add(new MDDOpenCRUDAction("Markups", Markup.class));

                        m.add(new MDDOpenCRUDAction("Markup lines", MarkupLine.class));

                        return m;
                    }
                });

                m.add(new AbstractMenu("Handling fee") {
                    @Override
                    public List<MenuEntry> buildEntries() {
                        List<MenuEntry> m = new ArrayList<>();

                        m.add(new MDDOpenCRUDAction("Handling fee", HandlingFee.class));

                        m.add(new MDDOpenCRUDAction("Handling fee lines", HandlingFeeLine.class));

                        return m;
                    }
                });

                return m;
            }
        });

        m.add(new MDDOpenCRUDAction("Markets", Market.class));


        m.add(new MDDOpenCRUDAction("CreditLimit", CreditLimit.class));

        m.add(new MDDOpenCRUDAction("Abseiling", Rebate.class));

        m.add(new MDDOpenCRUDAction("Commission terms", CommissionTerms.class));

        m.add(new MDDOpenCRUDAction("Payment terms", PaymentTerms.class));

        m.add(new MDDMenu("Representatives"));

        return m;
    }
}
