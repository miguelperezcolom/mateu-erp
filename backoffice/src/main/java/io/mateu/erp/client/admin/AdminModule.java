package io.mateu.erp.client.admin;

import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.authentication.User;
import io.mateu.common.model.config.AppConfig;
import io.mateu.common.model.config.Template;
import io.mateu.erp.model.financials.Currency;
import io.mateu.common.model.multilanguage.Literal;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.thirdParties.Integration;
import io.mateu.erp.model.world.City;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.State;
import io.mateu.erp.model.world.Zone;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.MDDAction;
import io.mateu.ui.mdd.client.MDDMenu;
import io.mateu.ui.mdd.client.MDDOpenEditorAction;

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
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDOpenEditorAction("AppConfig", AppConfig.class, 1l));

        m.add(new MDDAction("Users", User.class));

        m.add(new MDDAction("Auth tokens", AuthToken.class));

        m.add(new MDDMenu("Organization", "Offices", Office.class, "POS", PointOfSale.class));

        m.add(new MDDAction("Currencies", Currency.class));

        //m.add(new MDDMenu("Multilingual", "Languages", Language.class, "Translations", Literal.class));

        m.add(new MDDAction("Translations", Literal.class));

        m.add(new MDDMenu("World", "Countries", Country.class, "States", State.class, "City", City.class, "Zone", Zone.class));

        m.add(new MDDAction("Third party integrations", Integration.class));

        //m.add(new MDDMenu("Monitoring", "Watchdogs", Watchdog.class, "Alarms", Alarm.class, "Watchers", Watcher.class));

        m.add(new MDDAction("Templates", Template.class));

        return m;
    }
}
