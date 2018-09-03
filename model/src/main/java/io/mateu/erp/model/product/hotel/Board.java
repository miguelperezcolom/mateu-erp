package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.mdd.core.annotations.Unmodifiable;
import io.mateu.mdd.core.model.multilanguage.Literal;
import io.mateu.mdd.core.annotations.SearchFilter;
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
    private Hotel hotel;

    @SearchFilter
    @ManyToOne
    @NotNull
    private BoardType type;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal description;

    @Override
    public String getCode() {
        return getType().getCode();
    }

    @Override
    public String getName() {
        return getType().getName().getEs();
    }
}
