package io.mateu.erp.model.workflow;

import com.google.common.base.Strings;
import freemarker.template.TemplateException;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.PurchaseOrderStatus;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.transfer.IslandbusHelper;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.server.Utils;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.annotations.Output;
import io.mateu.ui.mdd.server.util.Helper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.*;
import org.apache.poi.hssf.usermodel.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.activation.DataSource;
import javax.mail.internet.InternetAddress;
import javax.mail.util.*;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Actor provider;

    @Output
    private PurchaseOrderSendingMethod method;

    @Output
    private String postscript;

    public SendPurchaseOrdersTask() {

    }

    public SendPurchaseOrdersTask(List<PurchaseOrder> purchaseOrders) {
        setPurchaseOrders(purchaseOrders);
    }

    public abstract void runParticular(EntityManager em, User user) throws Throwable;

    @Override
    public void run(EntityManager em, User user) throws Throwable {
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
        for (PurchaseOrder po : getPurchaseOrders()) {
            po.setSent(true);
            if (po.getProvider().isAutomaticOrderConfirmation()) po.setStatus(PurchaseOrderStatus.CONFIRMED);
            po.setSentTime(LocalDateTime.now());
            po.afterSet(em, false);
        }
    }



    public Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();
        List<Map<String, Object>> t = new ArrayList<>();
        List<Map<String, Object>> g = new ArrayList<>();
        if (!Strings.isNullOrEmpty(getPostscript())) d.put("postscript", getPostscript());
        for (PurchaseOrder po : getPurchaseOrders()) {
            for (Service s : po.getServices()) {
                Map<String, Object> ds = s.getData();
                ds.put("po", po.getId());
                if (s instanceof TransferService) {
                    ds.put("orderby", ((TransferService) s).getFlightTime());
                } else {
                    ds.put("orderby", s.getStart().atStartOfDay());
                }
                if (s instanceof TransferService) t.add(ds);
                else if (s instanceof GenericService) g.add(ds);
            }
        }
        Collections.sort(t, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return (o1.get("orderby") != null)?((LocalDateTime)o1.get("orderby")).compareTo(((LocalDateTime)o2.get("orderby"))):-1;
            }
        });
        if (t.size() > 0) d.put("transfers", t);

        Collections.sort(g, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return (o1.get("orderby") != null)?((LocalDate)o1.get("orderby")).compareTo(((LocalDate)o2.get("orderby"))):-1;
            }
        });
        if (g.size() > 0) d.put("generics", g);

        return d;
    }
}
