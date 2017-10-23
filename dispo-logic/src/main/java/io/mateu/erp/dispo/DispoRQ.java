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

    private LocalDate checkInLocalDate;
    private LocalDate checkOutLocalDate;
    private int totalNights;

    public DispoRQ(int checkIn, int checkOut, List<? extends Occupancy> occupancies, boolean includeStaticInfo) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.occupancies = occupancies;
        this.includeStaticInfo = includeStaticInfo;

        this.checkInLocalDate = Helper.toDate(checkIn);
        this.checkOutLocalDate = Helper.toDate(checkOut);

        setTotalNights((int) DAYS.between(checkInLocalDate, checkOutLocalDate));
    }
}
