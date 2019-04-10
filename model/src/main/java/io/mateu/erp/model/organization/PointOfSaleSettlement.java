package io.mateu.erp.model.organization;

import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.commissions.CommissionSettlement;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.payments.*;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class PointOfSaleSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Output
    private LocalDateTime created = LocalDateTime.now();

    @Output@ManyToOne@NotNull
    private User createdBy;

    @Output@ManyToOne@NotNull
    private PointOfSale pointOfSale;

    @OneToMany(mappedBy = "pointOfSaleSettlement")@UseLinkToListView
    private List<Booking> bookings = new ArrayList<>();

    @KPI
    private double totalSale;

    @KPI
    private double totalCash;

    @KPI
    private double totalCommissions;

    @KPI
    private boolean commissionsDiscounted;

    @KPI
    private double totalPaid;

    @OneToMany(mappedBy = "pointOfSaleSettlement")
    @OrderColumn(name = "id")
    @UseLinkToListView
    private List<PointOfSaleSettlementPaymentAllocation> payments = new ArrayList<>();


    @ManyToOne
    private CommissionSettlement commissionSettlement;


    @Ignored
    private boolean updatePending;



    @Action(order = 5, icon = VaadinIcons.EURO, saveBefore = true, saveAfter = true)
    @NotWhenCreating
    public void enterPayment(EntityManager em, @NotNull Account account, @NotNull MethodOfPayment methodOfPayment, @NotNull Currency currency, double amount) throws Throwable {
        if (pointOfSale.getFinancialAgent() == null) throw  new Exception("Missing financial agent for point of sale " + pointOfSale.getName() + ". Please fill");
        if (amount != 0) {
            Payment p = new Payment();
            p.setAccount(account);
            p.setDate(LocalDate.now());
            p.setAgent(pointOfSale.getFinancialAgent());

            PaymentLine l;
            p.getLines().add(l = new PaymentLine());
            l.setPayment(p);
            l.setMethodOfPayment(methodOfPayment);
            l.setCurrency(currency);
            l.setValue(amount);


            PointOfSaleSettlementPaymentAllocation a;
            p.getBreakdown().add(a = new PointOfSaleSettlementPaymentAllocation());
            a.setPayment(p);
            a.setPointOfSaleSettlement(this);
            getPayments().add(a);
            a.setValue(amount);

            em.persist(p);

        }
    }


    @PostUpdate@PostPersist
    public void post() {
        if (updatePending) WorkflowEngine.add(() -> {

            try {
                Helper.transact(em -> {

                    PointOfSaleSettlement s = em.find(PointOfSaleSettlement.class, getId());

                    double totalPagado = 0;
                    for (PointOfSaleSettlementPaymentAllocation pa : getPayments()) {
                        totalPagado += pa.getValue();
                    }
                    s.setTotalPaid(Helper.roundEuros(totalPagado));

                    s.setUpdatePending(false);

                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        });
    }

}
