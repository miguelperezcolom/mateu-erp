package io.mateu.erp.model.importing;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Actor;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonia on 26/03/2017.
 */
@Entity
@Getter
@Setter
@UseIdToSelect(ql="select x.id, concat(x.status, ' - ', x.agency.name, ' - ', x.id) as text from TransferImportTask x where x.id = xxxx")
public abstract class TransferImportTask {
    @Id
    @ListColumn(order = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ListColumn
    private String name;

    @Embedded
    @ListColumn(width = 300)
    @Output
    @SearchFilter(field="modified")
    private Audit audit;


    private int priority;

    public enum STATUS {PENDING,CANCELLED,OK,ERROR};
    @SearchFilter
    @ListColumn
    private STATUS status;

    private String file;

    @TextArea
    private String html;
    private byte[] email;


    @ManyToOne
    private Actor customer; //cliente de la reserva

    @ManyToOne
    private Office office;

    @ManyToOne
    private PointOfSale pointOfSale;

    @OneToMany(mappedBy = "task")
    @Ignored
    List<TransferBookingRequest> transferBookingRequests = new ArrayList<>();

    @ListColumn
    @Output
    @TextArea
    private String report;

    @ListColumn
    @Output
    private int errors=0;
    @ListColumn
    @Output
    private int additions=0;
    @ListColumn
    @Output
    private int cancellations=0;
    @ListColumn
    @Output
    private int modifications=0;
    @ListColumn
    @Output
    private int unmodified=0;

    @ListColumn
    @Output
    private int total=0;



    public void increaseAdditions()
    {
        this.additions ++;
    }
    public void increaseCancellations()
    {
        this.cancellations ++;
    }
    public void increaseModifications()
    {
        this.modifications ++;
    }
    public void increaseUnmodified()
    {
        this.unmodified ++;
    }
    public void increaseErrors()
    {
        this.errors ++;
    }
    public void increaseTotal()
    {
        this.total ++;
    }

    public abstract void execute(EntityManager em);

   /* public void cancel(User u)
    {
        this.status=STATUS.CANCELLED;
        //this.getAudit().touch(u);
    }

    public void repeat(User u)
    {
        this.status=STATUS.PENDING;
        //this.getAudit().touch(u);
    }*/


    @Action(name = "Retry")
    public static void retry(UserData _user, @Selection List<Data> _selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data x : _selection) {
                    Object id = x.get("_id");
                    TransferImportTask t = em.find(TransferImportTask.class, id);
                    t.setStatus(STATUS.PENDING);
                    t.getAudit().touch(em, _user.getLogin());
                }
            }
        });
    }

    @Action(name = "Cancel")
    public static void cancel(UserData _user, @Selection List<Data> _selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data x : _selection) {
                    Object id = x.get("_id");
                    TransferImportTask t = em.find(TransferImportTask.class, id);
                    t.setStatus(STATUS.CANCELLED);
                    t.getAudit().touch(em, _user.getLogin());
                }
            }
        });
    }

}

