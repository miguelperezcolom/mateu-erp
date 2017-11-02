package io.mateu.erp.model.booking.hotel;

import io.mateu.ui.mdd.server.BaseServerSideWizard;

public class HotelBookingWizard extends BaseServerSideWizard {

    public HotelBookingWizard() {
        add(new Pagina1());
        add(new Pagina2());
        add(new Pagina3());
    }

}
