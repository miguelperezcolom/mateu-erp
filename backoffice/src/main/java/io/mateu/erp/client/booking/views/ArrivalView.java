package io.mateu.erp.client.booking.views;

import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import io.mateu.erp.client.operations.ServiceCalendarRow;
import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.erp.model.organization.Office;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.MainSearchFilter;
import io.mateu.mdd.core.interfaces.RpcView;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.vaadinport.vaadin.components.oldviews.CRUDViewComponent;
import io.mateu.mdd.vaadinport.vaadin.components.oldviews.EditorViewComponent;
import io.mateu.mdd.vaadinport.vaadin.components.oldviews.ExtraFilters;
import io.mateu.mdd.vaadinport.vaadin.components.oldviews.JPAListViewComponent;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class ArrivalView implements RpcView<ArrivalView, ArrivalRow> {

    @NotNull
    @MainSearchFilter
    private LocalDate from = LocalDate.now();

    @MainSearchFilter
    private Office office;

    @NotNull
    @MainSearchFilter
    private FlightDirection direction = FlightDirection.Arrival;

    @Ignored
    private transient List<ArrivalRow> l;

    @Action
    public static void changeFlight(List<ArrivalRow> selection, String FlightNumber, int flightTime) {

    }


    @Override
    public List<ArrivalRow> rpc(ArrivalView filters, int offset, int limit) throws Throwable {
        List<ArrivalRow> l = null;
        if (l == null) l = fetch();
        else l = consume();
        return l;
    }

    private List<ArrivalRow> fetch() throws Throwable {
        List<ArrivalRow> l = new ArrayList<>();
        Helper.transact(em -> {
            l.addAll(createQuery(em).getResultList());
        });
        return l;
    }

    @Override
    public int gatherCount(ArrivalView filters) throws Throwable {
        List<ArrivalRow> l = null;
        if (l == null) l = fetch();
        else l = consume();
        return l.size();
    }

    private List<ArrivalRow> consume() {
        List<ArrivalRow> aux = l;
        l = null;
        return aux;
    }

    private Query createQuery(EntityManager em) {
        Query q = em.createQuery("select new " + ServiceCalendarRow.class.getName() + "(s.start, s.office, count(s), min(s.effectiveProcessingStatus)) from " + FreeTextService.class.getName() + " s where s.start >= :d " + (office != null?" and s.office = :o ":"") + " group by s.start, s.office order by s.office.name, s.start").setParameter("d", from);
        if (office != null) q.setParameter("o", office);
        return q;
    }

    @Override
    public boolean isDoubleClickHandled() {
        return true;
    }

    @Override
    public Object onDoubleClick(String rowid) {
        try {
            Office o = Helper.find(Office.class, Long.parseLong(rowid.split("-")[0]));
            LocalDate d = LocalDate.parse(rowid.split("-")[1], DateTimeFormatter.ofPattern("yyyyMMdd"));
            return new CRUDViewComponent(new JPAListViewComponent(FreeTextService.class, new ExtraFilters("x.start = :s and x.office = :o", "s", d, "o", o)).build(), new EditorViewComponent(FreeTextService.class).build()).setTitle("Free text services for " + o.getName() + " " + d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } catch (Throwable e) {
            MDD.alert(e);
        }
        return null;
    }

    @Override
    public void decorateGrid(Grid<ArrivalRow> grid) {
        grid.getColumn("bookings").setStyleGenerator(new StyleGenerator<ArrivalRow>() {
            @Override
            public String apply(ArrivalRow s) {
                String css = "";

                if (s != null) css = s.getStyle();

                return css;
            }
        });
    }

}


