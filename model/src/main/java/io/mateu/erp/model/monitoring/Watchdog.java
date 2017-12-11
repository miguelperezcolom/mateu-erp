package io.mateu.erp.model.monitoring;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    private WatchdogStatus status = WatchdogStatus.EMPTY;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRun;

    private int errors;

    @OneToMany
    private List<Watcher> notifyTo = new ArrayList<>();

    public abstract void check(EntityManager em) throws Throwable;
}
