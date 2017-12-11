package io.mateu.erp.model.monitoring;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Entity
@Getter
@Setter
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Watchdog watchdog;

    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    private Date when;

    private boolean notified;

}
