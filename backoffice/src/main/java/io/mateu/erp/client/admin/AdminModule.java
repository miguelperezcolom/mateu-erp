package io.mateu.erp.client.admin;

import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.client.MDDJPACRUDView;
import io.mateu.ui.mdd.shared.ERPService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class AdminModule extends AbstractModule {
    @Override
    public String getName() {
        return "Admin";
    }

    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("AppConfig") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.config.AppConfig", new Callback<Data>() {
                    @Override
                    public void onSuccess(Data result) {
                        MateuUI.openView(new MDDJPACRUDView(result).getNewEditorView().setInitialId(1l));
                    }
                });
            }
        });

        m.add(new AbstractAction("Users") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.authentication.User", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Auth tokens") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.authentication.AuthToken", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Offices") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.organization.Office", new MDDCallback());
            }
        });

        m.add(new AbstractAction("POS") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.organization.PointOfSale", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Actors") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.financials.Actor", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Currencies") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.financials.Currency", new MDDCallback());
            }
        });



        if (false) m.add(new AbstractAction("Languages") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.multilanguage.Language", new MDDCallback());
            }
        });

        if (false) m.add(new AbstractAction("Translations") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.multilanguage.Literal", new MDDCallback());
            }
        });

        if (false) m.add(new AbstractAction("Templates") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.config.Template", new MDDCallback());
            }
        });


        m.add(new AbstractMenu("World") {
            @Override
            public List<MenuEntry> getEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new AbstractAction("Countries") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.world.Country", new MDDCallback());
                    }
                });

                m.add(new AbstractAction("States") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.world.State", new MDDCallback());
                    }
                });

                m.add(new AbstractAction("Cities") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.world.City", new MDDCallback());
                    }
                });

                return m;
            }
        });


        m.add(new AbstractAction("Agents") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("com.quonext.quoon.Agent", new MDDCallback());
            }
        });

        return m;
    }
}
