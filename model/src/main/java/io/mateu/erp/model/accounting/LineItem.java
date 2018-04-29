package io.mateu.erp.model.accounting;


import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import io.mateu.ui.mdd.server.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class LineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private AccountingEntry entry;

    @ManyToOne
    private Account account;


    private int orderInsideEntry;


    private double debit;

    private double credit;

    @PreUpdate@PreRemove
    public void pre() {
        if (getAccount() != null) {
            getAccount().setCredit(getAccount().getCredit() - getCredit());
            getAccount().setDebit(getAccount().getDebit() - getDebit());
        }
    }

    @PostUpdate@PostPersist
    public void post() {

        WorkflowEngine.add(new Runnable() {

            long xid = getId();

            @Override
            public void run() {

                try {
                    Helper.transact(new JPATransaction() {
                        @Override
                        public void run(EntityManager em) throws Throwable {

                            LineItem i = em.find(LineItem.class, xid);

                            if (i.getAccount() != null) {
                                i.getAccount().setCredit(i.getAccount().getCredit() + i.getCredit());
                                i.getAccount().setDebit(i.getAccount().getDebit() + i.getDebit());
                            }

                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }


            }
        });

    }

}
