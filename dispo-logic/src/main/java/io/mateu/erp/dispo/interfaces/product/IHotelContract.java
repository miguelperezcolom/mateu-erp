package io.mateu.erp.dispo.interfaces.product;

import io.mateu.erp.dispo.interfaces.common.IPartner;
import io.mateu.erp.model.product.hotel.HotelContractPhoto;

import java.time.LocalDate;
import java.util.List;

public interface IHotelContract {
    public LocalDate getValidFrom();
    public LocalDate getValidTo();

    public HotelContractPhoto getTerms();

    public List<? extends IPartner> getTargets();

    long getId();
}
