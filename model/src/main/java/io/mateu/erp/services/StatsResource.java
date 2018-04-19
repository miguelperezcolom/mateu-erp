package io.mateu.erp.services;

import io.mateu.erp.model.authentication.User;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/stats")
public class StatsResource {

    @Path("/ping")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {

        try {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    User u = em.find(User.class, "admin");

                    System.out.println("user admin exists");

                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        }

        return "pong";
    }


    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @Path("/hotelavailability")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> hotelavailability() throws Throwable {
        Map<String, Object> d = new HashMap<>();



        // consumo cpu

        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("get")
                    && Modifier.isPublic(method.getModifiers())) {
                Object value;
                try {
                    value = method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    value = e;
                } // try
                System.out.println(method.getName() + " = " + value);

                if ("getSystemCpuLoad".equals(method.getName())) d.put("cpu", value);
                else if ("getProcessCpuLoad".equals(method.getName())) d.put("proceso", value);

            } // if
        } // for



        // heap


        // Get current size of heap in bytes
        long heapSize = Runtime.getRuntime().totalMemory();

        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
        long heapMaxSize = Runtime.getRuntime().maxMemory();

        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
        long heapFreeSize = Runtime.getRuntime().freeMemory();


        d.put("heapmax", heapMaxSize);
        d.put("heapsize", heapSize);
        d.put("heapfree", heapFreeSize);





        // stats

        HotelAvailabilityStatsData s = HotelAvailabilityStats.get(true);

        if (s != null) {

            d.put("checkinAvg", s.checkinAvg);
            d.put("checkinMax", s.checkinMax);
            d.put("checkinMin", s.checkinMin);
            d.put("checkoutAvg", s.checkoutAvg);
            d.put("checkoutMax", s.checkoutMax);
            d.put("checkoutMin", s.checkoutMin);
            d.put("priceAvg", s.priceAvg);
            d.put("priceMax", s.priceMax);
            d.put("priceMin", s.priceMin);
            d.put("returnedHotelsNoAvg", s.returnedHotelsNoAvg);
            d.put("returnedHotelsNoMax", s.returnedHotelsNoMax);
            d.put("returnedHotelsNoMin", s.returnedHotelsNoMin);
            d.put("returnedPricesNoAvg", s.returnedPricesNoAvg);
            d.put("returnedPricesNoMax", s.returnedPricesNoMax);
            d.put("returnedPricesNoMin", s.returnedPricesNoMin);
            d.put("stayAvg", s.stayAvg);
            d.put("stayMax", s.stayMax);
            d.put("stayMin", s.stayMin);
            d.put("totalRqs", s.totalRqs);

        }



        return d;
    }


    public static void main(String... args) throws Throwable {
        System.out.println(new StatsResource().hotelavailability());
    }
}