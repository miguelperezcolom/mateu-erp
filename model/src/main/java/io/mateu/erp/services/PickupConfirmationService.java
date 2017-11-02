package io.mateu.erp.services;

import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/pickupconfirmation")
public class PickupConfirmationService {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @Path("q")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> q(@QueryParam("p") String p) throws Throwable {
        Map<String, Object> d = new HashMap<>();
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                List<Service> l = em.createQuery("select x from " + Service.class.getName() + " x where lower(x.booking.agencyReference) like lower(:r)").setParameter("r", p).getResultList();
                if (l.size() > 0) {
                    boolean found = false;
                    for (Service s : l) {
                        if (s instanceof TransferService) {
                            TransferService t = (TransferService) s;
                            if (t.getPickupTime() != null) {
                                found = true;
                                d.put("result", "ok");
                                d.put("message", "Found booking with reference " + p + "");
                                d.put("service", t.getData());
                                t.setPickupConfirmedByWeb(LocalDateTime.now());
                            }
                        }
                    }
                    if (!found) {
                        d.put("result", "error");
                        d.put("message", "No pickup info for booking with reference " + p + ". Please check again within 24 hours before departure.");
                    }
                } else {
                    d.put("result", "error");
                    d.put("message", "No booking with reference " + p);
                }
            }
        });
        return d;
    }


    public static void main(String... args) throws Throwable {
        System.out.println(new PickupConfirmationService().q("HEqaKP"));
    }
}