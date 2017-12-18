package io.mateu.erp.model.monitoring;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Getter
@Setter
public abstract class Watchdog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private boolean active;

    int priority;

    private WatchdogStatus status = WatchdogStatus.EMPTY;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRun;

    private int errors;

    private String lastError;

    private String lastStackTrace;


    @OneToMany
    private List<Watcher> notifyTo = new ArrayList<>();

    public abstract void check(EntityManager em) throws Throwable;

    public void notifyWatchers(EntityManager em, Throwable e) {
        setLastError("" + e.getClass() + ":" + e.getMessage());
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        setLastStackTrace(sw.toString());
        setStatus(WatchdogStatus.ERROR);
        setErrors(getErrors() + 1);

        if (getErrors() > 5) {

            List<Alarm> alarms = em.createQuery("select x from " + Alarm.class.getName() + " x where x.watchdog == :xx order by x.id desc").setParameter("xx", this).getResultList();

            boolean createAlarm = true;
            if (alarms.size() > 0) {
                Alarm a = alarms.get(0);
                if (a.getWhen().getTime() > new Date().getTime() - (30l * 60l * 1000l)) createAlarm = false;
            }

            if (createAlarm) {
                Alarm a = new Alarm();
                em.persist(a);
                a.setWatchdog(this);
                a.setText(getLastError());
                a.notifyWatchers(em);
            }

        }

    }
}
