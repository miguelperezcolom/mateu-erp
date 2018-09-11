package io.mateu.erp.client.operations;

import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.interfaces.RpcView;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.vaadinport.vaadin.components.oldviews.CRUDViewComponent;
import io.mateu.mdd.vaadinport.vaadin.components.oldviews.EditorViewComponent;
import io.mateu.mdd.vaadinport.vaadin.components.oldviews.ExtraFilters;
import io.mateu.mdd.vaadinport.vaadin.components.oldviews.JPAListViewComponent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FreeTextServiceCalendar implements RpcView<FreeTextServiceCalendar, FreeTextCalendarRow> {


    @Ignored
    private transient List<FreeTextCalendarRow> l;


    @Override
    public List<FreeTextCalendarRow> rpc(FreeTextServiceCalendar filters, int offset, int limit) throws Throwable {
        List<FreeTextCalendarRow> l = null;
        if (l == null) l = fetch();
        else l = consume();
        return l;
    }

    private List<FreeTextCalendarRow> fetch() throws Throwable {
        List<FreeTextCalendarRow> l = new ArrayList<>();
        Helper.transact(em -> {
            l.addAll(em.createQuery(getSql()).getResultList());
        });
        return l;
    }

    @Override
    public int gatherCount(FreeTextServiceCalendar filters) throws Throwable {
        List<FreeTextCalendarRow> l = null;
        if (l == null) l = fetch();
        else l = consume();
        return l.size();
    }

    private List<FreeTextCalendarRow> consume() {
        List<FreeTextCalendarRow> aux = l;
        l = null;
        return aux;
    }

    private String getSql() {
        return "select new " + FreeTextCalendarRow.class.getName() + "(s.start, count(s)) from " + FreeTextService.class.getName() + " s group by s.start order by s.start";
    }

    @Override
    public boolean isDoubleClickHandled() {
        return true;
    }

    @Override
    public Object onDoubleClick(String rowid) {
        try {
            return new CRUDViewComponent(new JPAListViewComponent(FreeTextService.class, new ExtraFilters("x.start = :s", "s", LocalDate.parse(rowid, DateTimeFormatter.ofPattern("yyyyMMdd")))).build(), new EditorViewComponent(FreeTextService.class).build());
        } catch (Exception e) {
            MDD.alert(e);
        }
        return null;
    }
}


