package io.mateu.erp.model.caval;

import io.mateu.erp.model.product.DataSheet;
import io.mateu.erp.model.product.DataSheetImage;
import io.mateu.erp.model.product.FeatureGroup;
import io.mateu.erp.model.product.FeatureValue;
import io.mateu.mdd.core.data.Pair;
import io.mateu.mdd.core.model.common.Resource;
import io.mateu.mdd.core.model.multilanguage.Literal;
import io.mateu.mdd.core.util.Helper;
import travel.caval._20091127.commons.*;
import travel.caval._20091127.hotelbooking.*;

import javax.persistence.EntityManager;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CAVALClient {

    private static CAVALClient instance;

    private static final QName SERVICE_NAME_HOTEL = new QName("http://caval.travel/20091127/hotelBooking", "HotelBookingService");
    private static final QName SERVICE_NAME_COMMONS = new QName("http://caval.travel/20091127/commons", "CommonsBookingService");


    HotelBookingService hotelPort;
    CommonsBookingService commonsPort;


    public CAVALClient() {
        hotelPort = new HotelBookingService_Service(HotelBookingService_Service.WSDL_LOCATION, SERVICE_NAME_HOTEL).getHotelBookingServicePort();

        // Use the BindingProvider's context to set the endpoint
        ((BindingProvider) hotelPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://xml.marjetincoming.com/serveis/caval/20091127/soap/HotelBookingService");


        commonsPort = new CommonsBookingService_Service(CommonsBookingService_Service.WSDL_LOCATION, SERVICE_NAME_COMMONS).getCommonsBookingServicePort();

        // Use the BindingProvider's context to set the endpoint
        ((BindingProvider) commonsPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://xml.marjetincoming.com/serveis/caval/20091127/soap/CommonsBookingService");

    }

    public static CAVALClient get() {
        if (instance == null) instance = new CAVALClient();
        return instance;
    }


    public List<Pair<String, String>> getHotels() {
        List<Pair<String, String>> l = new ArrayList<>();

        GetWholeSupportedMap params = new GetWholeSupportedMap();
        CavalGetWholeSupportedMapRQ rq;
        params.setRq(rq = new CavalGetWholeSupportedMapRQ());
        rq.setAgentId("1");
        rq.setPropietaryCodes(true);
        rq.setLanguage("es");
        rq.setLogin("MIGUEL");
        rq.setPassword("GC5jQW");
        GetWholeSupportedMapResponse rs = commonsPort.getWholeSupportedMap(params);

        System.out.println("Result from CAVAL: " + rs.getReturn().getResultCode() + " " + rs.getReturn().getMessage());

        for (Country c : rs.getReturn().getCountries()) {
            for (State s : c.getStates()) {
                for (City cty : s.getCities()) {
                    for (Hotel h : cty.getHotels()) {
                        l.add(new Pair<>(h.getId(), "" + h.getName() + " (" + h.getId() + " - " + cty.getName() + ")"));
                    }
                }
            }
        }

        return l;
    }


    public void updateDataSheet(EntityManager em, String cavalHotelId, DataSheet d) throws IOException {
        GetEstablishmentDataSheets params = new GetEstablishmentDataSheets();
        CavalGetEstablishmentDataSheetsRQ rq = new CavalGetEstablishmentDataSheetsRQ();
        rq.setAgentId("1");
        rq.getEstablishmentIds().add(cavalHotelId);
        rq.setLanguage("es");
        rq.setLogin("MIGUEL");
        rq.setPassword("GC5jQW");
        params.setRq(rq);
        GetEstablishmentDataSheetsResponse rs = hotelPort.getEstablishmentDataSheets(params);

        System.out.println("Result from CAVAL: " + rs.getReturn().getResultCode() + " " + rs.getReturn().getMessage());

        System.out.println("rs = " + Helper.toJson(rs.getReturn()));

        for (EstablishmentDataSheet eds : rs.getReturn().getDataSheets()) {
            d.setMainImage(new Resource(new URL(eds.getMainImageUrl())));
            d.setName(eds.getName());
            d.setDescription(new Literal("", eds.getLongDescription()));
            for (String imgUrl : eds.getOtherImagesUrls()) {

                Optional<DataSheetImage> found = d.getImages().stream().filter(x -> x.getImage() != null && imgUrl.endsWith(x.getImage().getName())).findFirst();

                if (!found.isPresent()) {
                    DataSheetImage i;
                    d.getImages().add(i = new DataSheetImage());
                    i.setDataSheet(d);
                    i.setImage(new Resource(new URL(imgUrl)));
                }

            }
            for (FeaturesGroup efg : eds.getFeaturesGroups()) {
                for (Feature f : efg.getFeatures()) {

                    Optional<FeatureValue> found = d.getFeatures().stream().filter(v -> v.getFeature().getName().getEs().equalsIgnoreCase(f.getDescription())).findFirst();

                    if (!found.isPresent()) {
                        FeatureValue fv;
                        d.getFeatures().add(fv = new FeatureValue());
                        fv.setDataSheet(d);
                        fv.setFeature(getFeature(em, efg, f));
                        fv.setValue(f.getValue());
                    }

                }
            }
        }

    }

    private io.mateu.erp.model.product.Feature getFeature(EntityManager em, FeaturesGroup efg, Feature f) {

        Optional<FeatureGroup> o = ((List<FeatureGroup>) em.createQuery("select x from " + FeatureGroup.class.getName() + " x").getResultList()).stream().filter(g -> g.getName().getEs().equalsIgnoreCase(efg.getName())).findFirst();

        FeatureGroup fg;
        if (o.isPresent()) fg = o.get();
        else {
            fg = new FeatureGroup();
            fg.setName(new Literal("", efg.getName()));
            em.persist(fg);
            em.flush();
        }

        Optional<io.mateu.erp.model.product.Feature> of = ((List<io.mateu.erp.model.product.Feature>) em.createQuery("select x from " + io.mateu.erp.model.product.Feature.class.getName() + " x").getResultList()).stream().filter(g -> g.getName().getEs().equalsIgnoreCase(f.getDescription())).findFirst();

        if (of.isPresent()) return of.get();
        else {
            io.mateu.erp.model.product.Feature fx = new io.mateu.erp.model.product.Feature();
            fx.setName(new Literal("", f.getDescription()));
            fx.setGroup(fg);
            fg.getFeatures().add(fx);
            em.persist(fx);
            em.flush();
            return fx;
        }

    }


    public static void main(String[] args) throws Throwable {

        System.setProperty("appconf", "/home/miguel/work/erp.properties");



        List<Pair<String, String>> hs;
        System.out.println(hs = get().getHotels());
        Optional<Pair<String, String>> o = hs.stream().filter(p -> p.getValue().toLowerCase().contains("puchet")).findFirst();
        if (o.isPresent()) {
            System.out.println("puchet = " + o.get());
        }


        Helper.transact(em -> {
            DataSheet d;
            get().updateDataSheet(em, "479", d = new DataSheet());
            System.out.println("datasheet = " + d);
        });


    }

}
