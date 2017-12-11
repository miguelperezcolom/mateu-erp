package io.mateu.erp.dispo;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

public class Helper {

    private static ObjectMapper mapper = new ObjectMapper();

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
        return (start == null || start.isBefore(checkOut)) && (end == null || end.compareTo(checkIn) >= 0);
    }

    public static boolean cabe(LocalDate validFrom, LocalDate validTo, LocalDate checkIn, LocalDate checkOut) {
        return validFrom.compareTo(checkIn) <= 0 && validTo.compareTo(checkOut) >= 0;
    }

    public static Date toDate(LocalDateTime localDateTime) {
        Date out = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return out;
    }

    public static double roundEuros(double value) {
        return Math.round(value * 100d) / 100d;
    }

    public static int[] toIntArray(List<Integer> l) {
        int[] a = null;
        if (l != null) {
            a = new int[l.size()];
            for (int pos = 0; pos < l.size(); pos++) a[pos] = l.get(pos);
        }
        return a;
    }

    public static Map<String, Object> fromJson(String json) throws IOException {
        if (json == null || "".equals(json)) json = "{}";
        return mapper.readValue(json, Map.class);
    }

    public static <T> T fromJson(String json, Class<T> c) throws IOException {
        if (json == null || "".equals(json)) json = "{}";
        return mapper.readValue(json, c);
    }

    public static String toJson(Object o) throws IOException {
        return mapper.writeValueAsString(o);
    }
}
