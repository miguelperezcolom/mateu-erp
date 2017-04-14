package io.mateu.erp.model.importing;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.financials.Actor;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.Action;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Antonia on 26/03/2017.
 */
@Entity
@Getter
@Setter
public abstract class TransferImportTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @Embedded
    private Audit audit;

    private String report;
    private int priority;

    public enum STATUS {PENDING,CANCELLED,OK,ERROR};
    @SearchFilter
    private STATUS status;

    private String file;
    private String html;
    private byte[] email;

    @ManyToOne
    private Actor customer; //cliente de la reserva

    public abstract void execute(EntityManager em);

    public void cancel(User u)
    {
        this.status=STATUS.CANCELLED;
        //this.getAudit().touch(u);
    }

    public void repeat(User u)
    {
        this.status=STATUS.PENDING;
        //this.getAudit().touch(u);
    }


    @Action(name = "Run")
    public static void run(UserData _user, List<Data> _selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
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
    public static void cancel(UserData _user, List<Data> _selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
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

