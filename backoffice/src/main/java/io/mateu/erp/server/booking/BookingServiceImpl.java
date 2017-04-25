package io.mateu.erp.server.booking;

import io.mateu.erp.shared.booking.BookingService;
import io.mateu.ui.core.server.ServerSideHelper;
import io.mateu.ui.core.shared.Data;

import java.util.Date;

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



                ", min(case when transfertype = 1 and direction in (0,2) then effectiveprocessingstatus else 0 end)" +
                ", min(case when transfertype = 1 and direction = 1 then effectiveprocessingstatus else 0 end)" +

                ", min(case when transfertype = 3 and direction in (0,2) then effectiveprocessingstatus else 0 end)" +
                ", min(case when transfertype = 3 and direction = 1 then effectiveprocessingstatus else 0 end)" +

                ", min(case when transfertype = 2 and direction in (0,2) then effectiveprocessingstatus else 0 end)" +
                ", min(case when transfertype = 2 and direction = 1 then effectiveprocessingstatus else 0 end)" +

                ", min(case when transfertype = 0 and direction in (0,2) then effectiveprocessingstatus else 0 end)" +
                ", min(case when transfertype = 0 and direction = 1 then effectiveprocessingstatus else 0 end)" +



                "from dummydate d left outer join service on d.value = start left outer join transferpoint a on a.id = airport_id " +

                "where d.value >= now() and d.value < '2017-10-01' " +

                "group by 1, 2, a.name order by 1, a.name";

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
                    dx.set("_status", l[3 + i + 8]);
                    String css = "";
                    Object o = l[3 + i + 8];
                    int v = 0;
                    if (o == null) v = 0;
                    else if (o instanceof Integer) v = (Integer)o;
                    else if (o instanceof Long) v = ((Long)o).intValue();
                    if ("0".equals("" + l[3 + i])) {
                        css = "success";
                    } else {
                        if (v == 450) css = "danger";
                        else if (v < 500) css = "warning";
                        else if (v >= 500) css = "success";
                    }
                    dx.set("_css", css);
                    r.set("col" + (3 + i), dx);
                }

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
}
