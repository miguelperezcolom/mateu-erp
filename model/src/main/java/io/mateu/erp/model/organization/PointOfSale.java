package io.mateu.erp.model.organization;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.product.Tariff;
import io.mateu.erp.model.tpv.TPV;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.KPI;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
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

    @ManyToOne@NotNull
    private Tariff tariff;

    @ManyToOne@NotNull
    private SalesPoint salesPoint;

    private String email;

    private int hoursForUnpaidCancellation;


    @KPI
    private double totalSales;

    @KPI
    private double cash;

    @KPI
    private LocalDate lastSettlement;

    @Ignored
    private boolean updatePending;



    @Override
    public boolean equals(Object obj) {
        return this == obj || (id != 0 && obj != null && obj instanceof PointOfSale && id == ((PointOfSale) obj).getId());
    }

    @Override
    public String toString() {
        return name;
    }


    @Action
    public PointOfSaleSettlementForm settle() {
        return new PointOfSaleSettlementForm(this);
    }


    @PostUpdate@PostPersist
    public void post() {

        if (updatePending) {

            WorkflowEngine.add(() -> {

                System.out.println("PointOfSale " + getId() + ".post().run()");
                try {
                    Helper.transact(em -> {
                        PointOfSale b = em.find(PointOfSale.class, getId());
                        if (b.isUpdatePending()) {


                            Object[] l = (Object[]) em.createQuery("select sum(x.valueInNucs),  sum(x.valueInNucs) from " + BookingCharge.class.getName() + " x where x.booking.pos.id = " + b.getId() + " and x.pointOfSaleSettlement is null").getSingleResult();

                            if (l != null) {
                                b.setTotalSales(l[0] != null?Helper.roundEuros((Double) l[0]):0);
                                b.setCash(l[1] != null?Helper.roundEuros((Double) l[1]):0);
                            } else {
                                b.setTotalSales(0);
                                b.setCash(0);
                            }

                            b.setUpdatePending(false);
                        }

                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            });

        }

    }

}
