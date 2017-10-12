package io.mateu.erp.dispo.interfaces.product;

import io.mateu.erp.model.product.hotel.HotelContractPhoto;

import java.time.LocalDate;

public interface IHotelContract {
    public LocalDate getValidFrom();
    public LocalDate getValidTo();

    public HotelContractPhoto getTerms();
}
