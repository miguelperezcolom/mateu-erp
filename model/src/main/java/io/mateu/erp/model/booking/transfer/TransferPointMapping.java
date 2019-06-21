package io.mateu.erp.model.booking.transfer;

import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import io.mateu.mdd.core.workflow.Task;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 10/4/17.
 */
@Entity
@Getter
@Setter
public class TransferPointMapping {

    @Ignored
    static ThreadLocal<List<String>> persisted = new ThreadLocal<>();

    //select x.id, x.text, y.name from io.mateu.erp.model.file.transfer.TransferPointMapping x left outer join TransferPoint y on x.point = y order by x.text

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @MainSearchFilter
    private String text;

    @ManyToOne
    @MainSearchFilter
    private TransferPoint point;

    @Output
    @ManyToOne
    private TransferBookingRequest createdBy;


    public TransferPointMapping() {

    }

    public TransferPointMapping(String text, TransferBookingRequest transferService) {
        this.text = text;
        this.createdBy = transferService;
    }


    @Action
    public static MapUnmappedForm mapUnmapped() {
        return new MapUnmappedForm();
    }


    public static TransferPoint getTransferPoint(EntityManager em, String text, TransferBookingRequest transferService) {
        text = text.toLowerCase().trim().replaceAll("\\n", "_").replaceAll("\\r", "_");
        TransferPoint p = null;
        boolean found = false;
        for (TransferPointMapping m : (List<TransferPointMapping>) em.createQuery("select x from " + TransferPointMapping.class.getName() + " x where x.text = :t").setParameter("t", text).setFlushMode(FlushModeType.COMMIT).getResultList()) {
            p = m.getPoint();
            found = true;
            break;
        }
        if (!found) {
            List<String> l = persisted.get();
            if (l == null) {
                persisted.set(l = new ArrayList<>());
            }
            if (!l.contains(text)) {
                em.persist(new TransferPointMapping(text, transferService));
                l.add(text);
            }
        }
        return p;
    }

    @PrePersist@PreUpdate
    public void beforeSave() {
        setText(getText().toLowerCase().trim().replaceAll("\\n", "_").replaceAll("\\r", "_"));
    }


    @PostPersist@PostUpdate
    public void afterSet() throws Throwable {

        if (persisted.get() != null) persisted.get().remove(getText());

        WorkflowEngine.add(new Task() {

            long tpmId = getId();

            @Override
            public void run() {

                try {
                    Helper.transact(new JPATransaction() {
                        @Override
                        public void run(EntityManager em) throws Throwable {

                            TransferPointMapping tpm = em.find(TransferPointMapping.class, tpmId);

                            List<TransferService> ss = em.createQuery("select x from " + TransferService.class.getName() + " x where lower(x.pickupText) = :s and x.pickup is null").setFlushMode(FlushModeType.COMMIT).setParameter("s", tpm.getText()).getResultList();

                            for (TransferService s : ss) {
                                s.setPickup(tpm.getPoint());
                                if (tpm.getText().equalsIgnoreCase(s.getDropoffText()) && s.getDropoff() == null) s.setDropoff(tpm.getPoint());
                            }

                            ss = em.createQuery("select x from " + TransferService.class.getName() + " x where lower(x.dropoffText) = :s and x.dropoff is null").setFlushMode(FlushModeType.COMMIT).setParameter("s", tpm.getText()).getResultList();

                            for (TransferService s : ss) {
                                s.setDropoff(tpm.getPoint());
                                if (tpm.getText().equalsIgnoreCase(s.getPickupText()) && s.getPickup() == null) s.setPickup(tpm.getPoint());
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
