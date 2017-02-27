package io.mateu.erp.model.cms;

import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 21/1/17.
 */
@Entity
@Getter
@Setter
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Page parent;

    @ManyToOne
    private Website website;

    private String name;

    private boolean active;

    @TextArea
    private String freemarker;

    @OneToMany(mappedBy = "parent")
    @Ignored
    private List<Page> pages = new ArrayList<>();


    @Override
    public String toString() {
        return getName();
    }
}
