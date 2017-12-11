package io.mateu.erp.model.projectmanagement;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class WorkReportLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private WorkReport report;

    @ManyToOne
    @NotNull
    private Task task;

    private double hours;
}
