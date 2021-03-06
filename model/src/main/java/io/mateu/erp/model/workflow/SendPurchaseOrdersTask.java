package io.mateu.erp.model.workflow;

import com.google.common.base.Strings;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.PurchaseOrderStatus;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Provider;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by miguel on 28/4/17.
 */
@Entity
@Getter
@Setter
public abstract class SendPurchaseOrdersTask extends AbstractTask {

    @ManyToOne
    @Output
    private Office office;

    @ManyToOne
    @Output
    private Provider provider;

    @Output
    private PurchaseOrderSendingMethod method;

    @Output
    private String postscript;

    @Ignored
    private String signature;

    public SendPurchaseOrdersTask() {

    }

    public SendPurchaseOrdersTask(List<PurchaseOrder> purchaseOrders) {
        setPurchaseOrders(purchaseOrders);
    }

    public abstract void runParticular(EntityManager em, io.mateu.mdd.core.model.authentication.User user) throws Throwable;

    @Override
    public void run(EntityManager em, io.mateu.mdd.core.model.authentication.User user) throws Throwable {
        runParticular(em, user);
        switch (getMethod()) {
            case EMAIL:
                break;
            case XMLISLANDBUS:
                break;
            case QUOONAGENT:
                break;
                default:throw new Throwable("Unknown method: " + getMethod());
        }
        setSignature(createSignature());
    }



    public Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();
        List<Map<String, Object>> t = new ArrayList<>();
        List<Map<String, Object>> h = new ArrayList<>();
        List<Map<String, Object>> g = new ArrayList<>();
        List<Map<String, Object>> f = new ArrayList<>();
        if (!Strings.isNullOrEmpty(getPostscript())) d.put("postscript", getPostscript());
        for (PurchaseOrder po : getPurchaseOrders()) {
            {
                Service s = po.getService();
                Map<String, Object> ds = s.getData();

                if (!po.isActive()) ds.put("status", "CANCELLED");

                ds.put("po", po.getId());
                if (po.isConfirmationNeeded()) {
                    String u = (MDD.getApp() != null?MDD.getApp().getBaseUrl():"");
                    if (!u.endsWith("/")) u += "/";
                    if (u.endsWith("/app/")) u = u.replaceAll("\\/app\\/", "/");
                    u +=  "poconfirmation/" + Base64.getEncoder().encodeToString(("" + getId()).getBytes());
                    ds.put("confirmationUrl", u);
                }

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

    @Override
    public void statusChanged() {
        if (!TaskResult.CANCELLED.equals(getResult())) {
            boolean mismaFirma = getSignature() != null && getSignature().equals(createSignature());
            for (PurchaseOrder po : getPurchaseOrders()) {
                if (mismaFirma) { // si la firma de los pedidos no ha cambiado podemos darlos como confirmados
                    if (TaskStatus.FINISHED.equals(getStatus()) && TaskResult.OK.equals(getResult())) {
                        if (getProvider().isAutomaticOrderConfirmation()) po.setStatus(PurchaseOrderStatus.CONFIRMED);
                        po.setSentTime(getFinished());
                        po.setSent(true);
                    }
                } else { //TODO: si la firma de los pedidos ha cambiado los ponemos como pendientes?????????? repensar!
                    po.setStatus(PurchaseOrderStatus.PENDING);
                    po.setSent(false);
                    po.setSentTime(null);
                }
            }
        }
    }


    public String createSignature() {
        String s = "";
        for (PurchaseOrder po : getPurchaseOrders()) s += po.createSignature();
        return s;
    }
}
