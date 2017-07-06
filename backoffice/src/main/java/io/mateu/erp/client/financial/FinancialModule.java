package io.mateu.erp.client.financial;

import io.mateu.erp.shared.financial.FinancialService;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.components.fields.DateField;
import io.mateu.ui.core.client.views.AbstractDialog;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class FinancialModule extends AbstractModule {
    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        if (false) m.add(new AbstractAction("Agents") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.financials.FinancialAgent", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Billing concepts") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.financials.BillingConcept", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Reprice") {
            @Override
            public void run() {
                MateuUI.openView(new AbstractDialog() {
                    @Override
                    public void onOk(Data data) {
                        ((FinancialServiceAsync)MateuUI.create(FinancialService.class)).reprice(data.getLocalDate("from"), data.getLocalDate("to"), new Callback<>());
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


        if (false) {

            m.add(new AbstractAction("Isued invoices") {
                @Override
                public void run() {
                }
            });

            m.add(new AbstractAction("Received invoices") {
                @Override
                public void run() {
                }
            });

            m.add(new AbstractAction("Payment gateways") {
                @Override
                public void run() {
                }
            });

            m.add(new AbstractAction("VCC") {
                @Override
                public void run() {
                }
            });

            m.add(new AbstractAction("VAT") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Commissions") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Abseiling") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Prepayment") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Portfolios") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Payments") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Collections") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Bank remittances") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Direct payments") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Bank reconciliation") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Accounting") {
                @Override
                public void run() {

                }
            });

        }

        return m;
    }
}
