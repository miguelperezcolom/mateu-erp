package io.mateu.erp.model.booking;

import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.workflow.AbstractTask;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;

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

    @Section("Info")
    @Embedded
    @Output
    private Audit audit;

    @Output
    @ManyToOne
    private File file;

    @NotNull
    private QuotationRequestDirection direction;

    @NotNull
    @ManyToOne
    private Partner actor;

    private LocalDate expiryDate;

    @TextArea
    private String text;


    @Section("Contact")
    private String name;

    private String email;

    private String telephone;


    @Section("Answer")
    @NotNull
    private QuotationRequestAnswer answer = QuotationRequestAnswer.PENDING;

    @Ignored
    @OneToMany
    private List<AbstractTask> tasks = new ArrayList<>();

    @Output
    private LocalDateTime readTime;

    @Output
    private String reader;

    @Output
    private LocalDateTime answerTime;

    @Output
    private String answerText;

    private FastMoney answerPrice;

}
