package io.mateu.erp.model.product.hotel;

import io.mateu.common.model.util.XMLSerializable;
import io.mateu.ui.mdd.server.annotations.WeekDays;
import org.jdom2.Element;

import java.time.LocalDate;

/**
 * Created by miguel on 1/10/16.
 */
public class WeekDaysRule implements XMLSerializable {

    private LocalDate start;
    private LocalDate end;

    private boolean onRequest;

    @WeekDays
    private boolean[] weekDays = {false, false, false, false, false, false, false};

    private boolean checkin = true;
    private boolean checkout;
    private boolean stay;


    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public boolean isOnRequest() {
        return onRequest;
    }

    public void setOnRequest(boolean onRequest) {
        this.onRequest = onRequest;
    }

    public boolean[] getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(boolean[] weekDays) {
        this.weekDays = weekDays;
    }

    public boolean isCheckin() {
        return checkin;
    }

    public void setCheckin(boolean checkin) {
        this.checkin = checkin;
    }

    public boolean isCheckout() {
        return checkout;
    }

    public void setCheckout(boolean checkout) {
        this.checkout = checkout;
    }

    public WeekDaysRule(Element e) {
        if (e.getAttribute("start") != null) setStart(LocalDate.parse(e.getAttributeValue("start")));
        if (e.getAttribute("end") != null) setEnd(LocalDate.parse(e.getAttributeValue("end")));
        if (e.getAttribute("weekDays") != null) {
            boolean[] a = new boolean[e.getAttributeValue("weekDays").length()];
            int pos = 0;
            for (char c : e.getAttributeValue("weekDays").toCharArray()) a[pos++] = '1' == c;
            setWeekDays(a);
        }
        if (e.getAttribute("onRequest") != null) setOnRequest(true);
        if (e.getAttribute("checkin") != null) setCheckin(true);
        if (e.getAttribute("checkout") != null) setCheckout(true);
        if (e.getAttribute("stay") != null) setStay(true);
    }

    public WeekDaysRule() {
    }

    public WeekDaysRule(LocalDate start, LocalDate end, boolean onRequest, boolean[] weekDays, boolean checkin, boolean checkout, boolean stay) {
        this.start = start;
        this.end = end;
        this.onRequest = onRequest;
        this.weekDays = weekDays;
        this.checkin = checkin;
        this.checkout = checkout;
        this.stay = stay;
    }

    @Override
    public Element toXml() {
        Element e = new Element("rule");

        if (getStart() != null) e.setAttribute("start", getStart().toString());
        if (getEnd() != null) e.setAttribute("end", getEnd().toString());
        if (getWeekDays() != null) {
            StringBuffer sb = new StringBuffer();
            for (boolean v : getWeekDays()) sb.append((v)?"1":"0");
            e.setAttribute("weekDays", sb.toString());
        }
        if (isOnRequest()) e.setAttribute("onRequest", "");
        if (isCheckin()) e.setAttribute("checkin", "");
        if (isCheckout()) e.setAttribute("checkout", "");
        if (isStay()) e.setAttribute("stay", "");

        return e;
    }

    public boolean isStay() {
        return stay;
    }

    public void setStay(boolean stay) {
        this.stay = stay;
    }
}
