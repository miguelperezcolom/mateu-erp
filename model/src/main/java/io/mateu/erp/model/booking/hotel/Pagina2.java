package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.dispo.DispoRQ;
import io.mateu.erp.dispo.HotelAvailabilityRunner;
import io.mateu.erp.dispo.ModeloDispo;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.world.City;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.AbstractServerSideWizardPage;
import io.mateu.ui.mdd.server.ERPServiceImpl;
import io.mateu.ui.mdd.server.annotations.Output;
import io.mateu.ui.mdd.server.annotations.UseGridToSelect;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.AvailableHotel;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class Pagina2 extends AbstractServerSideWizardPage {

    @Output
    private String message;

    @NotNull
    @UseGridToSelect
    private HotelOption hotel;

    @Override
    public String getTitle() {
        return "Choose an option";
    }

    @Override
    public Data getData(UserData user, EntityManager em, Data in) throws Throwable {
        Data out = new Data();

        Pagina1 p = new Pagina1();
        p.fill(em, user, in);


        long t0 = System.currentTimeMillis();

        List<Hotel> hoteles = new ArrayList<>();
        for (City c : p.getState().getCities()) {
            hoteles.addAll(c.getHotels());
        }

        Actor agencia = p.getAgency();

        //System.out.println("" + hoteles.size() + " hoteles encontrados");

        ModeloDispo modelo = new ModeloDispo() {
            @Override
            public IHotelContract getHotelContract(long id) {
                return em.find(HotelContract.class, id);
            }
        };

        List<io.mateu.erp.dispo.Occupancy> os = new ArrayList<>();
        for (Occupation o : p.getOccupations()) {
            os.add(new io.mateu.erp.dispo.Occupancy(o.getNumberOfRooms(), o.getPaxPerRoom(), o.getAges()));
        }

        DispoRQ rq = new DispoRQ(p.getFormalizationDate(), io.mateu.erp.dispo.Helper.toInt(p.getCheckin()), io.mateu.erp.dispo.Helper.toInt(p.getCheckout()), os, false);


        List<HotelOption> options = new ArrayList<>();
        for (Hotel h : hoteles) {
            AvailableHotel ah = new HotelAvailabilityRunner().check(agencia, h, p.getAgency().getId(), 1, modelo, rq);
            if (ah != null) {

                HotelOption o = new HotelOption();

                o.setId(ah.getHotelId());
                o.setCategory(ah.getHotelCategoryName());
                o.setHotelName(ah.getHotelName());
                o.setBestDeal(ah.getBestDeal());

                options.add(o);

            }
        }

        long t = System.currentTimeMillis();

        out.set("message", "" + options.size() + " hotels found in " + (t - t0) + " ms.");

        System.out.println("" + out.get("message"));


        for (HotelOption o : options) {
            Hotel h = em.find(Hotel.class, Long.parseLong(o.getId()));
            o.setCity(h.getCity().getName());
        }

        long t1 = System.currentTimeMillis();

        System.out.println("data completed in " + (t - t0) + " ms.");



        List<Data> l = new ArrayList<>();
        for (HotelOption o : options) {
            Data d = new Data();
            ERPServiceImpl.fillData(user, em, d, o, null);
            l.add(d);
        }
        out.set("hotel_data", l);

        return out;
    }
}
