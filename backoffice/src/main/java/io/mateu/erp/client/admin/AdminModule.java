package io.mateu.erp.client.admin;

import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.authentication.User;
import io.mateu.mdd.core.app.*;
import io.mateu.mdd.core.model.config.Template;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Currency;
import io.mateu.mdd.core.model.multilanguage.Literal;
import io.mateu.erp.model.organization.Company;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.thirdParties.Integration;
import io.mateu.erp.model.world.Zone;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.world.Area;

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

        m.add(new MDDOpenCRUDAction("Users", io.mateu.mdd.core.model.authentication.User.class));

        m.add(new MDDOpenCRUDAction("Auth tokens", AuthToken.class));

        m.add(new MDDMenu("Organization", "Companies", Company.class, "Offices", Office.class, "POS", PointOfSale.class));

        m.add(new MDDOpenCRUDAction("Currencies", Currency.class));

        //m.add(new MDDMenu("Multilingual", "Languages", Language.class, "Translations", Literal.class));

        m.add(new MDDOpenCRUDAction("Translations", Literal.class));

        m.add(new MDDMenu("World", "Countries", Country.class, "Destinations", Destination.class, "Zones", Zone.class, "Areas", Area.class));

        m.add(new MDDOpenCRUDAction("Third party integrations", Integration.class));

        //m.add(new MDDMenu("Monitoring", "Watchdogs", Watchdog.class, "Alarms", Alarm.class, "Watchers", Watcher.class));

        m.add(new MDDOpenCRUDAction("Templates", Template.class));

        return m;
    }
}
