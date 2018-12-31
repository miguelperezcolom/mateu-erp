package io.mateu.erp.consolidador;

import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.partners.PartnerStatus;
import org.easytravelapi.hotel.GetAvailableHotelsRS;

public class Logica {

    public GetAvailableHotelsRS procesar(ModeloConsolidador modelo, DispoRQ rq) {

        GetAvailableHotelsRS rs = new GetAvailableHotelsRS();

        try {

            final long t0 = System.nanoTime();


            // validar token
            AuthToken token = modelo.getAuthToken(rq.getToken());

            if (token == null) throw new Throwable("Token " + rq.getToken() + " not found");

            if (!token.isActive()) throw new Throwable("Token " + rq.getToken() + " is not active");

            // tenemos agencia

            Partner actor = token.getPartner();

            if (actor == null) throw new Throwable("No actor for token " + rq.getToken() + "");

            if (!PartnerStatus.ACTIVE.equals(actor.getStatus())) throw new Throwable("Actor " + actor.getId() + " - " + actor.getName() + " is not active");

            // recorremos el mapa para obtener la lista de recursos (hoteles)

            /*

            List<IResource> resources = modelo.getResources(rq.getResorts());

            List<String> ocups = new ArrayList<>();
            for (Occupancy o : rq.getOccupancies()) if (o != null) ocups.add(o.toString());

            // lista de productos / integraciones
            for (IIntegration integration : actor.getIntegrations()) if (integration.isActive() && integration.isProvidingHotels()) {

                List<DispoRQ> foreignRqs = new ArrayList<>();

                Map<String, IResource> reversedResources = new HashMap<>();


                List<String> resourcesBatch = new ArrayList<>();
                int pos = 0;
                for (IResource r : resources) if (r.getIntegrationId() == integration.getId()) {
                    if (pos > 0 && pos % integration.getMaxResourcesPerRequest() == 0) {
                        // crear rq para la integración
                        foreignRqs.add(new DispoRQ(rq.getToken(), resourcesBatch, rq.getCheckIn(), rq.getCheckout(), rq.getOccupancies(), rq.isIncludeStaticInfo()));
                        resourcesBatch = new ArrayList<>();
                    }
                    resourcesBatch.add(r.getForeignId());
                    reversedResources.put(r.getForeignId(), r);
                    pos++;
                }
                if (resourcesBatch.size() > 0) foreignRqs.add(new DispoRQ(rq.getToken(), resourcesBatch, rq.getCheckIn(), rq.getCheckout(), rq.getOccupancies(), rq.isIncludeStaticInfo()));

                DefaultApi apiInstance = new DefaultApi();

                for (DispoRQ frq : foreignRqs) {

                    // todo: hacer reactivo


                    apiInstance.getApiClient()
                            //.setBasePath("http://test.easytravelapi.com/easytravelapi/rest")
                            .setBasePath(integration.getBaseUrl())
                    ;

                    try {

                        org.easytravelapi.swagger.client.model.GetAvailableHotelsRS frs = apiInstance.getAvailableHotels(
                                frq.getToken()
                                , frq.getResorts()
                                , frq.getCheckIn()
                                , frq.getCheckout()
                                , ocups
                                , frq.isIncludeStaticInfo()
                        );

                        System.out.println(frs);
                        // componemos el resultado
                        for (org.easytravelapi.swagger.client.model.AvailableHotel fh : frs.getHotels()) {
                            AvailableHotel h;
                            rs.getHotels().add(h = new AvailableHotel());
                            h.setHotelId(fh.getHotelId());
                            h.setHotelName(fh.getHotelName());


                            for (org.easytravelapi.swagger.client.model.Option fo : fh.getOptions()) {
                                Option o;
                                h.getOptions().add(o = new Option());

                                for (org.easytravelapi.swagger.client.model.Allocation fa : fo.getDistribution()) {
                                    Allocation a;
                                    o.getDistribution().add(a = new Allocation());

                                    a.setRoomId(fa.getRoomId());
                                    a.setRoomName(fa.getRoomName());
                                    a.setPaxPerRoom(fa.getPaxPerRoom());
                                    if (fa.getAges() != null) a.setAges(Ints.toArray(fa.getAges()));
                                    a.setNumberOfRooms(fa.getNumberOfRooms());

                                    for (org.easytravelapi.swagger.client.model.BoardPrice fp : fa.getPrices()) {

                                        BoardPrice p;
                                        o.getPrices().add(p = new BoardPrice());

                                        // todo: aplicar markup

                                        // todo: convertir moneda

                                        // todo: crear key

                                        p.setNonRefundable(fp.getNonRefundable());
                                        p.setOfferText(fp.getOfferText());
                                        p.setOffer(fp.getOffer());
                                        if (fp.getNetPrice() != null) p.setNetPrice(new Amount());
                                        p.setBoardBasisName(fp.getBoardBasisName());
                                        p.setBoardBasisId(fp.getBoardBasisId());
                                        p.setKey(fp.getKey()); // todo: convertir key
                                        if (fp.getCommission() != null) p.setCommission(new Amount());
                                        p.setOnRequest(fp.getOnRequest());
                                        p.setOnRequestText(fp.getOnRequestText());
                                        if (fp.getRetailPrice() != null) p.setRetailPrice(new Amount());


                                    }

                                }

                            }

                        }

                    } catch (ApiException e) {
                        System.err.println("Exception when calling DefaultApi#getAvailableHotels");
                        e.printStackTrace();
                    }

                }

            }


*/


            System.out.println("Logica.procesar(rq) en " + (System.nanoTime() - t0) + " ns.");


        } catch (Throwable e) {
            e.printStackTrace();
        }



        return rs;

    }
}
