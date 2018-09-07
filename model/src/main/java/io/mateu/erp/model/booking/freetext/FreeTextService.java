package io.mateu.erp.model.booking.freetext;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.annotations.Tab;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Getter@Setter
public class FreeTextService extends Service {

    @Tab("Service")
    @NotNull
    private String text;

    @NotNull
    private LocalDate deliveryDate;

    @NotNull
    private LocalDate returnDate;

    public FreeTextService() {
        setIcon(FontAwesome.EDIT.getHtml());
    }


    @PrePersist
    @PreUpdate
    public void pre(){
        setStart(getDeliveryDate());
        setFinish(getReturnDate());
    }


    @Override
    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = toMap();
            m.put("description", getText());
            m.put("start", getStart());
            m.put("finish", getFinish());

            m.put("cancelled", "" + isCancelled());
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public double rate(EntityManager em, boolean sale, Partner supplier, PrintWriter report) throws Throwable {
        return 0;
    }

    @Override
    public Partner findBestProvider(EntityManager em) throws Throwable {
        return null;
    }

    @Override
    protected String getDescription() {
        return text;
    }
}
