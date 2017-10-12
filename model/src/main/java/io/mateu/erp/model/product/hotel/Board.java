package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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

    @ManyToOne
    private Hotel hotel;

    @ManyToOne
    BoardType type;

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
