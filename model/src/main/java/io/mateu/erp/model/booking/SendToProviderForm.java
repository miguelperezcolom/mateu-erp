package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.workflow.SendPurchaseOrdersByEmailTask;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter@Setter
public class SendToProviderForm {

    @Ignored
    private java.io.File temp;

    @Ignored
    private final Service service;


    private Provider provider;

    private String email;

    @TextArea
    private String postscript;

    @IFrame
    @FullWidth
    private URL preview;



    public SendToProviderForm(Service service) throws Throwable {
        this.service = service;

        String archivo = UUID.randomUUID().toString();
        java.io.File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(archivo, ".html"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), archivo + ".html");

        Helper.notransact(em -> {

            AppConfig appconfig = AppConfig.get(em);

            Map<String, Object> data = getData();
            System.out.println("data=" + Helper.toJson(data));
            String msg = Helper.freemark(appconfig.getPurchaseOrderTemplate(), data);


            if (msg.contains("mylogosrc") && appconfig.getLogo() != null) {
                msg = msg.replaceAll("mylogosrc", appconfig.getLogo().toFileLocator().getUrl());
            }


            Files.write(msg.getBytes(), temp);

        });

        String baseUrl = System.getProperty("tmpurl");
        if (baseUrl == null) {
            baseUrl = temp.toURI().toURL().toString();
        }
        preview = new URL(baseUrl + "/" + temp.getName());

    }

    private Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();
        List<Map<String, Object>> t = new ArrayList<>();
        List<Map<String, Object>> h = new ArrayList<>();
        List<Map<String, Object>> g = new ArrayList<>();
        List<Map<String, Object>> f = new ArrayList<>();
        if (!Strings.isNullOrEmpty(getPostscript())) d.put("postscript", getPostscript());
            for (Service s : Lists.newArrayList(service)) {
                Map<String, Object> ds = s.getData();

                ds.put("po", "---");
                if (s instanceof TransferService) {
                    ds.put("orderby", ((TransferService) s).getFlightTime().format(DateTimeFormatter.ISO_DATE_TIME));
                } else {
                    ds.put("orderby", s.getStart().atStartOfDay().format(DateTimeFormatter.ISO_DATE));
                }
                if (s instanceof TransferService) t.add(ds);
                else if (s instanceof GenericService) g.add(ds);
                else if (s instanceof HotelService) h.add(ds);
                else if (s instanceof FreeTextService) f.add(ds);
            }
        Collections.sort(h, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return (o1.get("orderby") != null)?("" + o1.get("orderby")).compareTo(("" + o2.get("orderby"))):-1;
            }
        });
        if (h.size() > 0) d.put("hotels", h);
        Collections.sort(t, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return (o1.get("orderby") != null)?("" + o1.get("orderby")).compareTo(("" + o2.get("orderby"))):-1;
            }
        });
        if (t.size() > 0) d.put("transfers", t);

        Collections.sort(g, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return (o1.get("orderby") != null)?("" + o1.get("orderby")).compareTo(("" + o2.get("orderby"))):-1;
            }
        });
        if (g.size() > 0) d.put("generics", g);

        Collections.sort(f, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return (o1.get("orderby") != null)?("" + o1.get("orderby")).compareTo(("" + o2.get("orderby"))):-1;
            }
        });
        if (f.size() > 0) d.put("freetexts", f);

        return d;
    }

    @Action(icon = VaadinIcons.ENVELOPE, order = 1)
    public void send() throws Throwable {
        Helper.transact(em -> {
            em.find(Service.class, service.getId()).sendToProvider(em, provider, email, postscript);
        });
    }

    @Override
    public String toString() {
        return "Send to provider " + service.toString();
    }
}
