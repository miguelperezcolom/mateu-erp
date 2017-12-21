package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.AbstractServerSideWizardPage;
import io.mateu.ui.mdd.server.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

@Getter@Setter
public class Pagina3 extends AbstractServerSideWizardPage {

    @Output
    private String key;

    @Output
    private String selectedOption;

    private String agencyReference;

    @NotNull
    private String leadName;


    private String comments;


    @Override
    public String getTitle() {
        return "Fill and confirm";
    }

    @Override
    public Data getData(UserData user, EntityManager em, Data in) throws Throwable {
        Data out = new Data();

        Pagina2 p2 = new Pagina2();
        p2.fill(em, user, in);

        Pagina2b p2b = new Pagina2b();
        p2b.fill(em, user, in);

        out.set("selectedOption", "" + p2.getHotel().getHotelName() + " " + p2.getHotel().getCategory()
                        + "\n" + p2.getHotel().getCity()
                        + "\n" + p2b.getOption().getRooms()
                        + "\n" + p2b.getOption().getBoard()
                        + "\n" + p2b.getOption().getPrice() + " " + p2b.getOption().getCurrency()
        );

        out.set("key", p2b.getOption().getKey());


        return out;
    }
}
