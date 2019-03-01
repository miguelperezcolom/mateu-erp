package io.mateu.erp.model.booking.transfer;

import com.vaadin.ui.Button;
import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Caption;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter@Setter@Caption("Map unmapped transfer points")
public class MapUnmappedForm {

    @Ignored
    private int pending = 0;

    @Ignored
    private int skips = 0;

    @Ignored
    private TransferPointMapping m;

    @Output
    private String text;

    public boolean isTextVisible() {
        return pending > 0;
    }

    private TransferPoint point;

    public boolean isPointVisible() {
        return pending > 0;
    }

    @Output
    private TransferBookingRequest rq;


    @Action(addAsButton = true, order = 1, callOnEnterKeyPressed = true)
    public void saveAndNext() {
        if (m != null) {
            try {
                Helper.transact(em -> {

                    m.setPoint(point);

                    em.merge(m);

                });
            } catch (Throwable throwable) {
                MDD.alert(throwable);
            }
        }
        getNext();
    }

    @Action(addAsButton = true, order = 2)
    public void jump() {
        skips++;
        getNext();
    }

    @Action(addAsButton = true, order = 3)
    public void unjump() {
        if (skips > 0) skips--;
        getNext();
    }

    @Output
    private String msg;

    public MapUnmappedForm() {

        getNext();

    }

    private void getNext() {
        try {
            Helper.notransact(em -> {

                List<TransferPointMapping> l = em.createQuery("select x from " + TransferPointMapping.class.getName() + " x where x.point is null order by x.text").getResultList();
                pending = l.size();
                if (pending <= skips) skips = pending;
                if (pending <= skips) {
                    msg = "No more pending mappings";
                    m = null;
                }
                else {
                    msg = "" + l.size() + " pending mappings";
                    m = l.get(skips);
                    setText(m.getText());
                    setPoint(null);
                    setRq(m.getCreatedBy());
                }

            });
        } catch (Throwable throwable) {
            MDD.alert(throwable);
        }
    }

}
