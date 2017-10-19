package io.mateu.erp.server.financial;

import com.google.common.io.Files;
import io.mateu.erp.model.beroni.P1105;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.beroni.P1101;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.shared.financial.FinancialService;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import org.apache.poi.hssf.usermodel.*;

import javax.persistence.EntityManager;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by miguel on 20/5/17.
 */
public class FinancialServiceImpl implements FinancialService {
    @Override
    public void reprice(UserData user, LocalDate from, LocalDate to) throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Service> l = em.createQuery("select x from " + Service.class.getName() + " x where x.start >= :a and x.start <= :b order by x.start asc").setParameter("a", from).setParameter("b", to).getResultList();

                for (Service s : l) {
                    try {
                        s.price(em, user);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    @Override
    public URL generalReport(LocalDate from, LocalDate to) throws Throwable {

        HSSFWorkbook workbook = new HSSFWorkbook();


        Data data = fillGeneralReportData(from, to);

        System.out.println("" + data);


        fillGeneralReport(workbook, data);


        try {
            String archivo = UUID.randomUUID().toString();

            File temp = (System.getProperty("tmpdir") == null)?File.createTempFile(archivo, ".xls"):new File(new File(System.getProperty("tmpdir")), archivo + ".xls");


            System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
            System.out.println("Temp file : " + temp.getAbsolutePath());

            System.out.println(System.getProperty("java.io.tmpdir"));

            FileOutputStream fileOut = new FileOutputStream(temp);
            workbook.write(fileOut);
            fileOut.close();

            String baseUrl = System.getProperty("tmpurl");
            if (baseUrl == null) {
                return temp.toURI().toURL();
            }
            return new URL(baseUrl + "/" + temp.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public URL exportToBeroni(LocalDate from, LocalDate to) throws Throwable {

        URL[] u = new URL[1];

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                List<Service> l = em.createQuery("select x from " + Service.class.getName() + " x where x.start >= :a and x.start <= :b order by x.start asc").setParameter("a", from).setParameter("b", to).getResultList();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintWriter pw = new PrintWriter(baos);

                writeBeroniTxt(pw, em, AppConfig.get(em), l);


                String archivo = UUID.randomUUID().toString();

                File temp = (System.getProperty("tmpdir") == null)?File.createTempFile(archivo, ".xls"):new File(new File(System.getProperty("tmpdir")), archivo + ".xls");


                System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
                System.out.println("Temp file : " + temp.getAbsolutePath());

                System.out.println(System.getProperty("java.io.tmpdir"));

                Files.write(baos.toByteArray(), temp);

                String baseUrl = System.getProperty("tmpurl");
                if (baseUrl == null) {
                    u[0] = temp.toURI().toURL();
                }
                u[0] = new URL(baseUrl + "/" + temp.getName());


            }
        });

        return u[0];
    }

    private void writeBeroniTxt(PrintWriter pw, EntityManager em, AppConfig appConfig, List<Service> l) {

        for (Service s : l) if (!s.isCancelled() && !s.getBooking().getAgency().isExportableToinvoicingApp()) {
            pw.println(new P1101(em, appConfig, s));
            for (PurchaseOrder po : s.getPurchaseOrders()) if (!po.isCancelled()) {
                pw.println(new P1105(em, appConfig, s, po));
            }
        }

    }

    private void fillGeneralReport(HSSFWorkbook workbook, Data data) {

        HSSFCellStyle cellStyleDate = workbook.createCellStyle();
        cellStyleDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));

        HSSFCellStyle cellStyleDateTime = workbook.createCellStyle();
        cellStyleDateTime.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

        HSSFCellStyle cellStyleAmount = workbook.createCellStyle();
        cellStyleAmount.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));

        HSSFCellStyle cellStyleInt = workbook.createCellStyle();
        cellStyleInt.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0"));


        HSSFRow row = null;
        HSSFCell cell = null;


