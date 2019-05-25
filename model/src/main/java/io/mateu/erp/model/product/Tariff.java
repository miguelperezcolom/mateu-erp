package io.mateu.erp.model.product;

import io.mateu.mdd.core.annotations.TextArea;
import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity@Getter@Setter
public class Tariff {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal nameToShow;

    @ManyToOne(cascade = CascadeType.ALL)
    @TextArea
    private Literal terms;

    @Column(name = "_order")
    private int order;

    private boolean availableOnline;

    @Override
    public boolean equals(Object obj) {
        return this == obj || (id != 0 && obj != null && obj instanceof Tariff && id == ((Tariff) obj).getId());
    }

    @Override
    public String toString() {
        return name;
    }
}
