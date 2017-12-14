package io.mateu.erp.services;

import java.time.LocalDate;

public class HotelAvailabilityStats {

    private static HotelAvailabilityStatsData data;

    public static synchronized void add(
            double priceAvg
            , double priceMin
            , double priceMax
            , long returnedPrices
            , long returnedHotels
            , long checkin
            , long checkout
            , long stay
    ) {
        if (data == null) data = new HotelAvailabilityStatsData(
                priceAvg
                , priceMin
                , priceMax
                , returnedPrices
                , returnedPrices
                , returnedPrices
                , returnedHotels
                , returnedHotels
                , returnedHotels
                , 1
                , checkin
                , checkin
                , checkin
                , checkout
                , checkout
                , checkout
                , stay
                , stay
                , stay
        );
        else {
            data.priceAvg = (data.priceAvg * data.totalRqs + priceAvg) / (data.totalRqs + 1);
            if (returnedPrices > 0 && data.priceMin > priceMin) data.priceMin = priceMin;
            if (data.priceMax < priceMax) data.priceMax = priceMax;

            data.returnedPricesNoAvg = (data.returnedPricesNoAvg * data.totalRqs + returnedPrices) / (data.totalRqs + 1);
            if (data.returnedPricesNoMin > returnedPrices) data.returnedPricesNoMin = returnedPrices;
            if (data.returnedPricesNoMax < returnedPrices) data.returnedPricesNoMax = returnedPrices;

            data.checkinAvg = (data.checkinAvg * data.totalRqs + checkin) / (data.totalRqs + 1);
            if (data.checkinMin > checkin) data.checkinMin = checkin;
            if (data.checkinMax < checkin) data.checkinMax = checkin;

            data.checkoutAvg = (data.checkoutAvg * data.totalRqs + checkout) / (data.totalRqs + 1);
            if (data.checkoutMin > checkout) data.checkoutMin = checkout;
            if (data.checkoutMax < checkout) data.checkoutMax = checkout;

            data.stayAvg = (data.stayAvg * data.totalRqs + stay) / (data.totalRqs + 1);
            if (data.stayMin > stay) data.stayMin = stay;
            if (data.stayMax < stay) data.stayMax = stay;

            data.totalRqs++;
        }
    }

    public static synchronized HotelAvailabilityStatsData get(boolean reset) {
        HotelAvailabilityStatsData x = data;
        if (reset) data = null;
        else x = data.clonar();
        return x;
    }
}
