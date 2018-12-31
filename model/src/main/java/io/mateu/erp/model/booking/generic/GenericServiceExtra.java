package io.mateu.erp.model.booking.generic;

import io.mateu.erp.model.booking.parts.GenericBookingExtra;
import io.mateu.erp.model.product.generic.Extra;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class GenericServiceExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private GenericService service;

    @ManyToOne
    @NotNull
    private Extra extra;

    private int units;


    public GenericServiceExtra() {

    }

    public GenericServiceExtra(GenericService service, GenericBookingExtra gbe) {
        this.service = service;
        extra = gbe.getExtra();
        units = gbe.getUnits();
    }
}
