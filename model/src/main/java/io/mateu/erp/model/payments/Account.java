package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import io.mateu.erp.model.partners.Agency;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @SearchFilter
    @ListColumn
    private String name;

    @ManyToOne
    @NotNull
    private Currency currency;

    private String comments;

    private String account;

    @KPI
    private double balance;

    @Ignored
    private LocalDateTime triggerUpdate;

    @Override
    public String toString() {
        return getName();
    }


    @PostPersist@PostUpdate
    public void post() {
        if (triggerUpdate != null) {
            WorkflowEngine.add(() -> {

                try {
                    Helper.transact(em -> {

                        Account b = em.merge(Account.this);
                        if (b.getTriggerUpdate() != null) {


                            Double l = (Double) em.createQuery("select sum(x.valueInNucs) from " + Payment.class.getName() + " x where x.account.id = " + b.getId()).getSingleResult();

                            b.setBalance(l);

                            b.setTriggerUpdate(null);

                        }

                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }


            });
        }
    }

}
