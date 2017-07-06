package io.mateu.erp.model.workflow;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.Service;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @NotInEditor
    private List<Service> services = new ArrayList<>();
    @ManyToMany
    @SearchFilter(value="Booking Id", field = "id")
    @NotInEditor
    private List<Booking> bookings = new ArrayList<>();

    @ManyToMany(mappedBy = "sendingTasks")
    @SearchFilter(value="Purchase Order Id", field = "id")
    @NotInEditor
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();


    @Links
    public List<MDDLink> getLinks() {
        List<MDDLink> l = new ArrayList<>();
        l.add(new MDDLink("Purchase Orders", PurchaseOrder.class, ActionType.OPENLIST, new Data("sendingTasks.id", getId())));
        return l;
    }

    public void execute(EntityManager em, User user) {
        try {
            getAudit().touch(user);
            setStarted(LocalDateTime.now());
            setStatus(TaskStatus.RUNNING);
            em.flush();

            run(em, user);

            setStatus(TaskStatus.FINISHED);
            setResult(TaskResult.OK);
            setLog("done " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } catch (Throwable e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            setLog(sw.toString() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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
