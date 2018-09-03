package io.mateu.erp.model.product;

import io.mateu.mdd.core.model.common.Resource;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class DataSheetImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    DataSheet dataSheet;

    @ManyToOne
    Resource image;


    @Override
    public String toString() {
        return (image != null)?image.toString():"Empty image";
    }
}
