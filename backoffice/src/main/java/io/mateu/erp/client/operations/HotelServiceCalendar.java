package io.mateu.erp.client.operations;

import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.interfaces.RpcCrudView;
import io.mateu.mdd.core.interfaces.RpcView;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.vaadinport.vaadin.MDDUI;
import io.mateu.mdd.vaadinport.vaadin.components.oldviews.ExtraFilters;
import io.mateu.mdd.vaadinport.vaadin.components.oldviews.JPAListViewComponent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HotelServiceCalendar implements RpcView<HotelServiceCalendar, HotelCalendarRow> {


    @Ignored
    private transient int count;


    @Override
    public List<HotelCalendarRow> rpc(HotelServiceCalendar filters, int offset, int limit) throws Throwable {
        List<HotelCalendarRow> l = new ArrayList<>();
        Helper.transact(em -> {
            l.addAll(em.createQuery(getSql()).getResultList());
        });
        count = l.size();
        return l;
    }

    @Override
    public int gatherCount(HotelServiceCalendar filters) throws Throwable {
        return count;
    }

    private String getSql() {
        return "select new " + HotelCalendarRow.class.getName() + "(s.start, count(s)) from " + HotelService.class.getName() + " s group by s.start order by s.start";
    }

    @Override
    public Object onDoubleClick(String rowid) {
        return new JPAListViewComponent(HotelService.class, new ExtraFilters("x.start = :s", LocalDate.parse(rowid, DateTimeFormatter.ofPattern("yyyyMMdd"))));
    }
}


