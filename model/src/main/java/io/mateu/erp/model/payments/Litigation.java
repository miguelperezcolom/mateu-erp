package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.File;
import io.mateu.erp.model.invoicing.Invoice;
import io.mateu.mdd.core.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Litigation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToOne
    @NotNull
    private Invoice invoice;

    @ManyToOne
    @NotNull
    private File file;

    private double amount;

    private boolean closed;

    private double taken;

    private LocalDate reminder;

    @TextArea
    private String comment;

    @OneToMany(mappedBy = "litigation")
    private List<LitigationLogRecord> log = new ArrayList<>();

}
