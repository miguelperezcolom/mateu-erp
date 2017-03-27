package io.mateu.erp.model.product.transfer;

import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.Required;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 25/2/17.
 */
@Entity(name = "TransferContractPrice")
@Getter
@Setter
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @Required
    @SearchFilter
    private Contract contract;

    @ManyToOne
    @Required
    private Zone origin;

    @ManyToOne
    @Required
    private Zone destination;

    @ManyToOne
    @Required
    private Vehicle vehicle;

    @Required
    private PricePer pricePer;

    @Required
    private double price;

}
