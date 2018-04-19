package io.mateu.erp.client.product;

import io.mateu.erp.model.product.ContractClause;
import io.mateu.erp.model.product.ContractClauseGroup;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.MDDAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ConfigModule extends AbstractModule {
    @Override
    public String getName() {
        return "Config";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Clauses groups", ContractClauseGroup.class));

        m.add(new MDDAction("Clauses", ContractClause.class));

        return m;
    }
}
