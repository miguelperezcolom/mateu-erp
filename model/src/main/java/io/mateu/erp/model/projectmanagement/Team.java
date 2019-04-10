package io.mateu.erp.model.projectmanagement;

import io.mateu.erp.model.authentication.ERPUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    @ManyToOne
    private Department department;

    @ManyToMany
    private List<ERPUser> users = new ArrayList<>();
}
