package io.mateu.erp.model.product.generic;

import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.SameLine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 31/1/17.
 */
@Entity(name = "GenericProduct")
@Getter
@Setter
public class GenericProduct extends AbstractProduct {

    @OneToMany(mappedBy = "product")
    @Ignored
    private List<Extra> extras = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @Ignored
    private List<AllotmentOnGeneric> allotment = new ArrayList<>();


    private boolean dateDependant;

    @SameLine
    private boolean datesRangeDependant;

    @SameLine
    private boolean unitsDependant;

    @SameLine
    private boolean adultsDependant;

    @SameLine
    private boolean childrenDependant;

}
