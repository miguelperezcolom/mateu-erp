package io.mateu.erp.client.product;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ProductArea extends AbstractArea {

    public ProductArea() {
        super("Product");
    }

    @Override
    public List<AbstractModule> getModules() {
        return Arrays.asList(
        new HotelModule()
        , new TransferModule()
        , new GenericModule()
        );
    }
}
