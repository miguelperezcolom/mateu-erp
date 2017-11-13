package io.mateu.erp.model.cms;

import io.mateu.ui.mdd.server.annotations.SearchFilter;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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


    private String gitHubRepositoryUrl;


    private String gitHubAPIToken;
}
