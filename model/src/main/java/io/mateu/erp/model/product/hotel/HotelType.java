package io.mateu.erp.model.product.hotel;

import io.mateu.mdd.core.annotations.NoChart;
import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity@Getter@Setter
public class HotelType {

    @Id@GeneratedValue
    private long id;

    @NotEmpty
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @NoChart
    private Literal nameTranslated;


    @Override
    public String toString() {
        return name;
    }
}
