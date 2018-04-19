package io.mateu.erp.tests;

import io.mateu.erp.dispo.model.portfolio.World;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.thirdParties.Integration;
import io.mateu.erp.model.world.City;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.State;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispoDumper {

    public static void main(String... args) throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                Helper.transact("dispo", new JPATransaction() {
                    @Override
                    public void run(EntityManager emd) throws Throwable {

                        World w = null;
                        try {
                            w = emd.find(World.class, 1l);
                        } catch (Exception e) {

                        }
                        if (w == null) {
                            w = new World();
                            w.setId(1l);
                            emd.persist(w);
                        }


                        Map<String, io.mateu.erp.dispo.model.portfolio.Country> paisesEnDispo = new HashMap<>();
                        List<String> paisesVistos = new ArrayList<>();
                        for (Country c : (List<Country>) em.createQuery("select x from " + Country.class.getName() + " x").getResultList()) {

                            io.mateu.erp.dispo.model.portfolio.Country c2 = emd.find(io.mateu.erp.dispo.model.portfolio.Country.class, c.getIsoCode());
                            if (c2 == null) {
                                c2 = new io.mateu.erp.dispo.model.portfolio.Country();
                                c2.setCode(c.getIsoCode());
                                w.getCountries().add(c2);
                                emd.persist(c2);
                            }
                            c2.setName(c.getName());
                            paisesEnDispo.put(c2.getCode(), c2);
                            paisesVistos.add(c.getIsoCode());
                        }

                        for (io.mateu.erp.dispo.model.portfolio.Country c : (List<io.mateu.erp.dispo.model.portfolio.Country>) emd.createQuery("select x from " + io.mateu.erp.dispo.model.portfolio.Country.class.getName() + " x").getResultList())
                        if (!paisesVistos.contains(c.getCode())) {
                            w.getCountries().remove(c);
                            emd.remove(c);
                            paisesEnDispo.put(c.getCode(), c);
                        }



                        Map<Long, io.mateu.erp.dispo.model.portfolio.State> estadosEnDispo = new HashMap<>();
                        List<Long> estadosVistos = new ArrayList<>();
                        for (State c : (List<State>) em.createQuery("select x from " + State.class.getName() + " x").getResultList()) {

                            io.mateu.erp.dispo.model.portfolio.State c2 = emd.find(io.mateu.erp.dispo.model.portfolio.State.class, c.getId());
                            if (c2 == null) {
                                c2 = new io.mateu.erp.dispo.model.portfolio.State();
                                c2.setId(c.getId());
                                c2.setCountry(paisesEnDispo.get(c.getCountry().getIsoCode()));
                                c2.getCountry().getStates().add(c2);
                                emd.persist(c2);
                            }
                            c2.setName(c.getName());

                            if (!c2.getCountry().getCode().equals(c.getCountry().getIsoCode())) {
                                c2.getCountry().getStates().remove(c2);
                                c2.setCountry(paisesEnDispo.get(c.getCountry().getIsoCode()));
                                c2.getCountry().getStates().add(c2);
                            }

                            estadosEnDispo.put(c2.getId(), c2);
                            estadosVistos.add(c.getId());
                        }

                        for (io.mateu.erp.dispo.model.portfolio.State c : (List<io.mateu.erp.dispo.model.portfolio.State>) emd.createQuery("select x from " + io.mateu.erp.dispo.model.portfolio.State.class.getName() + " x").getResultList())
                            if (!estadosVistos.contains(c.getId())) {
                                paisesEnDispo.get(c.getCountry().getCode()).getStates().remove(c);
                                emd.remove(c);
                            }



                        Map<Long, io.mateu.erp.dispo.model.portfolio.City> localidadesEnDispo = new HashMap<>();
                        List<Long> localidadesVistos = new ArrayList<>();
                        for (City c : (List<City>) em.createQuery("select x from " + City.class.getName() + " x").getResultList()) {

                            io.mateu.erp.dispo.model.portfolio.City c2 = emd.find(io.mateu.erp.dispo.model.portfolio.City.class, c.getId());
                            if (c2 == null) {
                                c2 = new io.mateu.erp.dispo.model.portfolio.City();
                                c2.setId(c.getId());
                                c2.setState(estadosEnDispo.get(c.getState().getId()));
                                c2.getState().getCities().add(c2);
                                emd.persist(c2);
                            }
                            c2.setName(c.getName());

                            if (c2.getState().getId() != c.getState().getId()) {
                                c2.getState().getCities().remove(c2);
                                c2.setState(estadosEnDispo.get(c.getState().getId()));
                                c2.getState().getCities().add(c2);
                            }

                            localidadesEnDispo.put(c2.getId(), c2);
                            localidadesVistos.add(c.getId());
                        }

                        for (io.mateu.erp.dispo.model.portfolio.City c : (List<io.mateu.erp.dispo.model.portfolio.City>) emd.createQuery("select x from " + io.mateu.erp.dispo.model.portfolio.City.class.getName() + " x").getResultList())
                            if (!localidadesVistos.contains(c.getId())) {
                                estadosEnDispo.get(c.getState().getId()).getCities().remove(c);
                                emd.remove(c);
                            }




                        Map<Long, io.mateu.erp.dispo.model.portfolio.Resource> recursosEnDispo = new HashMap<>();
                        List<Long> recursosVistos = new ArrayList<>();
                        for (Hotel c : (List<Hotel>) em.createQuery("select x from " + Hotel.class.getName() + " x").getResultList()) {

                            io.mateu.erp.dispo.model.portfolio.Resource c2 = emd.find(io.mateu.erp.dispo.model.portfolio.Resource.class, c.getId());
                            if (c2 == null) {
                                c2 = new io.mateu.erp.dispo.model.portfolio.Resource();
                                c2.setId(c.getId());
                                c2.setCity(localidadesEnDispo.get(c.getCity().getId()));
                                c2.getCity().getResources().add(c2);
                                emd.persist(c2);
                            }
                            c2.setName(c.getName());


                            if (c2.getCity().getId() != c.getCity().getId()) {
                                c2.getCity().getResources().remove(c2);
                                c2.setCity(localidadesEnDispo.get(c.getCity().getId()));
                                c2.getCity().getResources().add(c2);
                            }

                            recursosEnDispo.put(c2.getId(), c2);
                            recursosVistos.add(c.getId());
                        }

                        for (io.mateu.erp.dispo.model.portfolio.Resource c : (List<io.mateu.erp.dispo.model.portfolio.Resource>) emd.createQuery("select x from " + io.mateu.erp.dispo.model.portfolio.Resource.class.getName() + " x").getResultList())
                            if (!recursosVistos.contains(c.getId())) {
                                localidadesEnDispo.get(c.getCity().getId()).getResources().remove(c);
                                emd.remove(c);
                            }








                        Map<Long, io.mateu.erp.dispo.model.integrations.Integration> integracionesEnDispo = new HashMap<>();
                        List<Long> integracionesVistos = new ArrayList<>();
                        for (Integration c : (List<Integration>) em.createQuery("select x from " + Integration.class.getName() + " x").getResultList()) {

                            io.mateu.erp.dispo.model.integrations.Integration c2 = emd.find(io.mateu.erp.dispo.model.integrations.Integration.class, c.getId());
                            if (c2 == null) {
                                c2 = new io.mateu.erp.dispo.model.integrations.Integration();
                                c2.setId(c.getId());
                                emd.persist(c2);
                            }
                            c2.setName(c.getName());
                            c2.setActive(c.isActive());
                            c2.setBaseUrl(c.getBaseUrl());
                            c2.setProvidingHotels(c.isProvidingHotels());

                            integracionesEnDispo.put(c2.getId(), c2);
                            integracionesVistos.add(c.getId());
                        }

                        for (io.mateu.erp.dispo.model.integrations.Integration c : (List<io.mateu.erp.dispo.model.integrations.Integration>) emd.createQuery("select x from " + io.mateu.erp.dispo.model.integrations.Integration.class.getName() + " x").getResultList())
                            if (!integracionesVistos.contains(c.getId())) {
                                emd.remove(c);
                            }



                        Map<Long, io.mateu.erp.dispo.model.common.Actor> actoresEnDispo = new HashMap<>();
                        List<Long> actoresVistos = new ArrayList<>();
                        for (Actor c : (List<Actor>) em.createQuery("select x from " + Actor.class.getName() + " x").getResultList()) {

                            io.mateu.erp.dispo.model.common.Actor c2 = emd.find(io.mateu.erp.dispo.model.common.Actor.class, c.getId());
                            if (c2 == null) {
                                c2 = new io.mateu.erp.dispo.model.common.Actor();
                                c2.setId(c.getId());
                                emd.persist(c2);
                            }
                            c2.setName(c.getName());

                            c2.getIntegrations().clear();
                            for (Integration i : c.getIntegrations()) {
                                c2.getIntegrations().add(integracionesEnDispo.get(i.getId()));
                            }

                            actoresEnDispo.put(c2.getId(), c2);
                            actoresVistos.add(c.getId());
                        }

                        for (io.mateu.erp.dispo.model.common.Actor c : (List<io.mateu.erp.dispo.model.common.Actor>) emd.createQuery("select x from " + io.mateu.erp.dispo.model.common.Actor.class.getName() + " x").getResultList())
                            if (!actoresVistos.contains(c.getId())) {
                                emd.remove(c);
                            }





                        Map<String, io.mateu.erp.dispo.model.auth.AuthToken> tokensEnDispo = new HashMap<>();
                        List<String> tokensVistos = new ArrayList<>();
                        for (AuthToken c : (List<AuthToken>) em.createQuery("select x from " + AuthToken.class.getName() + " x").getResultList()) {

                            io.mateu.erp.dispo.model.auth.AuthToken c2 = emd.find(io.mateu.erp.dispo.model.auth.AuthToken.class, c.getId());
                            if (c2 == null) {
                                c2 = new io.mateu.erp.dispo.model.auth.AuthToken();
                                c2.setId(c.getId());
                                emd.persist(c2);
                            }
                            c2.setActive(c.isActive());
                            c2.setActor(actoresEnDispo.get(c.getActor().getId()));

                            tokensEnDispo.put(c2.getId(), c2);
                            tokensVistos.add(c.getId());
                        }

                        for (io.mateu.erp.dispo.model.auth.AuthToken c : (List<io.mateu.erp.dispo.model.auth.AuthToken>) emd.createQuery("select x from " + io.mateu.erp.dispo.model.auth.AuthToken.class.getName() + " x").getResultList())
                            if (!tokensVistos.contains(c.getId())) {
                                emd.remove(c);
                            }







                    }
                });


            }
        });

    }

}
