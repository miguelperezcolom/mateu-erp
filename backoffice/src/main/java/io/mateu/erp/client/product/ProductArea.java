package io.mateu.erp.client.product;


import io.mateu.mdd.core.app.AbstractArea;
import io.mateu.mdd.core.app.AbstractModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ProductArea extends AbstractArea {

    public ProductArea() {
        super("Portfolio");
    }

    @Override
    public List<AbstractModule> buildModules() {
        List<AbstractModule> l = new ArrayList<>();
        l.add(new ConfigModule());
        l.add(new ProductsModule());
        return l;
    }
}
