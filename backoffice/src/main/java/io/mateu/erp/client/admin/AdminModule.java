package io.mateu.erp.client.admin;

import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.config.Template;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.monitoring.Alarm;
import io.mateu.erp.model.monitoring.Watchdog;
import io.mateu.erp.model.monitoring.Watcher;
import io.mateu.erp.model.multilanguage.Language;
import io.mateu.erp.model.multilanguage.Literal;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.world.City;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.State;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.client.*;
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

        m.add(new MDDAction("AppConfig", AppConfig.class, 1l));

        m.add(new MDDAction("Users", User.class));

        m.add(new MDDAction("Auth tokens", AuthToken.class));

        m.add(new MDDAction("Offices", Office.class));

        m.add(new MDDAction("POS", PointOfSale.class));

        m.add(new MDDAction("Actors", Actor.class));

        m.add(new MDDAction("Currencies", Currency.class));

        m.add(new MDDAction("Languages", Language.class));

        m.add(new MDDAction("Translations", Literal.class));

        m.add(new MDDAction("Templates", Template.class));

        m.add(new MDDMenu("World", "Countries", Country.class, "States", State.class, "City", City.class));

        m.add(new MDDMenu("Monitoring", "Watchdogs", Watchdog.class, "Alarms", Alarm.class, "Watchers", Watcher.class));

        return m;
    }
}
