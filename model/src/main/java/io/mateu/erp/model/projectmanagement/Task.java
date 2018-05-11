package io.mateu.erp.model.projectmanagement;

import io.mateu.common.model.common.File;
import io.mateu.ui.mdd.server.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "PMTask")
@Getter
@Setter
public class Task {

    //todo: linkar con github

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    private int priority;

    @NotNull
    private TaskType type;

    private String requirements;

    @OneToMany
    private List<File> attachments = new ArrayList<>();

    private String aceptanceTests;

    private int estimatedHours;

    @Output
    private int realHours;

    @NotNull
    private TaskStatus status = TaskStatus.PENDING;

    @ManyToOne
    private Task parent;

    @OneToMany(mappedBy = "parent")
    private List<Task> children = new ArrayList<>();

    @ManyToMany(mappedBy = "dependents")
    private List<Task> dependencies = new ArrayList<>();

    @ManyToMany
    private List<Task> dependents = new ArrayList<>();


    @ManyToOne
    private Project project;

    @ManyToOne
    private Epic epic;

    @ManyToOne
    private Milestone milestone;

    @ManyToOne
    private Feature feature;
}
