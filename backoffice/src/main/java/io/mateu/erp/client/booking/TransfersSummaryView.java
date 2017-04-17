package io.mateu.erp.client.booking;

import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.OutputColumn;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractSqlListView;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 15/4/17.
 */
public class TransfersSummaryView extends AbstractSqlListView {
    @Override
    public String getSql() {
        return "select start, airport, sum(pax), sum(pax), sum(pax), sum(pax), sum(pax) from service group by start, airport order by start, airport";
    }

    @Override
    public List<AbstractColumn> createColumns() {
        List<AbstractColumn> l = new ArrayList<>();
        l.add(new OutputColumn("date", "Date", 120));
        l.add(new OutputColumn("pri_inb", "Pr.In", 120));
        l.add(new OutputColumn("pri_out", "Pr.Out", 120));
        l.add(new OutputColumn("shu_inb", "Sh.In", 120));
        l.add(new OutputColumn("shu_out", "Sh.Out", 120));
        return l;
    }

    @Override
    public String getTitle() {
        return "Transfers summary";
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this);
    }
}
