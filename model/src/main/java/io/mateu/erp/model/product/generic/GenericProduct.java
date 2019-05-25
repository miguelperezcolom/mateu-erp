package io.mateu.erp.model.product.generic;

import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.erp.model.product.Variant;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.model.multilanguage.Literal;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
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
    private boolean paxDependant;

    @SameLine
    private boolean infantsDependant;

    @SameLine
    private boolean childrenDependant;

    @SameLine
    private boolean juniorsDependant;

    @SameLine
    private boolean adultsDependant;

    @SameLine
    private boolean seniorsDependant;

    private int childFrom;
    @SameLine
    private int juniorFrom;

    @SameLine
    private int adultFrom;

    @SameLine
    private int seniorFrom;

    private boolean ticket;


    @PostPersist
    public void postPersist() {
        if (getVariants().size() == 0) {
            WorkflowEngine.add(() -> {

                try {
                    Helper.transact(em -> {

                        GenericProduct p = em.find(GenericProduct.class, getId());

                        if (p.getVariants().size() == 0) {
                            Variant v;
                            p.getVariants().add(v = new Variant());
                            v.setProduct(p);
                            v.setName(new Literal("Standard", "Estándar"));
                        }

                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            });
        }
    }
}
