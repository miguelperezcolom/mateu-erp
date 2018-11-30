package io.mateu.erp.model.caval;

import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.mdd.core.data.Pair;

import java.util.Collection;

public class CavalIdDataProvider extends ListDataProvider<Pair<String, String>> {

    public CavalIdDataProvider() {
        super(CAVALClient.get().getHotels());
    }
}
