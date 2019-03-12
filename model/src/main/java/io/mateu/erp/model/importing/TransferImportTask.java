package io.mateu.erp.model.importing;

import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Agency;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.model.authentication.Audit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Antonia on 26/03/2017.
 */
@Entity
@Getter
@Setter
@UseIdToSelect
public abstract class TransferImportTask {
    @Id
    @ListColumn
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ListColumn@MainSearchFilter
    @Output
    private String name;

    @Embedded
    @ListColumn
    @Output
    @SearchFilter(field="modified")
    private Audit audit;


    @Output
    private int priority;

    public enum STATUS {PENDING,CANCELLED,OK,ERROR};
    @SearchFilter
    @ListColumn
    @Output
    private STATUS status;

    @Output
    private String file;

    @Output
    private String html;
    @Output
    private byte[] email;


    @ManyToOne
    private Agency customer; //cliente de la reserva

    @ManyToOne
    private Office office;

    @ManyToOne
    private PointOfSale pointOfSale;

    @ManyToOne
    private BillingConcept billingConcept;

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


    @Action
    public static void retry(EntityManager em, UserData _user, Set<TransferImportTask> _selection) throws Throwable {
        for (TransferImportTask t : _selection) {
            t.setStatus(STATUS.PENDING);
            t.getAudit().touch(em, _user.getLogin());
            t.execute(em);
        }
    }

    @Action
    public static void cancel(EntityManager em, UserData _user, Set<TransferImportTask> _selection) throws Throwable {
        for (TransferImportTask t : _selection) {
            t.setStatus(STATUS.CANCELLED);
            t.getAudit().touch(em, _user.getLogin());
        }
    }


    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof TransferImportTask && id == ((TransferImportTask) obj).getId());
    }

    @Override
    public String toString() {
        return name != null?name:"" + getClass().getSimpleName() + " " + id;
    }
}

