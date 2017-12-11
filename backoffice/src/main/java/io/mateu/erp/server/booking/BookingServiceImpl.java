package io.mateu.erp.server.booking;

import io.mateu.erp.model.booking.transfer.Importer;
import io.mateu.erp.model.booking.transfer.TransferDirection;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.DummyDate;
import io.mateu.erp.model.importing.TransferImportTask;
import io.mateu.erp.shared.booking.BookingService;
import io.mateu.ui.core.server.ServerSideHelper;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * Created by miguel on 23/4/17.
 */
public class BookingServiceImpl implements BookingService {
    @Override
    public Data getTransferSummary(Data parameters) throws Throwable {
        Data d = new Data();

        //INCOMING, SHUTTLE, EXECUTIVE, PRIVATE
        //INBOUND, OUTBOUND, POINTTOPOINT
        String sql = "select d.value, to_char(d.value, 'yyyy-MM-dd DY'), a.name " +


                ", sum(case when transfertype = 1 and direction in (0,2) then pax else 0 end)" +
                ", sum(case when transfertype = 1 and direction = 1 then pax else 0 end)" +

                ", sum(case when transfertype = 3 and direction in (0,2) then pax else 0 end)" +
                ", sum(case when transfertype = 3 and direction = 1 then pax else 0 end)" +

                ", sum(case when transfertype = 2 and direction in (0,2) then pax else 0 end)" +
                ", sum(case when transfertype = 2 and direction = 1 then pax else 0 end)" +

                ", sum(case when transfertype = 0 and direction in (0,2) then pax else 0 end)" +
                ", sum(case when transfertype = 0 and direction = 1 then pax else 0 end)" +


                ", to_char(pickupTimeInformed, 'Mon-dd HH24:MI')" +


                ", min(case when transfertype = 1 and direction in (0,2) then effectiveprocessingstatus else 1000 end)" +
                ", min(case when transfertype = 1 and direction = 1 then effectiveprocessingstatus else 1000 end)" +

                ", min(case when transfertype = 3 and direction in (0,2) then effectiveprocessingstatus else 1000 end)" +
                ", min(case when transfertype = 3 and direction = 1 then effectiveprocessingstatus else 1000 end)" +

                ", min(case when transfertype = 2 and direction in (0,2) then effectiveprocessingstatus else 1000 end)" +
                ", min(case when transfertype = 2 and direction = 1 then effectiveprocessingstatus else 1000 end)" +

                ", min(case when transfertype = 0 and direction in (0,2) then effectiveprocessingstatus else 1000 end)" +
                ", min(case when transfertype = 0 and direction = 1 then effectiveprocessingstatus else 1000 end)" +


                ", max(case when transfertype = 1 and direction in (0,2) then validationstatus else 0 end)" +
                ", max(case when transfertype = 1 and direction = 1 then validationstatus else 0 end)" +

                ", max(case when transfertype = 3 and direction in (0,2) then validationstatus else 0 end)" +
                ", max(case when transfertype = 3 and direction = 1 then validationstatus else 0 end)" +

                ", max(case when transfertype = 2 and direction in (0,2) then validationstatus else 0 end)" +
                ", max(case when transfertype = 2 and direction = 1 then validationstatus else 0 end)" +

                ", max(case when transfertype = 0 and direction in (0,2) then validationstatus else 0 end)" +
                ", max(case when transfertype = 0 and direction = 1 then validationstatus else 0 end)" +


                "from dummydate d left outer join service on d.value = start left outer join transferpoint a on a.id = airport_id " +

                "where 1 = 1 ";

        if (!parameters.isEmpty("start")) sql += " and d.value >= '" + parameters.getLocalDate("start").format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' ";
        if (!parameters.isEmpty("finish")) sql += " and d.value <= '" + parameters.getLocalDate("finish").format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' ";

        sql += " group by 1, 2, a.name order by 1, a.name";

        long t0 = new Date().getTime();

        int rowsPerPage = 3000;
        int fromRow = 0;

        d.getList("_data");

        for (Object[] l : ServerSideHelper.getServerSideApp().selectPage(sql, fromRow, rowsPerPage)) {
            Data r;
            d.getList("_data").add(r = new Data());
            if (l != null) {
                for (int i = 0; i <= 2; i++) {
                    r.set((i == 0)?"_id":"col" + i, l[i]);
                }

                for (int i = 0; i < 8; i++) {
                    Data dx = new Data();
                    dx.set("_text", "" + l[3 + i]);
                    dx.set("_status", l[3 + i + 8 + 1]);
                    String css = "";
                    Object o = l[3 + i + 8 + 1];
                    int v = 0;
                    if (o == null) v = 0;
                    else if (o instanceof Integer) v = (Integer)o;
                    else if (o instanceof Long) v = ((Long)o).intValue();

                    o = l[3 + i + 8 + 1 + 8];
                    int w = 0;
                    if (o == null) w = 0;
                    else if (o instanceof Integer) w = (Integer)o;
                    else if (o instanceof Long) w = ((Long)o).intValue();

                    if ("0".equals("" + l[3 + i])) {
                        css = null;
                    } else {
                        if (v == 450) css = "rojo";
                        else if (v < 500) css = "naranja";
                        else if (v >= 500) css = "verdemarino";

                        css += " ";
                        if (w == 0) css += "cell-valid";
                        else if (w < 2) css += "cell-warning";
                        else if (w >= 2) css += "cell-invalid";
                    }
                    dx.set("_css", css);
                    r.set("col" + (3 + i), dx);
                }
                int i = 11;
                r.set("col" + i, l[i++]);

            }

        }

        int numRows = ServerSideHelper.getServerSideApp().getNumberOfRows(sql);
        long t = new Date().getTime() - t0;
        d.set("_subtitle", "" + numRows + " records found in " + t + "ms.");
        d.set("_data_currentpageindex", fromRow / rowsPerPage);
        d.set("_data_totalrows", numRows);
        d.set("_data_pagecount", numRows / rowsPerPage + ((numRows % rowsPerPage == 0)?0:1));

        return d;
    }

