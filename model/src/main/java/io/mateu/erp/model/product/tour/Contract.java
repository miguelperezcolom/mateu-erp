package io.mateu.erp.model.product.tour;


import io.mateu.erp.model.product.AbstractContract;
import io.mateu.ui.mdd.server.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "TourContract")
@Getter@Setter
public class Contract extends AbstractContract {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contract")
    @Ignored
    private List<TourPrice> prices = new ArrayList<>();

}
