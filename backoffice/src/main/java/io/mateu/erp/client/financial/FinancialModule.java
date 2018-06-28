package io.mateu.erp.client.financial;

import io.mateu.erp.model.accounting.AccountingEntry;
import io.mateu.erp.model.accounting.LineItem;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.invoicing.*;
import io.mateu.erp.model.payments.*;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.erp.model.taxes.VATPercent;
import io.mateu.erp.model.taxes.VATSettlement;
import io.mateu.erp.model.tpv.TPV;
import io.mateu.erp.shared.financial.FinancialService;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.components.fields.DateField;
import io.mateu.ui.core.client.views.AbstractDialog;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.client.MDDAction;
import io.mateu.ui.mdd.client.MDDMenu;

import java.net.URL;
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

        m.add(new MDDAction("Agents", FinancialAgent.class));

        m.add(new MDDAction("Currency exchanges", CurrencyExchange.class));

        m.add(new MDDAction("Billing concepts", BillingConcept.class));

        m.add(new MDDAction("Rebate settlements", Rebate.class));

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

                        m.add(new MDDAction("Invoices", IssuedInvoice.class));

                        m.add(new MDDAction("Charges", Charge.class, "x.type = " + ChargeType.class.getName() + "." + ChargeType.BOOKING));

                        m.add(new MDDAction("Litigations", Litigation.class));

                        return m;
                    }
                });

                m.add(new AbstractMenu("Received") {
                    @Override
                    public List<MenuEntry> buildEntries() {
                        List<MenuEntry> m = new ArrayList<>();

                        m.add(new MDDAction("Invoices", ReceivedInvoice.class));

                        m.add(new MDDAction("Charges", Charge.class, "x.type = " + ChargeType.class.getName() + "." + ChargeType.PURCHASE));

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

                m.add(new MDDAction("Accounts", Account.class));

                m.add(new MDDAction("Payment gateways", TPV.class));

                m.add(new MDDAction("VCC", VCC.class));

                m.add(new MDDAction("Payments", Payment.class));

                m.add(new MDDAction("Deposits", Deposit.class));

                m.add(new MDDAction("Bank remittances", BankRemittance.class));

                m.add(new MDDAction("Bank reconciliation", BankStatement.class));

                return m;
            }
        });

        m.add(new AbstractMenu("Viajes Ibiza") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();


                m.add(new AbstractAction("Reprice") {
                    @Override
                    public void run() {
                        MateuUI.openView(new AbstractDialog() {
                            @Override
                            public void onOk(Data data) {
                                ((FinancialServiceAsync)MateuUI.create(FinancialService.class)).reprice(MateuUI.getApp().getUserData(), data.getLocalDate("from"), data.getLocalDate("to"), new Callback<>());
                            }

                            @Override
                            public String getTitle() {
                                return "Reprice";
                            }

                            @Override
                            public void build() {
                                add(new DateField("from", "From")).add(new DateField("to", "To"));
                            }
                        });
                    }
                });

                m.add(new AbstractAction("General report") {
                    @Override
                    public void run() {
                        MateuUI.openView(new AbstractDialog() {
                            @Override
                            public void onOk(Data data) {
                                ((FinancialServiceAsync)MateuUI.create(FinancialService.class)).generalReport(data.getLocalDate("from"), data.getLocalDate("to"), new Callback<URL>() {
                                    @Override
                                    public void onSuccess(URL result) {
                                        MateuUI.open(result);
                                    }
                                });
                            }

                            @Override
                            public String getTitle() {
                                return "General report";
                            }

                            @Override
                            public void build() {
                                add(new DateField("from", "From")).add(new DateField("to", "To"));
                            }
                        });
                    }
                });

                m.add(new AbstractAction("Export to Beroni") {
                    @Override
                    public void run() {
                        MateuUI.openView(new AbstractDialog() {
                            @Override
                            public void onOk(Data data) {
                                ((FinancialServiceAsync)MateuUI.create(FinancialService.class)).exportToBeroni(data.getLocalDate("from"), data.getLocalDate("to"), new Callback<>());
                            }

                            @Override
                            public String getTitle() {
                                return "Reprice";
                            }

                            @Override
                            public void build() {
                                add(new DateField("from", "From")).add(new DateField("to", "To"));
                            }
                        });
                    }
                });


                return m;

            }
        });


        return m;
    }
}
