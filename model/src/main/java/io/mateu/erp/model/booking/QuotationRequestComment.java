package io.mateu.erp.model.booking;

import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity@Getter@Setter
public class QuotationRequestComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne@NotNull@Output
    private QuotationRequest quotationRequest;

    @Output
    private LocalDateTime created = LocalDateTime.now();

    @Output
    private User createdBy = MDD.getCurrentUser();

    @Output
    private String comment;


}
