package io.mateu.erp.dispo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

public class Helper {

    public static LocalDate toDate(int n) {
        return LocalDate.of((n - n % 10000) / 10000, ((n % 10000) - n % 100) / 100, n % 100);
    }

    public static int toInt(LocalDate n) {
        return n.getDayOfMonth() + n.getMonthValue() * 100 + n.getYear() * 10000;
    }

    public static long noches(LocalDate start, LocalDate end) {
        return DAYS.between(start, end);
    }

    public static boolean intersects(LocalDate start, LocalDate end, LocalDate checkIn, LocalDate checkOut) {
        return start.isBefore(checkOut) && end.compareTo(checkIn) >= 0;
    }

    public static boolean cabe(LocalDate validFrom, LocalDate validTo, LocalDate checkIn, LocalDate checkOut) {
        return validFrom.compareTo(checkIn) <= 0 && validTo.compareTo(checkOut) >= -1;
    }

    public static Date toDate(LocalDateTime localDateTime) {
        Date out = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return out;
    }

    public static double roundEuros(double value) {
        return Math.round(value * 100d) / 100d;
    }
}
