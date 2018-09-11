package io.mateu.erp.client.operations;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class HotelCalendarRow {

    private LocalDate date;

    private long bookings;


    public HotelCalendarRow(LocalDate date, long bookings) {
        this.date = date;
        this.bookings = bookings;
    }

    @Override
    public String toString() {
        return (date != null)?date.format(DateTimeFormatter.ofPattern("yyyyMMdd")):"unknown";
    }
}
