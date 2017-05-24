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
public class SendPurchaseOrdersTask extends AbstractTask {

    @Column(name = "_to")
    private String to;
    private String cc;

    @ManyToOne
    private Office office;

    @ManyToOne
    private Actor provider;

    private PurchaseOrderSendingMethod method;

    private String postscript;

    public SendPurchaseOrdersTask() {

    }

    public SendPurchaseOrdersTask(List<PurchaseOrder> purchaseOrders) {
        setPurchaseOrders(purchaseOrders);
    }

    @Override
    public void run(EntityManager em, User user) throws Throwable {
        switch (getMethod()) {
            case EMAIL:

                AppConfig appconfig = AppConfig.get(em);

                // Create the attachment
//                EmailAttachment attachment = new EmailAttachment();
//                attachment.setPath("mypictures/john.jpg");
//                attachment.setDisposition(EmailAttachment.ATTACHMENT);
//                attachment.setDescription("Picture of John");
//                attachment.setName("John");

                // Create the email message
                HtmlEmail email = new HtmlEmail();
                //Email email = new HtmlEmail();
                email.setHostName((getOffice() != null)?getOffice().getEmailHost():appconfig.getAdminEmailSmtpHost());
                email.setSmtpPort((getOffice() != null)?getOffice().getEmailPort():appconfig.getAdminEmailSmtpPort());
                email.setAuthenticator(new DefaultAuthenticator((getOffice() != null)?getOffice().getEmailUsuario():appconfig.getAdminEmailUser(), (getOffice() != null)?getOffice().getEmailPassword():appconfig.getAdminEmailPassword()));
                //email.setSSLOnConnect(true);
                email.setFrom((getOffice() != null)?getOffice().getEmailFrom():appconfig.getAdminEmailFrom());
                if (!Strings.isNullOrEmpty((getOffice() != null)?getOffice().getEmailCC():appconfig.getAdminEmailCC())) email.getCcAddresses().add(new InternetAddress((getOffice() != null)?getOffice().getEmailCC():appconfig.getAdminEmailCC()));

                email.setSubject("Purchase Orders");
                email.setMsg(getMessage(appconfig));
                email.addTo(getTo());

                File attachment = createExcel();
                if (attachment != null) email.attach(attachment);

                email.attach(new ByteArrayDataSource(new XMLOutputter(Format.getPrettyFormat()).outputString(IslandbusHelper.toPrivateXml(getPurchaseOrders())).getBytes(), "text/xml"), "private.xml", "xml for privates");
                email.attach(new ByteArrayDataSource(new XMLOutputter(Format.getPrettyFormat()).outputString(IslandbusHelper.toShuttleXml(getPurchaseOrders())).getBytes(), "text/xml"), "shuttle.xml", "xml for shuttle");

                email.send();

                break;
            case XMLISLANDBUS:
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

    private File createExcel() throws IOException {
        Map<String, Object> data = getData();
        System.out.println("data=" + Helper.toJson(data));
        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
        String fileName = "purchase_orders_" + UUID.randomUUID().toString();
        java.io.File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(fileName, ".xls"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), fileName + ".xls");
        System.out.println("Temp file : " + temp.getAbsolutePath());

        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFCellStyle cellStyleDate = workbook.createCellStyle();
        cellStyleDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

        {

            HSSFSheet sheet = workbook.createSheet("Transfers");

            HSSFRow row = null;
            HSSFCell cell = null;

            //LINEA SUPERIOR
            int numfila = 0;
            int numcol = 0;

            numcol = 0;
            row = sheet.createRow(numfila++);

            for (String h : new String[]{"Ref.", "Status", "Service", "Vehicle", "Direction", "Pickup", "Pickup resort", "Pickup time", "Dropoff", "Dropoff resort", "Flight", "Flight date", "Flight time", "Origin/destination", "Pax", "Lead name", "Agency ref.", "Comments"}){
                cell = row.createCell(numcol++);
                cell.setCellValue(h);
            }

            if (data.get("transfers") != null) for (Map<String, Object> x : (List<Map<String, Object>>) data.get("transfers")){

                row = sheet.createRow(numfila++);

                numcol = 0;
                for (String id : new String[] {"po", "status", "transferType", "preferredVehicle", "direction", "pickup", "pickupResort", "pickupTime", "dropoff", "dropoffResort", "flight", "flightDate", "flightTime", "flightOriginOrDestination", "pax", "leadName", "agencyReference", "comments"}){
                    cell = row.createCell(numcol++);

                    Object o = x.get(id);

                    if (o == null) cell.setCellValue((String) null);
                    else if (o instanceof String) cell.setCellValue((String) o);
                    else if (o instanceof Double) cell.setCellValue((Double)o);
                    else if (o instanceof BigDecimal) cell.setCellValue(((BigDecimal)o).doubleValue());
                    else if (o instanceof Integer) cell.setCellValue(new Double((Integer) o));
                    else if (o instanceof BigInteger) cell.setCellValue(((BigInteger) o).doubleValue());
                    else if (o instanceof Long) cell.setCellValue(new Double((Long) o));
                    else if (o instanceof Date) {
                        cell.setCellStyle(cellStyleDate);
                        cell.setCellValue((Date) o);
                    } else if (o instanceof Boolean) cell.setCellValue((Boolean)o);
                    else cell.setCellValue("" + o);

                }

                if (numfila >= 65530) break;

            }

        }

        {

            HSSFSheet sheet = workbook.createSheet("Generic services");

            HSSFRow row = null;
            HSSFCell cell = null;

            //LINEA SUPERIOR
            int numfila = 0;
            int numcol = 0;

            numcol = 0;
            row = sheet.createRow(numfila++);

            for (String h : new String[]{"Ref.", "Status", "Units", "Start", "Finish", "Description", "Lead name", "Agency ref.", "Comments"}){
                cell = row.createCell(numcol++);
                cell.setCellValue(h);
            }

            if (data.get("generics") != null) for (Map<String, Object> x : (List<Map<String, Object>>) data.get("generics")){

                if (x.get("lines") != null) for (Map<String, Object> y : (List<Map<String, Object>>) x.get("lines")) {

                    row = sheet.createRow(numfila++);

                    numcol = 0;
                    for (String id : new String[]{"s.po", "s.status", "l.units", "l.start", "l.finish", "l.description", "s.leadName", "s.agencyReference", "s.comments"}) {
                        cell = row.createCell(numcol++);

                        Object o = null;
                        if (id.startsWith("s.")) x.get(id.substring("s.".length()));
                        else if (id.startsWith("l.")) y.get(id.substring("l.".length()));

                        if (o == null) cell.setCellValue((String) null);
                        else if (o instanceof String) cell.setCellValue((String) o);
                        else if (o instanceof Double) cell.setCellValue((Double) o);
                        else if (o instanceof BigDecimal) cell.setCellValue(((BigDecimal) o).doubleValue());
                        else if (o instanceof Integer) cell.setCellValue(new Double((Integer) o));
                        else if (o instanceof BigInteger) cell.setCellValue(((BigInteger) o).doubleValue());
                        else if (o instanceof Long) cell.setCellValue(new Double((Long) o));
                        else if (o instanceof Date) {
                            cell.setCellStyle(cellStyleDate);
                            cell.setCellValue((Date) o);
                        } else if (o instanceof Boolean) cell.setCellValue((Boolean) o);
                        else cell.setCellValue("" + o);

                    }

                    if (numfila >= 65530) break;
                } else {
                    row = sheet.createRow(numfila++);

                    numcol = 0;
                    for (String id : new String[]{"s.po", "s.status", "l.units", "l.start", "l.finish", "l.description", "s.leadName", "s.agencyReference", "s.comments"}) {
                        cell = row.createCell(numcol++);

                        Object o = null;
                        if (id.startsWith("s.")) x.get(id.substring("s.".length()));

                        if (o == null) cell.setCellValue((String) null);
                        else if (o instanceof String) cell.setCellValue((String) o);
                        else if (o instanceof Double) cell.setCellValue((Double) o);
                        else if (o instanceof BigDecimal) cell.setCellValue(((BigDecimal) o).doubleValue());
                        else if (o instanceof Integer) cell.setCellValue(new Double((Integer) o));
                        else if (o instanceof BigInteger) cell.setCellValue(((BigInteger) o).doubleValue());
                        else if (o instanceof Long) cell.setCellValue(new Double((Long) o));
                        else if (o instanceof Date) {
                            cell.setCellStyle(cellStyleDate);
                            cell.setCellValue((Date) o);
                        } else if (o instanceof Boolean) cell.setCellValue((Boolean) o);
                        else cell.setCellValue("" + o);

                    }

                    if (numfila >= 65530) break;
                }

            }

        }

        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
        System.out.println("Temp file : " + temp.getAbsolutePath());

        System.out.println(System.getProperty("java.io.tmpdir"));

        FileOutputStream fileOut = new FileOutputStream(temp);
        workbook.write(fileOut);
        fileOut.close();

        return temp;
    }

    private String getMessage(AppConfig appconfig) throws IOException, TemplateException {
        Map<String, Object> data = getData();
        System.out.println("data=" + Helper.toJson(data));
        return Helper.freemark(appconfig.getPurchaseOrderTemplate(), data);
    }

    private Map<String,Object> getData() {
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
