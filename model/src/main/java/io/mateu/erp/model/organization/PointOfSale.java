package io.mateu.erp.model.organization;

import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.tpv.TPV;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.KPI;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * holder for points of sale (e.g. a website, webservices)
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter
@Setter
public class PointOfSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    @ManyToOne@NotNull
    private Office office;

    @ManyToOne
    private TPV tpv;

    @ManyToOne
    private FinancialAgent financialAgent;


    @KPI
    private double totalSales;

    @KPI
    private double cash;

    @KPI
    private LocalDate lastSettlement;


    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof PointOfSale && id == ((PointOfSale) obj).getId());
    }

    @Override
    public String toString() {
        return name;
    }


    @Action
    public PointOfSaleSettlementForm settle() {
        return new PointOfSaleSettlementForm(this);
    }


}
