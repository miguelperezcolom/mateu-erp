package io.mateu.erp.model.product.tour;

import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.Section;
import io.mateu.mdd.core.annotations.UseLinkToListView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class Tour extends AbstractProduct {


    @Section("Purchase")
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


    @Section("Operation")
    /**
     * transporte organizado por la agencia. la excursión es propia
     */
    private boolean owned;

    private boolean freeSale;


    private int childFrom;

    private int adultFrom;


    @OneToMany(mappedBy = "tour")
    @UseLinkToListView
    private List<ManagedEvent> events = new ArrayList<>();



    @Action(order = 1)
    public URL sharedPlaning() {
        return null;
    }

    @Action(order = 2)
    public URL planing() {
        return null;
    }

    @Action(order = 3)
    public URL status() {
        return null;
    }


}
