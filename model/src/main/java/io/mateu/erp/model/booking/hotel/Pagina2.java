package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.dispo.DispoRQ;
import io.mateu.erp.dispo.HotelAvailabilityRunner;
import io.mateu.erp.dispo.ModeloDispo;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.world.Resort;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.interfaces.WizardPage;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.AvailableHotel;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class Pagina2 implements WizardPage {

    private final Pagina1 pagina1;

    @Output
    private String message;

    @NotNull
    private HotelOption hotel;

    public Pagina2(Pagina1 pagina1) {
        this.pagina1 = pagina1;
    }

    @Override
    public String toString() {
        return "Choose an option";
    }

    public Data getData(UserData user, EntityManager em, Data in) throws Throwable {
        Data out = new Data();


        long t0 = System.currentTimeMillis();

        List<Hotel> hoteles = new ArrayList<>();
        for (Resort c : pagina1.getState().getResorts()) {
            c.getProducts().stream().filter(p -> p instanceof Hotel).forEach(p -> hoteles.add((Hotel) p));
        }

        Partner agencia = pagina1.getAgency();

        //System.out.println("" + hoteles.size() + " hoteles encontrados");

        ModeloDispo modelo = new ModeloDispo() {
            @Override
            public IHotelContract getHotelContract(long id) {
                return em.find(HotelContract.class, id);
            }
        };

        List<io.mateu.erp.dispo.Occupancy> os = new ArrayList<>();
        for (Occupation o : pagina1.getOccupations()) {
            os.add(new io.mateu.erp.dispo.Occupancy(o.getNumberOfRooms(), o.getPaxPerRoom(), o.getAges()));
        }

        DispoRQ rq = new DispoRQ(pagina1.getFormalizationDate(), io.mateu.erp.dispo.Helper.toInt(pagina1.getCheckin()), io.mateu.erp.dispo.Helper.toInt(pagina1.getCheckout()), os, false);


        List<HotelOption> options = new ArrayList<>();
        for (Hotel h : hoteles) {
            AvailableHotel ah = new HotelAvailabilityRunner().check(agencia, h, pagina1.getAgency().getId(), 1, modelo, rq);
            if (ah != null) {

                HotelOption o = new HotelOption();

                o.setId(ah.getHotelId());
                o.setCategory(ah.getHotelCategoryName());
                o.setHotelName(ah.getHotelName());
                if (ah.getBestDeal() != null && ah.getBestDeal().getRetailPrice() != null) o.setBestDeal("" + ah.getBestDeal().getRetailPrice().toString());
                else o.setBestDeal("NOT AVAILABLE");

                options.add(o);

            }
        }

        long t = System.currentTimeMillis();

        out.set("message", "" + options.size() + " hotels found in " + (t - t0) + " ms.");

        System.out.println("" + out.get("message"));


        for (HotelOption o : options) {
            Hotel h = em.find(Hotel.class, Long.parseLong(o.getId()));
            o.setCity(h.getResort().getName());
        }

        long t1 = System.currentTimeMillis();

        System.out.println("data completed in " + (t - t0) + " ms.");



        List<Data> l = new ArrayList<>();
        for (HotelOption o : options) {
            Data d = new Data();
            //todo: recuperar
            //ERPServiceImpl.fillData(user, em, d, o, null);
            l.add(d);
        }
        out.set("hotel_data", l);

        return out;
    }

    @Override
    public WizardPage getPrevious() {
        return pagina1;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public WizardPage getNext() {
        return new Pagina2b(this);
    }
}
