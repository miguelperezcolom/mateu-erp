package io.mateu.erp.model.booking.generic;

import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.booking.*;
import io.mateu.erp.model.financials.Actor;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.annotations.Badges;
import io.mateu.ui.mdd.server.annotations.OwnedList;
import io.mateu.ui.mdd.server.annotations.Required;
import io.mateu.ui.mdd.server.annotations.Subtitle;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import io.mateu.ui.mdd.server.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by miguel on 12/4/17.
 */
@Entity
@Getter
@Setter
public class GenericService extends Service implements WithTriggers {

    @Required
    private String description;

    @OneToMany(mappedBy = "service")
    @OrderBy("id asc")
    @OwnedList
    private List<PriceLine> priceLines = new ArrayList<>();

    @Override
    public void beforeSet(EntityManager em, boolean isNew) throws Exception {

    }

    @Override
    public void afterSet(EntityManager em, boolean isNew) throws Throwable {
        setProcessingStatus(ProcessingStatus.INITIAL);

        LocalDate s = null, f = null;
        for (PriceLine l : getPriceLines()) {
            if (l.getStart() != null && (s == null || l.getStart().isBefore(s))) s = l.getStart();
            if (l.getFinish() != null && (f == null || l.getFinish().isAfter(f))) f = l.getFinish();
        }
        setStart(s);
        setFinish(f);

        price(em);

        checkPurchase(em);

    }

    @Override
    public void beforeDelete(EntityManager em) throws Exception {

    }

    @Override
    public void afterDelete(EntityManager em) throws Exception {

    }

    @Override
    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = toMap();
            m.put("description", getDescription());
            List<Map<String, Object>> ls = new ArrayList<>();
            for (PriceLine l : getPriceLines()) {
                Map<String, Object> x;
                ls.add(x = new HashMap<>());
                x.put("units", l.getUnits());
                x.put("description", l.getDescription());
                x.put("start", l.getStart());
                x.put("finish", l.getFinish());
                x.put("priceperunit", l.getPricePerUnit());
                x.put("priceperunitandnight", l.getPricePerUnitAndNight());
            }

            m.put("cancelled", "" + isCancelled());
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
    @Override
    public double rate(EntityManager em, boolean sale, Actor supplier, PrintWriter report) throws Throwable {
        if (getPriceLines().size() == 0) throw new Throwable("No price lines");
        double value = 0;
        for (PriceLine l : getPriceLines()) {
            LocalDate start = getStart();
            LocalDate finish = getFinish();
            if (l.getStart() != null) start = l.getStart();
            if (l.getStart() != null) finish = l.getStart();
            long nights = DAYS.between(start, finish) - 1;
            value += l.getUnits() * l.getPricePerUnit() + l.getUnits() * nights * l.getPricePerUnitAndNight();
        }
        return value;
    }


    public Map<String,Object> getData() {
        Map<String, Object> d = super.getData();

        d.put("id", getId());
        d.put("locator", getBooking().getId());
        d.put("leadName", getBooking().getLeadName());
        d.put("agency", getBooking().getAgency().getName());
        d.put("agencyReference", getBooking().getAgencyReference());
        d.put("status", (isCancelled())?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        d.put("office", getOffice().getName());

        d.put("comments", getComment());

        List<Map<String, Object>> l = new ArrayList<>();
        for (PriceLine pl : getPriceLines()) {
            l.add(pl.getData());
        }
        d.put("lines", l);

        return d;
    }


    @Subtitle
    public String getSubitle() {
        return super.toString();
    }

}
