package io.mateu.erp.dispo.interfaces.product;

import io.mateu.erp.dispo.interfaces.common.IActor;
import io.mateu.erp.model.product.hotel.DatesRange;
import io.mateu.erp.model.product.hotel.DatesRanges;

import java.time.LocalDate;
import java.util.List;

public interface IHotelOffer {
    public boolean isActive();

    public LocalDate getBookingWindowFrom();

    public LocalDate getBookingWindowTo();

    public LocalDate getLastCheckout();

    public List<? extends IActor> getTargets();

    public int getApplicationMinimumNights();

    public int getApplicationMaximumStay();

    public int getApplicationRelease();

    public List<? extends IHotelContract> getContracts();

    public List<? extends IHotelOffer> getCumulativeTo();

    DatesRanges getStayDates();

    public String getName();
}
