package io.mateu.erp.client.booking;


import com.vaadin.icons.VaadinIcons;
import io.mateu.mdd.core.app.AbstractArea;
import io.mateu.mdd.core.app.AbstractModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class BookingArea extends AbstractArea {

    public BookingArea() {
        super(VaadinIcons.CART, "Booking");
    }

    @Override
    public List<AbstractModule> buildModules() {
        List<AbstractModule> l = new ArrayList<>();
        l.add(new BookingModule());
        l.add(new ImportingModule());
        return l;
    }

}
