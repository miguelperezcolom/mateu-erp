package io.mateu.erp.model.product.tour;

import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.annotations.Section;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class Tour extends AbstractProduct {

    private int childFrom;

    @SameLine
    private int juniorFrom;

    @SameLine
    private int adultFrom;

    @SameLine
    private int seniorFrom;


    @Section("Purchase")
    private boolean providerConfirmationRequired;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tour")
    @Ignored
    private List<TourCost> costs = new ArrayList<>();

    @ManyToOne
    private Provider pool;

    @Section("Operation")
    private boolean freeSale;

    @OneToMany(mappedBy = "tour")
    @Ignored
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
