package io.mateu.erp.client.operations;

import com.vaadin.icons.VaadinIcons;
import io.mateu.mdd.core.app.AbstractArea;
import io.mateu.mdd.core.app.AbstractModule;

import java.util.ArrayList;
import java.util.List;

public class OperationsArea extends AbstractArea {

    public OperationsArea() {
        super(VaadinIcons.TRUCK,"Operations");
    }

    @Override
    public List<AbstractModule> buildModules() {
        List<AbstractModule> l = new ArrayList<>();
        l.add(new HotelOperationsModule());
        l.add(new TransferOperationsModule());
        l.add(new GenericOperationsModule());
        l.add(new FreeTextOperationsModule());
        l.add(new ExcursionOperationsModule());
        l.add(new CircuitOperationsModule());
        return l;
    }
}