//        for (Data x : data){
//
//            row = sheet.createRow(numfila++);
//
//            numcol = 0;
//            for (AbstractColumn c : view.getColumns()){
//                cell = row.createCell(numcol++);
//
//                Object o = x.get(c.getId());
//
//                if (o == null) cell.setCellValue((String) null);
//                else if (o instanceof String) cell.setCellValue((String) o);
//                else if (o instanceof Double) cell.setCellValue((Double)o);
//                else if (o instanceof BigDecimal) cell.setCellValue(((BigDecimal)o).doubleValue());
//                else if (o instanceof Integer) cell.setCellValue(new Double((Integer) o));
//                else if (o instanceof BigInteger) cell.setCellValue(((BigInteger) o).doubleValue());
//                else if (o instanceof Long) cell.setCellValue(new Double((Long) o));
//                else if (o instanceof Date) {
//                    cell.setCellStyle(cellStyleDate);
//                    cell.setCellValue((Date) o);
//                } else if (o instanceof Boolean) cell.setCellValue((Boolean)o);
//                else cell.setCellValue("" + o);
//
//            }
//
//            if (numfila >= 65530) break;
//
//        }


        // summary

        HSSFSheet sheet = workbook.createSheet("Summary");

        int fila = 3;
        int col = 0;
        row = sheet.createRow(fila++);
        col = 3;
        cell = row.createCell(col++);
        cell.setCellValue("From");
        cell = row.createCell(col++);
        cell.setCellStyle(cellStyleDate);
        cell.setCellValue(new Date(data.getLocalDate("from").atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

        row = sheet.createRow(fila++);
        col = 3;
        cell = row.createCell(col++);
        cell.setCellValue("To");
        cell = row.createCell(col++);
        cell.setCellStyle(cellStyleDate);
        cell.setCellValue(new Date(data.getLocalDate("to").atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

        row = sheet.createRow(fila++);
        col = 3;
        cell = row.createCell(col++);
        cell.setCellValue("Total sale");
        cell = row.createCell(col++);
        cell.setCellStyle(cellStyleAmount);
        cell.setCellValue(data.getDouble("totalsale"));

        row = sheet.createRow(fila++);
        col = 3;
        cell = row.createCell(col++);
        cell.setCellValue("Total purchase");
        cell = row.createCell(col++);
        cell.setCellStyle(cellStyleAmount);
        cell.setCellValue(data.getDouble("totalpurchase"));

        row = sheet.createRow(fila++);
        col = 3;
        cell = row.createCell(col++);
        cell.setCellValue("Total benefit");
        cell = row.createCell(col++);
        cell.setCellStyle(cellStyleAmount);
        cell.setCellValue(data.getDouble("totalbenefit"));

        fila++;fila++;

        row = sheet.createRow(fila++);
        col = 3;
        cell = row.createCell(col++);
        cell.setCellValue("Agency");
        cell = row.createCell(col++);
        cell.setCellValue("Valued");
        cell = row.createCell(col++);
        cell.setCellValue("Not valued");
        cell = row.createCell(col++);
        cell.setCellValue("Total");

        for (boolean soloShuttle : new boolean[] {false, true}) for (Data d : (soloShuttle)?data.getList("agenciesshuttleonly"):data.getList("agencies")) {
            row = sheet.createRow(fila++);
            col = 3;
            cell = row.createCell(col++);
            cell.setCellValue(d.getString("name") + ((soloShuttle)?" SHUTTLE ONLY":""));
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleInt);
            cell.setCellValue(d.getInt("valued"));
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleInt);
            cell.setCellValue(d.getInt("notvalued"));
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleAmount);
            cell.setCellValue(d.getDouble("total"));
        }

        fila++;fila++;

        row = sheet.createRow(fila++);
        col = 3;
        cell = row.createCell(col++);
        cell.setCellValue("Provider");
        cell = row.createCell(col++);
        cell.setCellValue("Valued");
        cell = row.createCell(col++);
        cell.setCellValue("Not valued");
        cell = row.createCell(col++);
        cell.setCellValue("Total");

        for (Data d : data.getList("providers")) {
            row = sheet.createRow(fila++);
            col = 3;
            cell = row.createCell(col++);
            cell.setCellValue(d.getString("name"));
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleInt);
            cell.setCellValue(d.getInt("valued"));
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleInt);
            cell.setCellValue(d.getInt("notvalued"));
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleAmount);
            cell.setCellValue(d.getDouble("total"));
        }


        sheet.autoSizeColumn((short)3);
        sheet.autoSizeColumn((short)4);


        // sales per agency

        for (boolean soloShuttle : new boolean[] {false, true}) for (Data d : (soloShuttle)?data.getList("agenciesshuttleonly"):data.getList("agencies")) {

            sheet = workbook.createSheet(d.getString("name") + ((soloShuttle)?" SHUTTLE ONLY":""));

            fila = 3;
            col = 0;

            row = sheet.createRow(fila++);
            col = 3;
            cell = row.createCell(col++);
            cell.setCellValue("Name");
            cell = row.createCell(col++);
            cell.setCellValue(d.getString("name"));

            row = sheet.createRow(fila++);
            col = 3;
            cell = row.createCell(col++);
            cell.setCellValue("Valued");
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleInt);
            cell.setCellValue(d.getInt("valued"));

            row = sheet.createRow(fila++);
            col = 3;
            cell = row.createCell(col++);
            cell.setCellValue("Not valued");
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleInt);
            cell.setCellValue(d.getInt("notvalued"));

            row = sheet.createRow(fila++);
            col = 3;
            cell = row.createCell(col++);
            cell.setCellValue("Total");
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleAmount);
            cell.setCellValue(d.getDouble("total"));

            fila++;fila++;


            // purchase per provider

            fila++;fila++;

            row = sheet.createRow(fila++);
            col = 1;
            cell = row.createCell(col++);
            cell.setCellValue("Provider");
            cell = row.createCell(col++);
            cell.setCellValue("Valued");
            cell = row.createCell(col++);
            cell.setCellValue("Not valued");
            cell = row.createCell(col++);
            cell.setCellValue("Total");

            double totalCompra = 0;
            for (Data dx : d.getList("providers")) {
                row = sheet.createRow(fila++);
                col = 1;
                cell = row.createCell(col++);
                cell.setCellValue(dx.getString("name"));
                cell = row.createCell(col++);
                cell.setCellStyle(cellStyleInt);
                cell.setCellValue(dx.getInt("valued"));
                cell = row.createCell(col++);
                cell.setCellStyle(cellStyleInt);
                cell.setCellValue(dx.getInt("notvalued"));
                cell = row.createCell(col++);
                cell.setCellStyle(cellStyleAmount);
                cell.setCellValue(dx.getDouble("total"));
                totalCompra += dx.getDouble("total");
            }
            row = sheet.createRow(fila++);
            col = 3;
            cell = row.createCell(col++);
            cell.setCellValue("Total");
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleAmount);
            cell.setCellValue(Helper.roundEuros(totalCompra));

            fila++;fila++;

            row = sheet.createRow(fila++);
            col = 0;
            for (String h : new String[]{"Ref.", "Status", "Service", "Vehicle", "Direction", "Pickup", "Pickup resort", "Pickup time", "Dropoff", "Dropoff resort", "Flight", "Flight date", "Flight time", "Origin/destination", "Pax", "Lead name", "Agency ref.", "Valued", "Total", "Pchse. valued", "Cost", "Comments"}){
                cell = row.createCell(col++);
                cell.setCellValue(h);
            }

            for (Data s : d.getList("services")) {

                row = sheet.createRow(fila++);

                col = 0;
                for (String id : new String[] {"id", "status", "transferType", "preferredVehicle", "direction", "pickup", "pickupResort", "pickupTime", "dropoff", "dropoffResort", "flight", "flightDate", "flightTime", "flightOriginOrDestination", "pax", "leadName", "agencyReference", "valued", "total", "purchaseValued", "totalCost", "comments"}){
                    cell = row.createCell(col++);

                    Object o = s.get(id);

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
                    } else if (o instanceof Boolean) cell.setCellValue(((Boolean)o)?"YES":"NO");
                    else cell.setCellValue("" + o);

                }

                if (fila >= 65530) break;

            }





            fila++;fila++;


            row = sheet.createRow(fila++);
            col = 0;
            for (String h : new String[]{"Lead name", "Invoice date", "Invoice number", "Voucher number", "Service date", "Voucher amount (net rate)", "Tax", "Non taxable", "Voucher total"}){
                cell = row.createCell(col++);
                cell.setCellValue(h);
            }

            for (Data s : d.getList("services")) {

                row = sheet.createRow(fila++);

                col = 0;
                for (String id : new String[] {"leadName", "", "", "agencyReference", "startddmmyyyy", "base", "iva", "", "total"}){
                    cell = row.createCell(col++);

                    Object o = s.get(id);

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
                    } else if (o instanceof Boolean) cell.setCellValue(((Boolean)o)?"YES":"NO");
                    else cell.setCellValue("" + o);

                }

                if (fila >= 65530) break;

            }




            for (int i = 0; i < 30; i++) {
                sheet.autoSizeColumn((short)i);
            }

        }


        // purchase per provider

        for (Data d : data.getList("providers")) {

            sheet = workbook.createSheet(d.getString("name") + " (PROVIDER  )");

            fila = 3;
            col = 0;

            row = sheet.createRow(fila++);
            col = 3;
            cell = row.createCell(col++);
            cell.setCellValue("Name");
            cell = row.createCell(col++);
            cell.setCellValue(d.getString("name"));

            row = sheet.createRow(fila++);
            col = 3;
            cell = row.createCell(col++);
            cell.setCellValue("Valued");
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleInt);
            cell.setCellValue(d.getInt("valued"));

            row = sheet.createRow(fila++);
            col = 3;
            cell = row.createCell(col++);
            cell.setCellValue("Not valued");
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleInt);
            cell.setCellValue(d.getInt("notvalued"));

            row = sheet.createRow(fila++);
            col = 3;
            cell = row.createCell(col++);
            cell.setCellValue("Total");
            cell = row.createCell(col++);
            cell.setCellStyle(cellStyleAmount);
            cell.setCellValue(d.getDouble("total"));

            fila++;fila++;


            row = sheet.createRow(fila++);
            col = 0;
            for (String h : new String[]{"Ref.", "Status", "Sent", "Sent time", "Valued", "Total"}){
                cell = row.createCell(col++);
                cell.setCellValue(h);
            }

            for (Data s : d.getList("orders")) {

                row = sheet.createRow(fila++);

                col = 0;
                for (String id : new String[] {"id", "status", "sent", "sentTime", "valued", "total"}){
                    cell = row.createCell(col++);

                    Object o = s.get(id);

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
                    } else if (o instanceof Boolean) cell.setCellValue(((Boolean)o)?"YES":"NO");
                    else cell.setCellValue("" + o);

                }

                if (fila >= 65530) break;

            }


            for (int i = 0; i < 30; i++) {
                sheet.autoSizeColumn((short)i);
            }

        }

    }

    private Data fillGeneralReportData(LocalDate from, LocalDate to) throws Throwable {
        Data data = new Data();
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Service> l = em.createQuery("select x from " + Service.class.getName() + " x where x.start >= :a and x.start <= :b order by x.start asc").setParameter("a", from).setParameter("b", to).getResultList();


                Map<Actor, Data> dataPerAgency = new LinkedHashMap<>();
                Map<Actor, Data> dataPerAgencyOnlyShuttle = new LinkedHashMap<>();
                Map<Actor, Map<Actor, Data>> dataPerAgencyAndProvider = new LinkedHashMap<>();
                Map<Actor, Map<Actor, Data>> dataPerAgencyAndProviderOnlyShuttle = new LinkedHashMap<>();
                Map<Actor, Data> dataPerProvider = new LinkedHashMap<>();

                double totalventa = 0;
                double totalcompra = 0;

                for (Service s : l) {

                    Map<Actor, Data> dpa = dataPerAgency;
                    Map<Actor, Map<Actor, Data>> dpaap = dataPerAgencyAndProvider;

                    if (s.getBooking().getAgency().isShuttleTransfersInOwnInvoice() && s instanceof TransferService && TransferType.SHUTTLE.equals(((TransferService) s).getTransferType())) {
                        dpa = dataPerAgencyOnlyShuttle;
                        dpaap = dataPerAgencyAndProviderOnlyShuttle;
                    }

                    Data agencyData = dpa.get(s.getBooking().getAgency());
                    if (agencyData == null) {
                        dpa.put(s.getBooking().getAgency(), agencyData = new Data());
                        agencyData.set("name", s.getBooking().getAgency().getName());
                    }

                    {
                        int valoradas = agencyData.getInt("valued");
                        int novaloradas = agencyData.getInt("notvalued");
                        double total = agencyData.getDouble("total");

                        if (s.isValued()) {
                            valoradas++;
                            total += s.getTotal();
                            totalventa += s.getTotal();
                        } else {
                            novaloradas++;
                        }

                        agencyData.getList("services").add(new Data(s.getData()));

                        agencyData.set("valued", valoradas);
                        agencyData.set("notvalued", novaloradas);
                        agencyData.set("total", Helper.roundEuros(total));

                    }

                    for (PurchaseOrder po : s.getPurchaseOrders()) if (po.getProvider() != null) {

                        // para esta agencia
                        {
                            Map<Actor, Data> aux = dpaap.get(s.getBooking().getAgency());
                            if (aux == null) {
                                dpaap.put(s.getBooking().getAgency(), aux = new HashMap<Actor, Data>());
                            }
                            Data providerData = aux.get(po.getProvider());
                            if (providerData == null) {
                                aux.put(po.getProvider(), providerData = new Data());
                                providerData.set("name", po.getProvider().getName());
                            }


                            int valoradas = providerData.getInt("valued");
                            int novaloradas = providerData.getInt("notvalued");
                            double total = providerData.getDouble("total");

                            if (po.isValued()) {
                                valoradas++;
                                total += po.getTotal();
                            } else {
                                novaloradas++;
                            }

                            providerData.set("valued", valoradas);
                            providerData.set("notvalued", novaloradas);
                            providerData.set("total", Helper.roundEuros(total));

                            providerData.getList("orders").add(new Data(po.getData()));
                        }


                        // total
                        {
                            Data providerData = dataPerProvider.get(po.getProvider());
                            if (providerData == null) {
                                dataPerProvider.put(po.getProvider(), providerData = new Data());
                                providerData.set("name", po.getProvider().getName());
                            }


                            int valoradas = providerData.getInt("valued");
                            int novaloradas = providerData.getInt("notvalued");
                            double total = providerData.getDouble("total");

                            if (po.isValued()) {
                                valoradas++;
                                total += po.getTotal();
                                totalcompra += po.getTotal();
                            } else {
                                novaloradas++;
                            }

                            providerData.set("valued", valoradas);
                            providerData.set("notvalued", novaloradas);
                            providerData.set("total", Helper.roundEuros(total));

                            providerData.getList("orders").add(new Data(po.getData()));
                        }

                    }

                }


                for (Actor a : dataPerAgency.keySet()) {
                    Map<Actor, Data> aux = dataPerAgencyAndProvider.get(a);
                    if (aux != null) {
                        Data d = dataPerAgency.get(a);
                        d.getList("providers").addAll(aux.values());
                    }
                }

                for (Actor a : dataPerAgencyOnlyShuttle.keySet()) {
                    Map<Actor, Data> aux = dataPerAgencyAndProviderOnlyShuttle.get(a);
                    if (aux != null) {
                        Data d = dataPerAgencyOnlyShuttle.get(a);
                        d.getList("providers").addAll(aux.values());
                    }
                }

                data.getList("agencies").addAll(dataPerAgency.values());
                data.getList("agenciesshuttleonly").addAll(dataPerAgencyOnlyShuttle.values());
                data.getList("providers").addAll(dataPerProvider.values());

                data.set("from", from);
                data.set("to", to);
                data.set("totalsale", Helper.roundEuros(totalventa));
                data.set("totalpurchase", Helper.roundEuros(totalcompra));
                data.set("totalbenefit", Helper.roundEuros(totalventa - totalcompra));


            }
        });
        return data;
    }
}
