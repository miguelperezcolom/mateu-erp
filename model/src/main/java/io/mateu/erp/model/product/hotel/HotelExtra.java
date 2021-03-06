package io.mateu.erp.model.product.hotel;

import io.mateu.mdd.core.annotations.NoChart;
import io.mateu.mdd.core.annotations.TextArea;
import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by miguel on 31/1/17.
 */
@Entity
@Getter
@Setter
public class HotelExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne@NotNull
    @NoChart
    private Hotel hotel;

    @ManyToOne
    @NotNull
    @NoChart
    private HotelExtraType type;

    @ManyToOne
    @NoChart
    @TextArea
    private Literal description;


    private boolean optional;

    private boolean internalUseOnly;


    @Override
    public String toString() {
        return (type != null)?type.toString():"Hotel extra " + id;
    }
}
