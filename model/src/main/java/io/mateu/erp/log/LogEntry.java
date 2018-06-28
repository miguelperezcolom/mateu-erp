package io.mateu.erp.log;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter@Setter
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "_when")
    private Date when = new Date();

    @Index
    private String coordinate1;

    @Index
    private String coordinate2;

    @Index
    private String coordinate3;

    @Index
    private String coordinate4;

    @Index
    private String type;

    private String text;

    public LogEntry() {

    }


    public LogEntry(String coordinate1, String coordinate2, String coordinate3, String coordinate4, String type, String text) {
        this.coordinate1 = coordinate1;
        this.coordinate2 = coordinate2;
        this.coordinate3 = coordinate3;
        this.coordinate4 = coordinate4;
        this.type = type;
        this.text = text;
    }
}
