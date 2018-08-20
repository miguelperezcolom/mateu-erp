package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.dispo.DispoRQ;
import io.mateu.erp.dispo.HotelAvailabilityRunner;
import io.mateu.erp.dispo.ModeloDispo;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.UseGridToSelect;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.views.AbstractServerSideWizardPage;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.Allocation;
import org.easytravelapi.hotel.AvailableHotel;
import org.easytravelapi.hotel.BoardPrice;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class Pagina2b extends AbstractServerSideWizardPage {

    @Output
    private String message;

    @Output
    private String selectedOption;

    @NotNull
    @UseGridToSelect
    private PriceOption option;

    @Override
    public String getTitle() {
        return "Choose an option";
    }

    @Override
    public Data getData(UserData user, EntityManager em, Data in) throws Throwable {
        Data out = new Data();


        Pagina1 p = new Pagina1();
        p.fill(em, user, in);

        Pagina2 p2 = new Pagina2();
        p2.fill(em, user, in);

        out.set("selectedOption", "" + p2.getHotel().getHotelName() + " " + p2.getHotel().getCategory()
                + "\n" + p2.getHotel().getCity()
        );


        long t0 = System.currentTimeMillis();

        Partner agencia = p.getAgency();

        //System.out.println("" + hoteles.size() + " hoteles encontrados");

        ModeloDispo modelo = new ModeloDispo() {
            @Override
            public IHotelContract getHotelContract(long id) {
                return em.find(HotelContract.class, id);
            }
        };

        List<Hotel> hoteles = new ArrayList<>();
        hoteles.add(em.find(Hotel.class, Long.parseLong(in.getData("hotel").getString("id"))));

        List<io.mateu.erp.dispo.Occupancy> os = new ArrayList<>();
        for (Occupation o : p.getOccupations()) {
            os.add(new io.mateu.erp.dispo.Occupancy(o.getNumberOfRooms(), o.getPaxPerRoom(), o.getAges()));
        }

        DispoRQ rq = new DispoRQ(p.getFormalizationDate(), io.mateu.erp.dispo.Helper.toInt(p.getCheckin()), io.mateu.erp.dispo.Helper.toInt(p.getCheckout()), os, false);


        List<PriceOption> options = new ArrayList<>();
        for (Hotel h : hoteles) {
            AvailableHotel ah = new HotelAvailabilityRunner().check(agencia, h, p.getAgency().getId(), 1, modelo, rq);
            if (ah != null) for (org.easytravelapi.hotel.Option xo : ah.getOptions()) {

                StringBuffer sb = new StringBuffer();
                for (Allocation a : xo.getDistribution()) {
                    sb.append(a.getNumberOfRooms() * a.getPaxPerRoom());
                    sb.append(" pax in ");
                    sb.append(a.getNumberOfRooms());
                    sb.append(" ");
                    sb.append(a.getRoomName());
                }

                String dist = sb.toString();

                for (BoardPrice bp : xo.getPrices()) {
                    PriceOption o = new PriceOption();

                    o.setBoard(bp.getBoardBasisName());
                    o.setCurrency(bp.getNetPrice().getCurrencyIsoCode());
                    o.setKey(bp.getKey());
                    o.setPrice(bp.getNetPrice().getValue());
                    o.setRooms(dist);

                    options.add(o);
                }
            }
        }

        long t = System.currentTimeMillis();

        out.set("message", "" + options.size() + " prices found in " + (t - t0) + " ms.");

        List<Data> l = new ArrayList<>();
        for (PriceOption o : options) {
            Data d = new Data();
            //todo: recuperar
            //ERPServiceImpl.fillData(user, em, d, o, null);
            l.add(d);
        }
        out.set("option_data", l);

        return out;
    }
}
