package io.mateu.erp.model.cms;

import io.mateu.erp.model.organization.Office;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @SearchFilter
    @NotNull
    private String name;

    @SearchFilter
    private boolean active;

    @ManyToOne
    private Office office;

    private String gitHubRepositoryUrl;


    private String branch = "master";


    private String subModule;
}
