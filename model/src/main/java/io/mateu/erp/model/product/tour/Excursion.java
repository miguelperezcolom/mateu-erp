package io.mateu.erp.model.product.tour;


import io.mateu.mdd.core.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Excursion extends Tour {


    @NotNull
    private TourDuration duration;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tour")
    @Ignored
    private List<TourShift> shifts = new ArrayList<>();


    /**
     * si la comprobación de cupo debe ser por vehículo en lugar de por pax
     */
    private boolean salePerVehicle;

    /**
     * si es venta por vehículo
     */
    private double defaultVehicleCapacity;


}
