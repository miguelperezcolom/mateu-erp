package io.mateu.erp.model.product.hotel.contracting;

import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.erp.model.product.hotel.HotelContractPhoto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class DynamicHotelContract extends AbstractContract implements IHotelContract {

    @ManyToOne
    @NotNull
    private HotelContract parent;

    private double percentOnBed;

    private double valueOnBed;

    private double percentOnMealPlan;

    private double valueOnMealPlan;

    @Convert(converter = DynamicFaresConverter.class)
    private DynamicFares fares = new DynamicFares();


    @Override
    public HotelContractPhoto getTerms() {

        // todo: aplicar porcentajes y suplementos a las condiciones del padre.

        return getParent().getTerms();
    }
}
