package io.mateu.erp.model.importing;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.financials.Actor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
    private STATUS status;

    private String file;
    private String html;
    private byte[] email;

    @ManyToOne
    private Actor customer; //cliente de la reserva


    public abstract String importTask();

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


}

