package io.mateu.erp;


import io.mateu.erp.dispo.DispoRQ;
import io.mateu.erp.dispo.HotelAvailabilityRunner;
import io.mateu.erp.dispo.ModeloDispo;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.tests.TestPopulator;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import org.easytravelapi.hotel.AvailableHotel;
import org.easytravelapi.hotel.Occupancy;
import org.easytravelapi.hotel.Option;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DispoTest {


    public static void main(String... args) throws Throwable {

        System.setProperty("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/quotest");

        System.out.println("comprobamos que exiten datos...");

        final boolean[] hayDatos = {false};
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                try {

                    hayDatos[0] = AppConfig.get(em).getId() == 1;
                } catch (Throwable e) {

                }

            }
        });

        if (!hayDatos[0]) {
            TestPopulator.populateAll();
        }


        List<Long> idsHoteles = new ArrayList<>();

        Helper.transact(new JPATransaction() {
                            @Override
                            public void run(EntityManager em) throws Throwable {

                                List<Hotel> hoteles = em.createQuery("select h from " + Hotel.class.getName() + " h").getResultList();

                                System.out.println("" + hoteles.size() + " hoteles encontrados");

                                int numContratos = 0;
                                for (Hotel h : hoteles) {
                                    numContratos += h.getContracts().size();
                                }

                                System.out.println("" + numContratos + " contratos encontrados");

                                for (Hotel h : hoteles) idsHoteles.add(h.getId());
                            }
                        });


        List<AvailableHotel> dispo = new ArrayList<>();

        for (int i = 0; i < 300; i++) {
            long t0 = System.currentTimeMillis();

            dispo = new ArrayList<>();


            List<AvailableHotel> finalDispo = dispo;
            Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = new ArrayList<>();
                for (long idHotel : idsHoteles) {
                    hoteles.add(em.find(Hotel.class, idHotel));
                }

                Actor a = em.find(Actor.class, 1l);

                //System.out.println("" + hoteles.size() + " hoteles encontrados");

                ModeloDispo modelo = new ModeloDispo() {
                    @Override
                    public IHotelContract getHotelContract(long id) {
                        return em.find(HotelContract.class, id);
                    }
                };

                DispoRQ rq = new DispoRQ(LocalDate.now(), 20180601, 20180615, Arrays.asList(new Occupancy(1, 2, null)), false);




                    for (int j = 0; j < 100; j++) for (Hotel h : hoteles) {
                        AvailableHotel ah = new HotelAvailabilityRunner().check(a, h, 1, 1, modelo, rq);
                        if (ah != null) finalDispo.add(ah);
                    }





                //System.out.println(Helper.toJson(dispo));

            }
        });

            long t = System.currentTimeMillis();
            int numeroPrecios = 0;
            for (AvailableHotel ah : finalDispo) {
                for (Option o : ah.getOptions()) {
                    numeroPrecios += o.getPrices().size();
                }
            }

            System.out.println("" + dispo.size() + " hoteles disponibles / " + numeroPrecios + " precios en " + (t - t0) + " ms.");
        }

    }


}
