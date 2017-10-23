package io.mateu.erp.model.cms;

import io.mateu.erp.model.tpv.TPV;
import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.Required;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
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
    @Required
    private String name;

    @SearchFilter
    private boolean active;

    @SearchFilter
    private String url;

    @OneToMany(mappedBy = "website")
    @Ignored
    private List<Page> pages = new ArrayList<>();

    @OneToMany
    @Ignored
    private List<Asset> assets = new ArrayList<>();

    @ManyToOne
    private TPV tpv;

    @Override
    public String toString() {
        return getName();
    }
}
