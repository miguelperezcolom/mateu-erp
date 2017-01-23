package io.mateu.erp.model.cms;

import io.mateu.ui.mdd.serverside.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 21/1/17.
 */
@Entity
@Getter@Setter
public class Website {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @SearchFilter
    private String name;

    @SearchFilter
    private boolean active;

    @SearchFilter
    private String url;

    @OneToMany(mappedBy = "website")
    private List<Page> pages = new ArrayList<>();

    @OneToMany
    private List<Asset> assets = new ArrayList<>();

    @Override
    public String toString() {
        return getName();
    }
}
