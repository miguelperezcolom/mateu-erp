package io.mateu.erp.client.operations;

import com.vaadin.ui.themes.ValoTheme;
import io.mateu.erp.model.organization.Office;
import io.mateu.mdd.core.CSS;
import io.mateu.mdd.core.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ServiceCalendarRow {

    private LocalDate date;

    private Office office;

    private long bookings;

    @Ignored
    private int minProcessingStatus;


    public ServiceCalendarRow(LocalDate date, Office office, long bookings, int minProcessingStatus) {
        this.date = date;
        this.office = office;
        this.bookings = bookings;
        this.minProcessingStatus = minProcessingStatus;
    }

    @Override
    public String toString() {
        return ((office != null)?"" + office.getId():"unkknown") + "-" +  ((date != null)?date.format(DateTimeFormatter.ofPattern("yyyyMMdd")):"unknown");
    }


    public String getStyle() {
        String css = CSS.RIGHTALIGN;

        if (getBookings() != 0) {
            if (getMinProcessingStatus() == 450) css += " " + CSS.REDBGD;
            else if (getMinProcessingStatus() < 500) css += " " + CSS.ORANGEBGD;
            else if (getMinProcessingStatus() >= 500) css += " " + CSS.GREENBGD;
        }

        return css;
    }

}
