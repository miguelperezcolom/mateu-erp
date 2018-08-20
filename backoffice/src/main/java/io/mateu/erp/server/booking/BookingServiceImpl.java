package io.mateu.erp.server.booking;

import com.google.common.base.Strings;
import io.mateu.erp.client.booking.DayServiceStatus;
import io.mateu.erp.client.booking.TransfersSummaryDay;
import io.mateu.erp.client.booking.TransfersSummaryView;
import io.mateu.erp.model.booking.transfer.Importer;
import io.mateu.erp.model.booking.transfer.TransferDirection;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.data.FileLocator;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.model.config.DummyDate;
import io.mateu.erp.model.importing.TransferImportTask;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by miguel on 23/4/17.
 */
public class BookingServiceImpl {

    public String getTransferSummaryJPQL(TransfersSummaryView parameters) {

        //INCOMING, SHUTTLE, EXECUTIVE, PRIVATE
        //INBOUND, OUTBOUND, POINTTOPOINT
        String sql = "select d.value, to_char(d.value, 'yyyy-MM-dd DY'), a.name " +


                ", sum(case when transfertype = 1 and direction = 0 then pax else 0 end)" +
                ", min(case when transfertype = 1 and direction = 0 then effectiveprocessingstatus else 1000 end)" +
                ", max(case when transfertype = 1 and direction = 0 then validationstatus else 0 end)" +

                ", sum(case when transfertype = 1 and direction = 1 then pax else 0 end)" +
                ", min(case when transfertype = 1 and direction = 1 then effectiveprocessingstatus else 1000 end)" +
                ", max(case when transfertype = 1 and direction = 1 then validationstatus else 0 end)" +

                ", sum(case when transfertype = 3 and direction = 0 then pax else 0 end)" +
                ", min(case when transfertype = 3 and direction = 0 then effectiveprocessingstatus else 1000 end)" +
                ", max(case when transfertype = 3 and direction = 0 then validationstatus else 0 end)" +

                ", sum(case when transfertype = 3 and direction = 1 then pax else 0 end)" +
                ", min(case when transfertype = 3 and direction = 1 then effectiveprocessingstatus else 1000 end)" +
                ", max(case when transfertype = 3 and direction = 1 then validationstatus else 0 end)" +

                ", sum(case when transfertype = 2 and direction = 0 then pax else 0 end)" +
                ", min(case when transfertype = 2 and direction = 0 then effectiveprocessingstatus else 1000 end)" +
                ", max(case when transfertype = 2 and direction = 0 then validationstatus else 0 end)" +

                ", sum(case when transfertype = 2 and direction = 1 then pax else 0 end)" +
                ", min(case when transfertype = 2 and direction = 1 then effectiveprocessingstatus else 1000 end)" +
                ", max(case when transfertype = 2 and direction = 1 then validationstatus else 0 end)" +

                ", sum(case when transfertype = 0 and direction = 0 then pax else 0 end)" +
                ", min(case when transfertype = 0 and direction = 0 then effectiveprocessingstatus else 1000 end)" +
                ", max(case when transfertype = 0 and direction = 0 then validationstatus else 0 end)" +

                ", sum(case when transfertype = 0 and direction = 1 then pax else 0 end)" +
                ", min(case when transfertype = 0 and direction = 1 then effectiveprocessingstatus else 1000 end)" +
                ", max(case when transfertype = 0 and direction = 1 then validationstatus else 0 end)" +

                ", sum(case when direction = 2 then pax else 0 end)" +
                ", min(case when direction = 2 then effectiveprocessingstatus else 1000 end)" +
                ", max(case when direction = 2 then validationstatus else 0 end)" +

                ", to_char(pickupTimeInformed, 'Mon-dd HH24:MI')" +

                "from dummydate d left outer join service on d.value = start left outer join transferpoint a on a.id = airport_id " +

                "where visibleinsummary ";

        if (parameters.getStart() != null) sql += " and d.value >= '" + parameters.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' ";
        if (parameters.getFinish() != null) sql += " and d.value <= '" + parameters.getFinish().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' ";

        sql += " group by 1, 2, a.name order by 1, a.name";


        return sql;
    }

    public int getTransferSummaryCount(TransfersSummaryView filters) throws Throwable {
        return Helper.sqlCount(getTransferSummaryJPQL(filters));
    }


    public List<TransfersSummaryDay> getTransferSummary(TransfersSummaryView parameters, int offset, int limit) throws Throwable {

        List<TransfersSummaryDay> list = new ArrayList<>();


        long t0 = new Date().getTime();

        int rowsPerPage = limit;
        int fromRow = offset;


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");

        String sql = getTransferSummaryJPQL(parameters);

        for (Object[] l : Helper.sqlSelectPage(sql, fromRow, rowsPerPage)) {
            TransfersSummaryDay r;
            list.add(r = new TransfersSummaryDay());
            if (l != null) {

                int col = 0;
                r.setDate((LocalDate) l[col++]);
                r.setDateText((String) l[col++]);
                r.setAirport((String) l[col++]);

                r.setShuttleIn(new DayServiceStatus(l, col));
                col += 3;
                r.setShuttleOut(new DayServiceStatus(l, col));
                col += 3;

                r.setPrivateIn(new DayServiceStatus(l, col));
                col += 3;
                r.setPrivateOut(new DayServiceStatus(l, col));
                col += 3;

                r.setExecutIn(new DayServiceStatus(l, col));
                col += 3;
                r.setExecutOut(new DayServiceStatus(l, col));
                col += 3;

                r.setIncomIn(new DayServiceStatus(l, col));
                col += 3;
                r.setIncomOut(new DayServiceStatus(l, col));
                col += 3;

                r.setPUInfd((Boolean) l[col++]);
            }

        }

        return list;
    }

    public void informPickupTime(UserData user, List<TransfersSummaryDay> selection) throws Throwable {
        for (TransfersSummaryDay s : selection) {
            LocalDate d = s.getDate();

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


    public String importPickupTimeExcel(io.mateu.mdd.core.model.common.File file) throws Throwable {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        if (file != null) throw new Throwable("You must first upload an excel file");
        else {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {
                    Object[][] l = Helper.parseExcel(new File(file.toFileLocator().getTmpPath()))[0];
                    Importer.importPickupTimes(em, l, pw);
                }
            });
        }
        return sw.toString();
    }


    public Data getAvailableHotels(Data parameters) throws Throwable {
        return new Data();
    }


    public void pickupTimeInformed(String login, long serviceId, String comments) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                TransferService s = em.find(TransferService.class, serviceId);
                s.setPickupConfirmedByTelephone(LocalDateTime.now());
                if (!Strings.isNullOrEmpty(comments)) {
                    String aux = s.getPrivateComment();
                    if (Strings.isNullOrEmpty(aux)) aux = "";
                    else aux += "\n";
                    s.setPrivateComment(aux + ">>TELEPHONE (" + login + "): " + comments);
                }
            }
        });
    }


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


    public static void main(String[] args) {

        System.setProperty("appconf", "/Users/miguel/mateu.properties");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {
                    Object[][] l = Helper.parseExcel(new File("/Users/miguel/Downloads/horas.xls"))[0];
                    Importer.importPickupTimes(em, l, pw);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        System.out.println(sw.toString());
    }

}
