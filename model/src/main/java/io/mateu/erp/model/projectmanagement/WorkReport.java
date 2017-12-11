package io.mateu.erp.model.projectmanagement;

import io.mateu.erp.model.authentication.User;
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
public class WorkReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private LocalDate date;

    @ManyToOne
    @NotNull
    private User user;

    @OneToMany(mappedBy = "report")
    private List<WorkReportLine> lines = new ArrayList<>();
}
