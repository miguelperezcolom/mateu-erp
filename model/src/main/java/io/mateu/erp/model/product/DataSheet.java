package io.mateu.erp.model.product;

import com.vaadin.ui.Component;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Html;
import io.mateu.mdd.core.annotations.TextArea;
import io.mateu.mdd.core.model.common.Resource;
import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class DataSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull
    @TextArea
    private Literal description;

    @ManyToOne
    private Resource mainImage;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataSheet", orphanRemoval = true)
    private List<DataSheetImage> images = new ArrayList<>();


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataSheet", orphanRemoval = true)
    private List<FeatureValue> features = new ArrayList<>();

    @Override
    public String toString() {
        return getName();
    }


    @Action
    public Component preview() {
        return new DataSheetComponent(this);
    }
}
