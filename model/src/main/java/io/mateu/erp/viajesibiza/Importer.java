package io.mateu.erp.viajesibiza;

import io.mateu.erp.model.accounting.AccountingPlan;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.booking.transfer.TransferPointMapping;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.importing.*;
import io.mateu.erp.model.organization.Company;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Market;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.partners.PartnerStatus;
import io.mateu.erp.model.population.Populator;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.transfer.*;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.world.Zone;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.USER_STATUS;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import javax.persistence.EntityManager;
import javax.xml.XMLConstants;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Importer {

    private static Office oficina;
    private static Company cia;
    private static Currency eur;
    private static Map<String, Zone> ciudades = new HashMap<>();
    private static Map<String, TransferPoint> puntos = new HashMap<>();
    private static Map<String, User> usuarios = new HashMap<>();
    private static Map<String, Partner> partners = new HashMap<>();
    private static Map<String, io.mateu.erp.model.product.transfer.Zone> zonas = new HashMap<>();
    private static Map<String, Vehicle> vehiculos = new HashMap<>();
    private static Map<String, PointOfSale> poses = new HashMap<>();
    private static Map<String, Market> mercados = new HashMap<>();
    private static ProductLine lineaProducto;
    private static BillingConcept concepto;

    public static void main(String[] args) {
        System.setProperty("appconf", "/home/miguel/work/quotravel.properties");
        importar("/home/miguel/work/viajesibiza/todo.xml");

        ShuttleDirectAutoImport.run();

        ShuttleDirectImportTask.run();

        TravelRepublicAutoImport.run();

        TravelRepublicImportTask.run();

        TransferBookingRequest.run();

        WorkflowEngine.exit(0);
    }

    private static void importar(String path) {

        try {
            Helper.runCommand("dropdb -U postgres quotravel; createdb -U postgres quotravel");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
            new Populator().populate(AppConfig.class);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        try {
            Helper.transact(em -> {

                SAXBuilder b = new SAXBuilder();
                b.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
                Document xml = b.build(path);

                crearEstructura(em, xml);

                //crearReservas(em, xml);

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        try {
            Helper.transact(em -> {

                ((List<Service>)em.createQuery("select x from " + Service.class.getName() + " x").getResultList()).forEach(s -> s.setAlreadyPurchased(true));

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        //WorkflowEngine.exit(0);
    }

    private static void crearReservas(EntityManager em, Document xml) {

        xml.getRootElement().getChild("bookings").getChildren().forEach(eb -> {
            if (eb.getChildren().size() > 0) {

                TransferBooking b = new TransferBooking();
                b.setAudit(new Audit(MDD.getCurrentUser()));
                b.setLeadName(eb.getAttributeValue("leadName"));
                b.setAgencyReference(eb.getAttributeValue("ref"));
                b.setAgency(partners.get(eb.getAttributeValue("agency")));
                b.setEmail(eb.getAttributeValue("email"));
                b.setTelephone(eb.getAttributeValue("telephone"));
                if (eb.getAttribute("confirmed") != null) b.setConfirmed(true);

                b.setTransferType(TransferType.SHUTTLE);
                b.setStart(LocalDate.now());
                b.setEnd(LocalDate.now());

                b.setAlreadyInvoiced(true);
                b.setAlreadyPurchased(true);

                b.setDeprecated(true);

                eb.getChildren().forEach(es -> {
                    b.setTransferType(TransferType.valueOf(es.getAttributeValue("transferType")));
                    b.setPos(poses.get(es.getAttributeValue("pos")));
                    b.setAdults(Integer.parseInt(es.getAttributeValue("pax")));

                    b.setTotalValue(Helper.roundEuros(b.getTotalValue() + Double.parseDouble(es.getAttributeValue("totalNet"))));
                    b.setTotalCost(Helper.roundEuros(b.getTotalCost() + Double.parseDouble(es.getAttributeValue("totalCost"))));

                    if ("outbound".equalsIgnoreCase(es.getAttributeValue("direction"))) {
                        b.setOrigin(puntos.get(es.getAttributeValue("dropoff")));
                        b.setDestination(puntos.get(es.getAttributeValue("pickup")));
                        b.setOrigin(puntos.get(es.getAttributeValue("effectiveDropoff")));
                        b.setDestination(puntos.get(es.getAttributeValue("effectivePickup")));
                        b.setDepartureFlightNumber(es.getAttributeValue("flightNumber"));
                        b.setDepartureFlightTime(LocalDateTime.parse(es.getAttributeValue("flightTime")));
                        b.setDepartureFlightDestination(es.getAttributeValue("flightOriginDestination"));
                    } else {
                        b.setOrigin(puntos.get(es.getAttributeValue("pickup")));
                        b.setDestination(puntos.get(es.getAttributeValue("dropoff")));
                        b.setOrigin(puntos.get(es.getAttributeValue("effectivePickup")));
                        b.setDestination(puntos.get(es.getAttributeValue("effectiveDropoff")));
                        b.setArrivalFlightNumber(es.getAttributeValue("flightNumber"));
                        b.setArrivalFlightTime(LocalDateTime.parse(es.getAttributeValue("flightTime")));
                        b.setArrivalFlightOrigin(es.getAttributeValue("flightOriginDestination"));
                    }


                });

                if (b.getOrigin() != null && b.getDestination() != null && ((b.getArrivalFlightTime() != null && b.getArrivalFlightTime().isBefore(LocalDateTime.now())) || (b.getDepartureFlightTime() != null && b.getDepartureFlightTime().isBefore(LocalDateTime.now())))) em.persist(b);

            }
        });

    }

    private static void crearEstructura(EntityManager em, Document xml) {

        cia = new Company();
        cia.setName("Viajes Ibiza");
        FinancialAgent agente;
        cia.setFinancialAgent(agente = new FinancialAgent());
        agente.setName("Viajes Ibiza");
        agente.setCurrency(em.find(Currency.class, "EUR"));
        AccountingPlan plan;
        cia.setAccountingPlan(plan = new AccountingPlan());
        plan.setName("Plan general");
        plan.setCurrency(eur = em.find(Currency.class, "EUR"));
        em.persist(agente);
        em.persist(plan);
        em.persist(cia);



        xml.getRootElement().getChild("countries").getChildren().forEach(ec -> {
            Country cou = new Country();
            cou.setIsoCode(ec.getAttributeValue("code"));
            cou.setName(ec.getAttributeValue("name"));
            em.persist(cou);

            ec.getChildren().forEach(es -> {
                Destination d;
                cou.getDestinations().add(d = new Destination());
                d.setCountry(cou);
                d.setId(Long.parseLong(es.getAttributeValue("id")));
                d.setName(es.getAttributeValue("name"));
                em.persist(d);


                es.getChildren().forEach(el -> {

                    Zone z;
                    d.getZones().add(z = new Zone());
                    z.setDestination(d);
                    z.setName(el.getAttributeValue("name"));
                    ciudades.put(el.getAttributeValue("id"), z);
                    em.persist(z);

                    el.getChildren().forEach(ep -> {

                        TransferPoint tp;
                        z.getTransferPoints().add(tp = new TransferPoint());
                        tp.setZone(z);
                        tp.setId(Long.parseLong(ep.getAttributeValue("id")));
                        tp.setName(ep.getAttributeValue("name"));

                        tp.setType(TransferPointType.valueOf(ep.getAttributeValue("type")));
                        tp.setOffice(getOffice(em));
                        if (ep.getAttribute("address") != null) tp.setAddress(ep.getAttributeValue("address"));
                        if (ep.getAttribute("email") != null) tp.setEmail(ep.getAttributeValue("email"));
                        if (ep.getAttribute("fax") != null) tp.setFax(ep.getAttributeValue("fax"));
                        if (ep.getAttribute("telephone") != null) tp.setTelephone(ep.getAttributeValue("telephone"));
                        if (ep.getAttribute("instructions") != null) tp.setInstructions(ep.getAttributeValue("instructions"));
                        puntos.put(ep.getAttributeValue("id"), tp);
                        //if (ep.getAttribute("shuttleAlternatePoint") != null) tp.setAlternatePointForShuttle();

                        em.persist(tp);

                    });

                });

            });

        });


        xml.getRootElement().getChild("countries").getChildren().forEach(ec -> {
            ec.getChildren().forEach(es -> {
                es.getChildren().forEach(el -> {
                    el.getChildren().forEach(ep -> {
                        if (ep.getAttribute("shuttleAlternatePoint") != null) {
                            puntos.get(ep.getAttributeValue("id")).setAlternatePointForShuttle(puntos.get(ep.getAttributeValue("shuttleAlternatePoint")));
                        }
                    });
                });
            });
        });


        xml.getRootElement().getChild("users").getChildren().forEach(eu -> {
            User u = em.find(User.class, eu.getAttributeValue("login"));
            if (u == null) {
                u = new User();
                u.setLogin(eu.getAttributeValue("login"));
                u.setName(eu.getAttributeValue("name"));
                u.setEmail(eu.getAttributeValue("email"));
                u.setPassword(eu.getAttributeValue("password"));
                u.setStatus(USER_STATUS.ACTIVE);
                em.persist(u);
            }
            usuarios.put(eu.getAttributeValue("login"), u);
        });

        xml.getRootElement().getChild("poses").getChildren().forEach(e -> {
            PointOfSale p = new PointOfSale();
            p.setName(e.getAttributeValue("name"));
            poses.put(e.getAttributeValue("id"), p);
            em.persist(p);
        });

        xml.getRootElement().getChild("markets").getChildren().forEach(e -> {
            Market p = new Market();
            p.setName(e.getAttributeValue("name"));
            mercados.put(e.getAttributeValue("id"), p);
            em.persist(p);
        });

        xml.getRootElement().getChild("actors").getChildren().forEach(ea -> {
            Partner p = new Partner();
            p.setName(ea.getAttributeValue("name"));
            p.setEmail(ea.getAttributeValue("email"));
            p.setCurrency(eur);
            p.setStatus(ea.getAttribute("active") != null?PartnerStatus.ACTIVE:PartnerStatus.INACTIVE);
            p.setOneLinePerBooking(ea.getAttribute("oneLinePerBooking") != null);
            if (ea.getAttribute("ordersSendingMethod") != null) p.setOrdersSendingMethod(PurchaseOrderSendingMethod.valueOf(ea.getAttributeValue("ordersSendingMethod")));
            p.setSendOrdersTo(ea.getAttributeValue("sendOrdersTo"));
            if (ea.getAttribute("autoOrderConfirm") != null) p.setAutomaticOrderSending(true);
            p.setFullAddress(ea.getAttributeValue("address"));
            p.setAgency(ea.getAttribute("agency") != null);
            p.setProvider(ea.getAttribute("provider") != null);
            if (ea.getAttribute("market") != null) p.setMarket(mercados.get(ea.getAttributeValue("market")));
            em.persist(p);
            partners.put(ea.getAttributeValue("id"), p);
        });

        xml.getRootElement().getChild("mappings").getChildren().forEach(e -> {
            TransferPointMapping m = new TransferPointMapping();
            //m.setCreatedBy(ea.getAttributeValue("name")); //todo: falta relacionar con el servicio
            if (e.getAttributeValue("point") != null) m.setPoint(puntos.get(e.getAttributeValue("point")));
            m.setText(e.getAttributeValue("text"));
            em.persist(m);
        });

        xml.getRootElement().getChild("zones").getChildren().forEach(e -> {
            io.mateu.erp.model.product.transfer.Zone z = new io.mateu.erp.model.product.transfer.Zone();
            z.setName(e.getAttributeValue("name"));
            z.setGroup(e.getAttributeValue("group"));
            for (String i : e.getAttributeValue("cities").split(",")) {
                if (ciudades.get(i) != null) z.getCities().add(ciudades.get(i));
                else System.out.println("No existe la ciudad con id = " + i);
            }
            for (String i : e.getAttributeValue("points").split(",")) {
                if (puntos.get(i) != null) z.getPoints().add(puntos.get(i));
                else System.out.println("No existe el punto con id = " + i);
            }
            zonas.put(e.getAttributeValue("id"), z);
            em.persist(z);
        });

        xml.getRootElement().getChild("vehicles").getChildren().forEach(e -> {
            Vehicle v = new Vehicle();
            v.setName(e.getAttributeValue("name"));
            v.setMinPax(Integer.parseInt(e.getAttributeValue("minPax")));
            v.setMaxPax(Integer.parseInt(e.getAttributeValue("maxPax")));
            vehiculos.put(e.getAttributeValue("id"), v);
            em.persist(v);
        });

        xml.getRootElement().getChild("contracts").getChildren().forEach(e -> {
            Contract c = new Contract();
            c.setOffice(oficina);
            c.setCurrency(eur);
            c.setProductLine(lineaProducto);
            c.setBillingConcept(concepto);
            c.setTitle(e.getAttributeValue("title"));
            if (e.getAttribute("supplier") != null) c.setSupplier(partners.get(e.getAttributeValue("supplier")));
            c.setType(ContractType.valueOf(e.getAttributeValue("type")));
            c.setValidFrom(LocalDate.parse(e.getAttributeValue("validFrom")));
            c.setValidTo(LocalDate.parse(e.getAttributeValue("validTo")));
            c.setMinPaxPerBooking(Integer.parseInt(e.getAttributeValue("minPaxPerBooking")));
            if (e.getAttribute("vatincluded") != null) c.setVATIncluded(true);
            for (String i : e.getAttributeValue("targets").split(",")) {
                if (partners.get(i) != null) c.getPartners().add(partners.get(i));
                else System.out.println("No existe el partner con id = " + i);
            }

            e.getChildren().forEach(x -> {
                Price p = new Price();
                c.getPrices().add(p);
                p.setContract(c);
                p.setOrigin(zonas.get(x.getAttributeValue("fromZone")));
                p.setDestination(zonas.get(x.getAttributeValue("toZone")));
                p.setPrice(Double.parseDouble(x.getAttributeValue("price")));
                p.setPricePer(PricePer.valueOf(x.getAttributeValue("per")));
                p.setTransferType(TransferType.valueOf(e.getAttributeValue("transferType")));
                p.setVehicle(vehiculos.get(x.getAttributeValue("vehicle")));
                //p.setFromPax();
                //p.setToPax();
                em.persist(p);
            });

            em.persist(c);
        });


        {
            ShuttleDirectAutoImport i = new ShuttleDirectAutoImport();
            i.setIdTransportista("476");
            i.setCustomer(partners.values().stream().filter(p -> "shuttle direct".equalsIgnoreCase(p.getName())).findFirst().get());
            i.setLogin("VIB");
            i.setName("Shuttle Direct");
            i.setOffice(oficina);
            i.setPassword("f8qu34w8f8aq");
            i.setPointOfSale(poses.values().stream().filter(p -> "importación".equalsIgnoreCase(p.getName())).findFirst().get());
            i.setUrl("http://www2.shuttledirect.com/supplier/admin/exportarListadoTransportista2Xml.php");
            em.persist(i);
        }

        {
            TravelRepublicAutoImport i = new TravelRepublicAutoImport();
            i.setCustomer(partners.values().stream().filter(p -> "travelrepublic".equalsIgnoreCase(p.getName())).findFirst().get());
            i.setLogin("xxxx");
            i.setName("Travelrepublic");
            i.setOffice(oficina);
            i.setPassword("xxxx");
            i.setPointOfSale(poses.values().stream().filter(p -> "importación".equalsIgnoreCase(p.getName())).findFirst().get());
            i.setUrl("xxxx");
            em.persist(i);
        }
    }

    private static Office getOffice(EntityManager em) {
        if (oficina == null) {
            oficina = new Office();
            oficina.setCompany(cia);
            oficina.setName("Ibiza");
            oficina.setCurrency(eur);
            oficina.setCity(ciudades.values().iterator().next());
            em.persist(oficina);

            lineaProducto = new ProductLine();
            lineaProducto.setName("Traslados");
            em.persist(lineaProducto);

            concepto = new BillingConcept();
            concepto.setCode("TRA");
            concepto.setLocalizationRule(LocalizationRule.SERVICE);
            concepto.setTransfer(true);
            concepto.setName("Traslado");
            em.persist(concepto);

        }
        return oficina;
    }

}
