package io.mateu.erp.client.financial;

import io.mateu.erp.model.accounting.AccountingEntry;
import io.mateu.erp.model.accounting.LineItem;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.invoicing.ChargeType;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import io.mateu.erp.model.invoicing.ReceivedInvoice;
import io.mateu.erp.model.payments.*;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.erp.model.taxes.VATPercent;
import io.mateu.erp.model.taxes.VATSettlement;
import io.mateu.erp.model.tpv.TPV;
import io.mateu.erp.server.financial.FinancialServiceImpl;
import io.mateu.mdd.core.app.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class FinancialModule extends AbstractModule {
    @Override
    public String getName() {
        return "Financial";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDOpenCRUDAction("Agents", FinancialAgent.class));

        m.add(new MDDOpenCRUDAction("Currency exchanges", CurrencyExchange.class));

        m.add(new MDDOpenCRUDAction("Billing concepts", BillingConcept.class));

        m.add(new MDDOpenCRUDAction("Rebate settlements", Rebate.class));

        m.add(new MDDMenu("VAT", "VAT", VAT.class, "Percents", VATPercent.class, "Settlements", VATSettlement.class));


        m.add(new MDDMenu("Accounting", "Plans", io.mateu.erp.model.accounting.AccountingPlan.class, "Accounts", io.mateu.erp.model.accounting.Account.class, "Entries", AccountingEntry.class, "Line items", LineItem.class));

        m.add(new AbstractMenu("Invoicing") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new AbstractMenu("Issued") {
                    @Override
                    public List<MenuEntry> buildEntries() {
                        List<MenuEntry> m = new ArrayList<>();

                        m.add(new MDDOpenCRUDAction("Invoices", IssuedInvoice.class));

                        m.add(new MDDOpenCRUDAction("Charges", Charge.class, "x.type = " + ChargeType.class.getName() + "." + ChargeType.BOOKING));

                        m.add(new MDDOpenCRUDAction("Litigations", Litigation.class));

                        return m;
                    }
                });

                m.add(new AbstractMenu("Received") {
                    @Override
                    public List<MenuEntry> buildEntries() {
                        List<MenuEntry> m = new ArrayList<>();

                        m.add(new MDDOpenCRUDAction("Invoices", ReceivedInvoice.class));

                        m.add(new MDDOpenCRUDAction("Charges", Charge.class, "x.type = " + ChargeType.class.getName() + "." + ChargeType.PURCHASE));

                        return m;
                    }
                });


                return m;
            }
        });


        m.add(new AbstractMenu("Payments") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDOpenCRUDAction("Accounts", Account.class));

                m.add(new MDDOpenCRUDAction("Payment gateways", TPV.class));

                m.add(new MDDOpenCRUDAction("VCC", VCC.class));

                m.add(new MDDOpenCRUDAction("Payments", Payment.class));

                m.add(new MDDOpenCRUDAction("Deposits", Deposit.class));

                m.add(new MDDOpenCRUDAction("Bank remittances", BankRemittance.class));

                m.add(new MDDOpenCRUDAction("Bank reconciliation", BankStatement.class));

                return m;
            }
        });

        m.add(new AbstractMenu("Viajes Ibiza") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();


                m.add(new MDDCallMethodAction("Reprice", FinancialServiceImpl.class, "reprice"));

                m.add(new MDDCallMethodAction("General report", FinancialServiceImpl.class, "generalReport"));

                m.add(new MDDCallMethodAction("Export to Beroni", FinancialServiceImpl.class, "exportToBeroni"));
                return m;

            }
        });


        return m;
    }
}
