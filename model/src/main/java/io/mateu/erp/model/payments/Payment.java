package io.mateu.erp.model.payments;

import com.google.common.collect.ImmutableList;
import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.mdd.core.annotations.*;
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
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull@NotWhenEditing
    private Account account;

    @ManyToOne
    @NotNull@NotWhenEditing
    private FinancialAgent agent;

    @NotNull@NotWhenEditing
    private LocalDate date = LocalDate.now();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "payment")
    @UseLinkToListView
    private List<PaymentLine> lines = ImmutableList.of();

    public List<PaymentLine> getLines() {
        return ImmutableList.copyOf(lines);
    }

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    @UseLinkToListView
    private List<AbstractPaymentAllocation> breakdown = ImmutableList.of();

    public List<AbstractPaymentAllocation> getBreakdown() {
        return ImmutableList.copyOf(breakdown);
    }

    @KPI@Money
    private double valueInNucs;

    @KPI@Money
    private double balance;

    @Ignored
    private LocalDateTime triggerUpdate;





    public void updateBalance() {
        double v = 0;
        for (PaymentLine l : lines) {
            v += l.getValueInNucs();
        }

        double r = 0;
        for (AbstractPaymentAllocation a : breakdown) {
            if (!(a instanceof BookingPaymentAllocation) || ((BookingPaymentAllocation) a).getInvoice() == null) r += a.getValue();
        }

        setBalance(Helper.roundEuros(v - r));
        setValueInNucs(Helper.roundEuros(v));
    }


    @PreUpdate@PrePersist
    public void pre() throws Throwable {
        updateBalance();
    }


    @PostPersist@PostUpdate@PostRemove
    public void post() {
        WorkflowEngine.add(() -> {
            try {

                Helper.transact(em -> {
                    Payment p = em.find(Payment.class, getId());

                    p.getAgent().setTriggerUpdate(LocalDateTime.now());
                    p.getAccount().setTriggerUpdate(LocalDateTime.now());
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }


    @Action(icon = VaadinIcons.ENVELOPE)
    public void sendPaymentLetter() {
        //todo: pendiente
    }

    @Override
    public String toString() {
        return "Payment " + id;
    }
}
