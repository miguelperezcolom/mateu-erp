package io.mateu.erp.model.booking.hotel;


import io.mateu.erp.dispo.KeyValue;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.interfaces.WizardPage;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

@Getter@Setter
public class Pagina3 implements WizardPage {

    private final Pagina2b pagina2b;
    @Output
    private String key;

    @Output
    private String selectedOption;

    private String agencyReference;

    @NotNull
    private String leadName;


    private String comments;

    public Pagina3(Pagina2b pagina2b) {
        this.pagina2b = pagina2b;
    }


    @Override
    public String toString() {
        return "Fill and confirm";
    }

    public Data getData(UserData user, EntityManager em, Data in) throws Throwable {
        Data out = new Data();

        out.set("selectedOption", "" + pagina2b.getPagina2().getHotel().getHotelName()
                + " " + pagina2b.getPagina2().getHotel().getCategory()
                        + "\n" + pagina2b.getPagina2().getHotel().getCity()
                        + "\n" + pagina2b.getOption().getRooms()
                        + "\n" + pagina2b.getOption().getBoard()
                        + "\n" + pagina2b.getOption().getPrice() + " " + pagina2b.getOption().getCurrency()
        );

        out.set("key", pagina2b.getOption().getKey());


        return out;
    }

    @Override
    public WizardPage getPrevious() {
        return pagina2b;
    }

    @Override
    public WizardPage getNext() {
        return null;
    }

    @Override
    public void onOk() {
        long serviceId = 0;
        try {
            serviceId = HotelService.createFromKey(
                    MDD.getUserData()
                    , new KeyValue(
                            getPagina2b().getOption().getKey())
                    , getAgencyReference()
                    , getLeadName()
                    , getComments());
        } catch (Throwable throwable) {
            MDD.alert(throwable);
        }

        //todo: abrir la reserva recién creada
    }
}
