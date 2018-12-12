package io.mateu.erp.model.payments;

import io.mateu.erp.model.authentication.User;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class LitigationLogRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Litigation litigation;

    @Output
    private LocalDateTime date;

    @Output
    @ManyToOne
    private User user;

    private String text;


}