    @Override
    public void informPickupTime(UserData user, List<Data> selection) throws Throwable {
        for (Data s : selection) {
            LocalDate d = s.get("_id");

            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    List<TransferService> l = em.createQuery("select x from " + TransferService.class.getName() + " x where x.start = :s and x.direction = :d order by x.flightTime asc").setParameter("s", d).setParameter("d", TransferDirection.OUTBOUND).getResultList();

                    for (TransferService s : l) {
                        try {
                            s.informPickupTime(user, em);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }


                    DummyDate dd = em.find(DummyDate.class, d);
                    dd.setPickupTimeInformed(LocalDateTime.now());

                }
            });


        }
    }

    @Override
    public String importPickupTimeExcel(Data data) throws Throwable {
        System.out.println("" + data);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        if (data.isEmpty("file")) throw new Throwable("You must first upload an excel file");
        else {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {
                    Object[][] l = Helper.parseExcel(new File(((FileLocator) data.get("file")).getTmpPath()))[0];
                    Importer.importPickupTimes(em, l, pw);
                }
            });
        }
        return sw.toString();
    }

    @Override
    public Data getAvailableHotels(Data parameters) throws Throwable {
        return new Data();
    }

    @Override
    public void retryImportationTasks(List<Data> selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data d : selection) {
                    TransferImportTask t = em.find(TransferImportTask.class, d.get("_id"));
                    t.setStatus(TransferImportTask.STATUS.PENDING);
                    t.execute(em);
                }
            }
        });
    }

    @Override
    public void cancelImportationTasks(List<Data> selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data d : selection) {
                    TransferImportTask t = em.find(TransferImportTask.class, d.get("_id"));
                    t.setStatus(TransferImportTask.STATUS.PENDING);
                    t.execute(em);
                }
            }
        });
    }
}
