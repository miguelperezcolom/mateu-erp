package io.mateu.erp.model.booking.hotel;

import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.AbstractServerSideWizard;
import io.mateu.ui.mdd.server.BaseServerSideWizard;
import io.mateu.ui.mdd.server.WizardPageVO;

public class HotelBookingWizard extends BaseServerSideWizard {

    public HotelBookingWizard() {
        add(new Pagina1());
        add(new Pagina2());
        add(new Pagina3());
    }

}
