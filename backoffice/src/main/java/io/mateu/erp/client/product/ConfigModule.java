package io.mateu.erp.client.product;

import io.mateu.erp.model.product.*;
import io.mateu.mdd.core.app.AbstractModule;
import io.mateu.mdd.core.app.MDDMenu;
import io.mateu.mdd.core.app.MDDOpenCRUDAction;
import io.mateu.mdd.core.app.MenuEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ConfigModule extends AbstractModule {
    @Override
    public String getName() {
        return "Common";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDOpenCRUDAction("Product types", ProductType.class));

        m.add(new MDDMenu("Data sheets", "Features groups", FeatureGroup.class, "Features", Feature.class, "Data sheets", DataSheet.class, "Data sheet images", DataSheetImage.class, "Data sheet features", FeatureValue.class));

        m.add(new MDDMenu("Clauses", "Clauses groups", ContractClauseGroup.class, "Clauses", ContractClause.class));

        m.add(new MDDMenu("Operations", "Prices list", AbstractProduct.class, "Remarks", ZoneProductRemark.class));

        return m;
    }
}
