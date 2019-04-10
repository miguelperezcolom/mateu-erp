package io.mateu.erp.model.payments;

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
    private List<PaymentLine> lines = new ArrayList<>();


    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<AbstractPaymentAllocation> breakdown = new ArrayList<>();


    @KPI
    private double valueInNucs;

    @KPI
    private double balance;

    @Ignored
    private boolean markedForUpdate;


    public void updateBalance() {
        double consumed = 0;
        for (AbstractPaymentAllocation a : breakdown) {
            consumed += a.getValue();
        }
        setBalance(Helper.roundEuros(valueInNucs - consumed));
    }


    @PreUpdate@PrePersist
    public void pre() throws Throwable {
        double v = 0;
        for (PaymentLine l : lines) {
            v += l.getValueInNucs();
        }

        double r = 0;
        for (AbstractPaymentAllocation a : breakdown) {
            r += a.getValue();
        }

        setBalance(Helper.roundEuros(v - r));
        setValueInNucs(Helper.roundEuros(v));
    }


    @PostPersist@PostUpdate@PostRemove
    public void post() {
        WorkflowEngine.add(() -> {
            try {

                Helper.transact(em -> {
                    Payment p = em.find(Payment.class, getId());

                    p.setMarkedForUpdate(false);

                    p.getAgent().setMarkedForUpdate(true);
                    p.getAccount().setMarkedForUpdate(true);
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

}
