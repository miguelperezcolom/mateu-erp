package io.mateu.erp.model.payments;

import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.KPI;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Payment {

    @Ignored
    private transient FinancialAgent oldAgent;
    @Ignored
    private transient Account oldAccount;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Account account;

    @ManyToOne
    @NotNull
    private FinancialAgent agent;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<AbstractPaymentAllocation> breakdown = new ArrayList<>();

    @ManyToOne
    @NotNull
    private Currency currency;

    /*
    positivo es a nuestro favor (cobro)
    negativo es en nuestra contra (pago)
     */
    private double value;

    public void setValue(double value) {
        this.value = value;
        updateBalance();
    }

    @ManyToOne
    private Payment cost;

    @KPI
    private double balance;

    public void updateBalance() {
        double consumed = 0;
        for (AbstractPaymentAllocation a : breakdown) consumed += a.getValue();
        setBalance(Helper.roundEuros(value - consumed));
    }


    @PostLoad
    public void postload() {
        oldAgent = agent;
        oldAccount = account;
    }

    @PreUpdate
    public void preupdate() throws Throwable {
        Helper.transact(em ->{
            if (oldAgent != null && !oldAgent.equals(agent)) {
                oldAgent.setMarkedForUpdate(true);
                em.merge(oldAgent);

            }
            if (agent != null) {
                agent.setMarkedForUpdate(true);
                em.merge(agent);
            }

            if (oldAccount != null && !oldAccount.equals(account)) {
                oldAccount.setMarkedForUpdate(true);
                em.merge(oldAccount);
            }
            if (account != null) {
                account.setMarkedForUpdate(true);
                em.merge(account);
            }
        });
    }

    @PreRemove
    public void preremove() throws Throwable {
        Helper.transact(em -> {
            if (oldAgent != null) {
                oldAgent.setMarkedForUpdate(true);
                em.merge(oldAgent);
            }
            if (oldAccount != null) {
                oldAccount.setMarkedForUpdate(true);
                em.merge(oldAccount);
            }
        });
    }


}
