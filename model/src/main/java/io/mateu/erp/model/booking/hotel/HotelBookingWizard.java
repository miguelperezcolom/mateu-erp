package io.mateu.erp.model.booking.hotel;


import io.mateu.mdd.core.views.BaseServerSideWizard;

public class HotelBookingWizard extends BaseServerSideWizard {

    public HotelBookingWizard() {
        add(new Pagina1());
        add(new Pagina2());
        add(new Pagina2b());
        add(new Pagina3());
    }

}
