package io.mateu.erp.model.commissions;

import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.organization.PointOfSaleSettlement;
import io.mateu.erp.model.partners.CommissionAgent;
import io.mateu.erp.model.payments.*;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity@Getter@Setter@NewNotAllowed@Indelible
public class CommissionSettlement {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded@Output
    private Audit audit;

    @ManyToOne@NotNull
    private CommissionAgent agent;

    @Output
    private double totalSale;

    @Output
    private double totalCommission;

    @OneToMany(mappedBy = "commissionSettlement")@UseLinkToListView
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "commissionSettlement")
    @OrderColumn(name = "id")
    @UseLinkToListView
    private List<CommissionSettlementPaymentAllocation> payments = new ArrayList<>();

    @Ignored
    private boolean updatePending;

    @Action(order = 5, icon = VaadinIcons.EURO, saveBefore = true, saveAfter = true)
    @NotWhenCreating
    public void enterPayment(EntityManager em, @NotNull Account account, @NotNull MethodOfPayment methodOfPayment, @NotNull Currency currency, double amount) throws Throwable {
        if (agent.getFinancialAgent() == null) throw  new Exception("Missing financial agent for rep " + agent.getName() + ". Please fill");
        if (amount != 0) {
            Payment p = new Payment();
            p.setAccount(account);
            p.setDate(LocalDate.now());
            p.setAgent(agent.getFinancialAgent());

            PaymentLine l;
            p.getLines().add(l = new PaymentLine());
            l.setPayment(p);
            l.setMethodOfPayment(methodOfPayment);
            l.setCurrency(currency);
            l.setValue(amount);


            CommissionSettlementPaymentAllocation a;
            p.getBreakdown().add(a = new CommissionSettlementPaymentAllocation());
            a.setPayment(p);
            a.setCommissionSettlement(this);
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
                    for (CommissionSettlementPaymentAllocation pa : getPayments()) {
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


