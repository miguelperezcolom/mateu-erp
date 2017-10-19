package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.annotations.CacheIndex;
import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.config.QueryHints;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class BoardType {

    @Id
    private String code;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal name;
}
