package io.mateu.erp.model.booking.freetext;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.ServiceType;
import io.mateu.erp.model.booking.parts.FreeTextBooking;
import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.annotations.*;
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
@NewNotAllowed
@Indelible
public class FreeTextService extends Service {

    @NotNull
    @ListColumn
    @Position(5)
    @Output
    private String text;

    @NotNull
    @Position(6)
    @Output
    private LocalDate deliveryDate;

    @NotNull
    @Position(7)
    @Output
    private LocalDate returnDate;

    public FreeTextService() {
        setServiceType(ServiceType.FREETEXT);
        setIcons(FontAwesome.PENCIL.getHtml());
    }


    @PrePersist
    @PreUpdate
    public void pre(){
        setStart(getDeliveryDate());
        setFinish(getReturnDate());
        setAvailable(true);
        super.pre();
    }


    @Override
    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = toMap();
            m.put("leadName", getBooking().getLeadName());
            m.put("text", getText());
            m.put("start", getStart());
            m.put("finish", getFinish());

            m.put("cancelled", "" + !isActive());
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public double rate(EntityManager em, boolean sale, Partner supplier, PrintWriter report) throws Throwable {
        return getBooking() instanceof FreeTextBooking && ((FreeTextBooking)getBooking()).getOverridedCost() != null?((FreeTextBooking)getBooking()).getOverridedCost().getNumber().doubleValue():0;
    }

    @Override
    public Partner findBestProvider(EntityManager em) throws Throwable {
        return getBooking() instanceof FreeTextBooking?((FreeTextBooking)getBooking()).getProvider():null;
    }

    @Override
    protected String getDescription() {
        return text;
    }
}
