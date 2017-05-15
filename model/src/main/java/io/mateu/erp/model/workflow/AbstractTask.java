package io.mateu.erp.model.workflow;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.Service;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 28/4/17.
 */
@Entity
@Getter
@Setter
@Table(name = "task")
public abstract class AbstractTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @Ignored
    private Audit audit;

    @ListColumn
    private LocalDateTime started;
    @ListColumn
    private LocalDateTime finished;
    @ListColumn
    @SearchFilter
    private TaskStatus status = TaskStatus.PENDING;
    @ListColumn
    @SearchFilter
    private TaskResult result;
    @ListColumn
    private String log;
    @ManyToMany
    @SearchFilter(value="Service Id", field = "id")
    @NotInList
    private List<Service> services = new ArrayList<>();
    @ManyToMany
    @SearchFilter(value="Booking Id", field = "id")
    @NotInList
    private List<Booking> bookings = new ArrayList<>();

    public void execute(EntityManager em, User user) {
        try {
            getAudit().touch(user);
            setStarted(LocalDateTime.now());
            setStatus(TaskStatus.RUNNING);
            em.flush();

            run(em, user);

            setStatus(TaskStatus.FINISHED);
            setResult(TaskResult.OK);
        } catch (Throwable e) {
            e.printStackTrace();
            setStatus(TaskStatus.FINISHED);
            setResult(TaskResult.ERROR);
        }
    }

    public abstract void run(EntityManager em, User user) throws Throwable;



    @Action(name = "Run")
    public static void launch(EntityManager em, UserData _user, @Selection List<Data> selection) {
        User u = em.find(User.class, _user.getLogin());
        for (Data d : selection) {
            AbstractTask t = em.find(AbstractTask.class, d.get("_id"));
            t.execute(em, u);
        }
    }

}
