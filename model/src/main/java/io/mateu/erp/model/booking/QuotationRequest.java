package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.workflow.AbstractTask;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class QuotationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Booking booking;

    @NotNull
    private QuotationRequestDirection direction;

    @Embedded
    private Audit audit;

    @NotNull
    @ManyToOne
    private Actor actor;

    private LocalDate expiryDate;

    private double price;

    @NotNull
    private QuotationRequestAnswer answer = QuotationRequestAnswer.PENDING;

    @OneToMany
    private List<AbstractTask> tasks = new ArrayList<>();

    private LocalDateTime readTime;

    private String reader;

    private LocalDateTime answerTime;

    private String answerText;

}
