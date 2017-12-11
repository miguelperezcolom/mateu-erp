package io.mateu.erp.model.projectmanagement;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.common.File;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date when;

    @ManyToOne
    @NotNull
    private User user;

    private String text;

    @OneToMany
    private List<File> attachments = new ArrayList<>();

    private TaskStatus newTaskStatus;

    private CommentType type;
}
