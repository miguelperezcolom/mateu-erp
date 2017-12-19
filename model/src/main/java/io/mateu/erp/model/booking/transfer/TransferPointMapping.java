package io.mateu.erp.model.booking.transfer;

import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.ERPServiceImpl;
import io.mateu.ui.mdd.server.annotations.Action;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import io.mateu.ui.mdd.server.annotations.SearchFilterIsNull;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
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
public class TransferPointMapping implements WithTriggers {

    //select x.id, x.text, y.name from io.mateu.erp.model.booking.transfer.TransferPointMapping x left outer join TransferPoint y on x.point = y order by x.text

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @SearchFilter
    private String text;

    @ManyToOne
    @SearchFilter
    @SearchFilterIsNull(value = "Unmapped")
    private TransferPoint point;


    public TransferPointMapping() {

    }

    public TransferPointMapping(String text) {
        this.text = text;
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

    public static TransferPoint getTransferPoint(EntityManager em, String text) {
        text = text.toLowerCase().trim();
        TransferPoint p = null;
        boolean found = false;
        for (TransferPointMapping m : (List<TransferPointMapping>) em.createQuery("select x from " + TransferPointMapping.class.getName() + " x where x.text = '" + text.replaceAll("'", "''") + "'").getResultList()) {
            p = m.getPoint();
            found = true;
            break;
        }
        if (!found) {
            em.persist(new TransferPointMapping(text));
        }
        return p;
    }

    @Override
    public void beforeSet(EntityManager em, boolean isNew) throws Exception {

    }

    @Override
    public void afterSet(EntityManager em, boolean isNew) throws Exception {
        setText(getText().toLowerCase().trim());
    }

    @Override
    public void beforeDelete(EntityManager em) throws Exception {

    }

    @Override
    public void afterDelete(EntityManager em) throws Exception {

    }
}
