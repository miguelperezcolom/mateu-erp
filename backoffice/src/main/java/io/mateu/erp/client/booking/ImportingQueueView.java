package io.mateu.erp.client.booking;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.components.fields.ComboBoxField;
import io.mateu.ui.core.client.components.fields.DateTimeField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractSqlListView;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.mdd.client.AbstractJPAListView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Antonia on 02/04/2017.
 */
public class ImportingQueueView extends AbstractJPAListView {

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> as = super.createActions();

        as.add(new AbstractAction("New Task") {
            @Override
            public void run() {
                System.out.println("hola");
            }
        });

        return as;
    }

    @Override
    public String getSql() {
         String sql = "select x.id, x.status, x.audit.created, x.audit.modified, x.name, x.report, x.priority" +
                " from TransferImportTask x  where 1=1";

         if (getForm().getData().get("status")!=null)
            sql +=   " and x.status = '" + getForm().getData().getData("status").get("value") + "'" ;
        if (getForm().getData().getString("creatFrom")!=null)
            sql +=   " and  x.audit.created >= '" + getForm().getData().getDate("creatFrom") + "'" ;
        if (getForm().getData().getString("creatTo")!=null)
            sql +=   " and  x.audit.created <= '" + getForm().getData().getDate("creatTo") + "'" ;

        if (getForm().getData().getString("modifFrom")!=null)
            sql +=   " and  x.audit.modified >= '" + getForm().getData().getDate("modifFrom") + "'" ;
        if (getForm().getData().getString("modifTo")!=null)
            sql +=   " and  x.audit.modified <= '" + getForm().getData().getDate("modifTo") + "'" ;

        if (getForm().getData().get("type")!=null)
            sql +=   " and x.getClass = '" + getForm().getData().getString("type") + "'" ;

        sql +=    " order by x.audit.modified ";

         return sql;
    }

    @Override
    public List<AbstractColumn> createColumns() {
        return Arrays.asList(
                new TextColumn("_id", "Task Id", 60, false),
                new TextColumn("col1", "Status", 60, false),
                new TextColumn("col2", "Created", 120, false),
                new TextColumn("col3", "Modified", 120, false),
                new TextColumn("col4", "Name", 200, false),
                new TextColumn("col5", "Report", 500, false),
                new TextColumn("col6", "Priority", 60, false)

        );
    }

    @Override
    public String getTitle() {
        return "Importing Queue";
    }

    @Override
    public AbstractForm createForm() {

        return new ViewForm(this)
                .add(new ComboBoxField("status", "Status", Arrays.asList(
                        new Pair("io.mateu.erp.model.importing.TransferImportTask.STATUS.PENDING", "Pending")
                        , new Pair("io.mateu.erp.model.importing.TransferImportTask.STATUS.ERROR", "Error")
                        , new Pair("io.mateu.erp.model.importing.TransferImportTask.STATUS.OK", "Ok")
                        , new Pair("io.mateu.erp.model.importing.TransferImportTask.STATUS.CANCELLED", "Cancelled")

                )))
                .add(new DateTimeField("creatFrom", "Created From"))
                .add(new DateTimeField("creatTo", "Created To"))
                .add(new DateTimeField("modifFrom", "Modified From"))
                .add(new DateTimeField("modifTo", "Modified To"))
                .add(new ComboBoxField("type", "Type", Arrays.asList(
                        new Pair("ShuttleDirectImportTask", "ShuttleDirect")
                        , new Pair("IbizaRocksImportTask", "IbizaRocks")
 //add other implementations of TransferImportTask....
                )));
    }
}
