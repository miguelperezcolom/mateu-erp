package io.mateu.erp.model.product.tour;

import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.erp.model.product.DataSheet;
import io.mateu.erp.model.world.Zone;
import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class Tour extends AbstractProduct {


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tour")
    @Ignored
    private List<TourVariant> variants = new ArrayList<>();

    @NotNull
    private TourDuration duration;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tour")
    @Ignored
    private List<TourShift> shifts = new ArrayList<>();


    @Tab("Purchase")
    /**
     * no hay coste. Toda la venta es margen para nmosotros
     */
    private boolean noCost;

    /**
     * esperamos una línea de coste por cada ticket en la factura de compra.
     * Si no está marcado esperamos un coste por servicio
     */
    private boolean costPerTicket;


    private boolean providerConfirmationRequired;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tour")
    @Ignored
    private List<TourCost> costs = new ArrayList<>();


    @Tab("Operation")
    /**
     * transporte organizado por la agencia. la excursión es propia
     */
    private boolean owned;

    private boolean freeSale;

    /**
     * si la comprobación de cupo debe ser por vehículo en lugar de por pax
     */
    private boolean salePerVehicle;

    /**
     * si es venta por vehículo
     */
    private double defaultVehicleCapacity;


    @Tab("Ages")
    private int childFrom;

    private int adultFrom;



}
