package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.mdd.core.annotations.NoChart;
import io.mateu.mdd.core.annotations.SearchFilter;
import io.mateu.mdd.core.annotations.Unmodifiable;
import io.mateu.mdd.core.model.multilanguage.Literal;
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
public class Board implements IBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @SearchFilter
    @ManyToOne
    @NotNull
    @Unmodifiable
    @NoChart
    private Hotel hotel;

    @SearchFilter
    @ManyToOne
    @NotNull
    @NoChart
    private BoardType type;

    @Column(name = "_order")
    private int order;

    @ManyToOne(cascade = CascadeType.ALL)
    @NoChart
    private Literal description;

    @Override
    public String getCode() {
        return getType().getCode();
    }

    @Override
    public String getName() {
        return getType().getName().getEs();
    }


    @Override
    public String toString() {
        return (getType() != null)?getType().toString():null;
    }



    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof Board && id == ((Board)obj).id);
    }

}
