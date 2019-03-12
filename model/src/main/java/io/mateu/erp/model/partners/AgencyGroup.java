package io.mateu.erp.model.partners;

import io.mateu.erp.model.revenue.Markup;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class AgencyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    @ManyToOne
    private Markup markup;


    @Override
    public String toString() {
        return getName();
    }
}
