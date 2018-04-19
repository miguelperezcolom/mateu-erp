package io.mateu.erp.model.booking.transfer;

import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.ERPServiceImpl;
import io.mateu.ui.mdd.server.annotations.Action;
import io.mateu.ui.mdd.server.annotations.Output;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import io.mateu.ui.mdd.server.annotations.SearchFilterIsNull;
import io.mateu.ui.mdd.server.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by miguel on 10/4/17.
 */
@Entity
@Getter
@Setter
public class TransferPointMapping {

    //select x.id, x.text, y.name from io.mateu.erp.model.booking.transfer.TransferPointMapping x left outer join TransferPoint y on x.point = y order by x.text

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @SearchFilter
    private String text;

    @ManyToOne
    @SearchFilterIsNull(value = "Unmapped")
    private TransferPoint point;

    @Output
    private TransferService createdBy;


    public TransferPointMapping() {

    }

    public TransferPointMapping(String text, TransferService transferService) {
        this.text = text;
        this.createdBy = transferService;
    }


    @Action(name = "Save and next", callOnEnterKeyPressed = true, addAsButton = true)
    public Data saveAndNext(UserData user, EntityManager em, Data _data) throws Throwable {
        ERPServiceImpl s = new ERPServiceImpl();
        s.set(user, TransferPointMapping.class.getName(), TransferPointMapping.class.getName(), _data);

        Data[] data = new Data[1];

        List<TransferPointMapping> l = em.createQuery("select x from " + TransferPointMapping.class.getName() + " x where x.point is null order by x.text").getResultList();
        if (l.size() == 0) throw new Exception("No more pending mappings");
        else {
            TransferPointMapping m = l.get(0);
            data[0] = s.get(user, TransferPointMapping.class.getName(), TransferPointMapping.class.getName(), m.getId());
        }

        return data[0];
    }

    public static TransferPoint getTransferPoint(EntityManager em, String text, TransferService transferService) {
        text = text.toLowerCase().trim();
        TransferPoint p = null;
        boolean found = false;
        for (TransferPointMapping m : (List<TransferPointMapping>) em.createQuery("select x from " + TransferPointMapping.class.getName() + " x where x.text = '" + text.replaceAll("'", "''") + "'").getResultList()) {
            p = m.getPoint();
            found = true;
            break;
        }
        if (!found) {
            em.persist(new TransferPointMapping(text, transferService));
        }
        return p;
    }


    @PostPersist@PostUpdate
    public void afterSet() throws Throwable {
        setText(getText().toLowerCase().trim());

        WorkflowEngine.add(new Runnable() {

            long tpmId = getId();

                               @Override
                               public void run() {

                                   try {
                                       Helper.transact(new JPATransaction() {
                                           @Override
                                           public void run(EntityManager em) throws Throwable {

                                               TransferPointMapping tpm = em.find(TransferPointMapping.class, tpmId);

                                               List<TransferService> ss = em.createQuery("select x from " + TransferService.class.getName() + " x where lower(x.pickupText) = :s and x.pickup is null").setParameter("s", tpm.getText()).getResultList();

                                               for (TransferService s : ss) {
                                                   s.setPickup(tpm.getPoint());
                                                   if (tpm.getText().equalsIgnoreCase(s.getDropoffText()) && s.getDropoff() == null) s.setDropoff(tpm.getPoint());
                                               }

                                               ss = em.createQuery("select x from " + TransferService.class.getName() + " x where lower(x.dropoffText) = :s and x.dropoff is null").setParameter("s", tpm.getText()).getResultList();

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
                           }
        );
    }
}
