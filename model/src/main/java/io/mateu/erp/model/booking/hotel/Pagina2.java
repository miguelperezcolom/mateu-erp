package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.dispo.DispoRQ;
import io.mateu.erp.dispo.HotelAvailabilityRunner;
import io.mateu.erp.dispo.ModeloDispo;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import io.mateu.erp.model.world.City;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.AbstractServerSideWizardPage;
import io.mateu.ui.mdd.server.ERPServiceImpl;
import io.mateu.ui.mdd.server.annotations.Output;
import io.mateu.ui.mdd.server.annotations.UseGridToSelect;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.Allocation;
import org.easytravelapi.hotel.AvailableHotel;
import org.easytravelapi.hotel.BoardPrice;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class Pagina2 extends AbstractServerSideWizardPage {

    @Output
    private String message;

    @NotNull
    @UseGridToSelect
    private Option option;

    @Override
    public String getTitle() {
        return "Choose an option";
    }

    @Override
    public Data getData(Data in) throws Throwable {
        Data out = new Data();

        Helper.transact((JPATransaction) (em) -> {

            Pagina1 p = new Pagina1();
            p.fill(em, in);


            long t0 = System.currentTimeMillis();

            List<Hotel> hoteles = new ArrayList<>();
            for (City c : p.getState().getCities()) {
                hoteles.addAll(c.getHotels());
            }

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

            DispoRQ rq = new DispoRQ(io.mateu.erp.dispo.Helper.toInt(p.getCheckin()), io.mateu.erp.dispo.Helper.toInt(p.getCheckout()), os, false);


            List<Option> options = new ArrayList<>();
            for (Hotel h : hoteles) {
                AvailableHotel ah = new HotelAvailabilityRunner().check(h, p.getAgency().getId(), 1, modelo, rq);
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
                        Option o = new Option();

                        o.setBoard(bp.getBoardBasisName());
                        o.setCategory(ah.getHotelCategoryName());
                        o.setCity(ah.getHotelCategoryId());
                        o.setCurrency(bp.getNetPrice().getCurrencyIsoCode());
                        o.setHotelName(ah.getHotelName());
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
            for (Option o : options) {
                Data d = new Data();
                ERPServiceImpl.fillData(d, o);
                l.add(d);
            }
            out.set("option_data", l);


        });

        return out;
    }
}
