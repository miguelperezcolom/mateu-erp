package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.Occupancy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter@Setter
public class DispoRQ {

    private int checkIn;
    private int checkOut;
    private List<? extends Occupancy> occupancies = new ArrayList<>();
    private boolean includeStaticInfo;

    private LocalDate formalizationLocalDate;
    private LocalDate checkInLocalDate;
    private LocalDate checkOutLocalDate;
    private int totalNights;
    private int release;

    public DispoRQ(LocalDate formalizationLocalDate, int checkIn, int checkOut, List<? extends Occupancy> occupancies, boolean includeStaticInfo) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.occupancies = occupancies;
        this.includeStaticInfo = includeStaticInfo;
        this.formalizationLocalDate = formalizationLocalDate;

        this.checkInLocalDate = Helper.toDate(checkIn);
        this.checkOutLocalDate = Helper.toDate(checkOut);

        setTotalNights((int) DAYS.between(checkInLocalDate, checkOutLocalDate));
        setRelease((int) DAYS.between(formalizationLocalDate, checkInLocalDate));
    }

    @Override
    public String toString() {
        StringBuffer ocs = new StringBuffer();
        for (Occupancy o : occupancies) {
            if (!"".equalsIgnoreCase(ocs.toString())) ocs.append("|");
            ocs.append(o.getNumberOfRooms());
            ocs.append("x");
            ocs.append(o.getPaxPerRoom());
            if (o.getAges() != null) for (int a : o.getAges()) {
                ocs.append("-");
                ocs.append(a);
            }
        }
        return "" + checkIn + "-" + checkOut + "," + ocs + "," + formalizationLocalDate + "," + includeStaticInfo;
    }
}
