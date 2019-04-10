package io.mateu.erp.model.booking.freetext;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.ServiceType;
import io.mateu.erp.model.booking.parts.FreeTextBooking;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.partners.Provider;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            m.put("opsComment", "" + getOperationsComment());
            m.put("comment", "" + getBooking().getSpecialRequests());
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public double rateSale(EntityManager em, PrintWriter report) throws Throwable {
        return getBooking() instanceof FreeTextBooking?((FreeTextBooking)getBooking()).getOverridedValue():0;
    }

    @Override
    public double rateCost(EntityManager em, Provider supplier, PrintWriter report) throws Throwable {
        return getBooking() instanceof FreeTextBooking?((FreeTextBooking)getBooking()).getOverridedCost():0;
    }

    @Override
    public Provider findBestProvider(EntityManager em) throws Throwable {
        return getBooking() instanceof FreeTextBooking?((FreeTextBooking)getBooking()).getProvider():null;
    }

    @Override
    protected String getDescription() {
        return text;
    }

    @Override
    public BillingConcept getBillingConcept(EntityManager em) {
        return AppConfig.get(em).getBillingConceptForOthers();
    }


    @Override
    public Element toXml() {
        Element xml = super.toXml();

        xml.setAttribute("type", "freetext");

        xml.setAttribute("header", "Free text service");

        if (getDescription() != null) xml.setAttribute("description", getDescription());

        return xml;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> d = super.getData();

        d.put("text", getText());

        return d;
    }
}
