package io.mateu.erp.client.booking;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class BookingArea extends AbstractArea {

    public BookingArea() {
        super("Booking");
    }

    @Override
    public List<AbstractModule> getModules() {
        List<AbstractModule> l = new ArrayList<>();
        l.add(new BookingModule());
        l.add(new ImportingModule());
        return l;
    }
}
