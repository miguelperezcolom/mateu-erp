package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.AbstractServerSideWizardPage;
import io.mateu.ui.mdd.server.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter@Setter
public class Pagina3 extends AbstractServerSideWizardPage {

    @Output
    private String key;

    @Output
    private String selectedOption;

    @NotNull
    private String leadName;


    private String comments;


    @Override
    public String getTitle() {
        return "Fill and confirm";
    }

    @Override
    public Data getData(Data in) throws Throwable {
        Data out = new Data();

        Helper.transact((JPATransaction) (em) -> {

            Pagina2 p = new Pagina2();
            p.fill(em, in);

            out.set("selectedOption", "" + p.getOption().getHotelName() + " " + p.getOption().getCategory()
                            + "\n" + p.getOption().getCity()
                            + "\n" + p.getOption().getRooms()
                            + "\n" + p.getOption().getBoard()
                            + "\n" + p.getOption().getPrice() + " " + p.getOption().getCurrency()
            );

            out.set("key", p.getOption().getKey());

        });

        return out;
    }
}
