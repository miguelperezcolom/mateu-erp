package io.mateu.erp.services;

public class HotelAvailabilityStatsData {

    public double priceAvg;
    public double priceMin;
    public double priceMax;

    public double returnedHotelsNoAvg;
    public long returnedHotelsNoMin;
    public long returnedHotelsNoMax;

    public double returnedPricesNoAvg;
    public long returnedPricesNoMin;
    public long returnedPricesNoMax;

    public long totalRqs;

    public double checkinAvg;
    public long checkinMin;
    public long checkinMax;

    public double checkoutAvg;
    public long checkoutMin;
    public long checkoutMax;

    public double stayAvg;
    public long stayMin;
    public long stayMax;


    public HotelAvailabilityStatsData(double priceAvg, double priceMin, double priceMax, double returnedPricesNoAvg, long returnedPricesNoMin, long returnedPricesNoMax, double returnedHotelsNoAvg, long returnedHotelsNoMin, long returnedHotelsNoMax, long totalRqs, double checkinAvg, long checkinMin, long checkinMax, double checkoutAvg, long checkoutMin, long checkoutMax, double stayAvg, long stayMin, long stayMax) {
        this.priceAvg = priceAvg;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.returnedPricesNoAvg = returnedPricesNoAvg;
        this.returnedPricesNoMin = returnedPricesNoMin;
        this.returnedPricesNoMax = returnedPricesNoMax;
        this.returnedHotelsNoAvg = returnedHotelsNoAvg;
        this.returnedHotelsNoMin = returnedHotelsNoMin;
        this.returnedHotelsNoMax = returnedHotelsNoMax;
        this.totalRqs = totalRqs;
        this.checkinAvg = checkinAvg;
        this.checkinMin = checkinMin;
        this.checkinMax = checkinMax;
        this.checkoutAvg = checkoutAvg;
        this.checkoutMin = checkoutMin;
        this.checkoutMax = checkoutMax;
        this.stayAvg = stayAvg;
        this.stayMin = stayMin;
        this.stayMax = stayMax;
    }

    public HotelAvailabilityStatsData clonar() {
        return new HotelAvailabilityStatsData(priceAvg, priceMin, priceMax, returnedPricesNoAvg, returnedPricesNoMin, returnedPricesNoMax, returnedHotelsNoAvg, returnedHotelsNoMin, returnedHotelsNoMax, totalRqs, checkinAvg, checkinMin, checkinMax, checkoutAvg, checkoutMin, checkoutMax, stayAvg, stayMin, stayMax);
    }
}
