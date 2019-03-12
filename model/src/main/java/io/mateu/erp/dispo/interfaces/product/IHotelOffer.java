package io.mateu.erp.dispo.interfaces.product;

import io.mateu.erp.dispo.Condiciones;
import io.mateu.erp.dispo.LineaReserva;
import io.mateu.erp.dispo.ValoracionLineaReserva;
import io.mateu.erp.dispo.interfaces.common.IPartner;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.product.DatesRanges;

import java.time.LocalDate;
import java.util.Set;

public interface IHotelOffer {
    public boolean isActive();

    public LocalDate getBookingWindowFrom();

    public LocalDate getBookingWindowTo();

    public LocalDate getLastCheckout();

    public Set<? extends Agency> getTargets();

    public int getApplicationMinimumNights();

    public int getApplicationMaximumStay();

    public int getApplicationRelease();

    public Set<? extends IHotelContract> getContracts();

    public Set<? extends IHotelOffer> getCumulativeTo();

    DatesRanges getStayDates();

    public String getName();

    public double aplicar(IBoard board, IRoom room, LineaReserva lineaReserva, ValoracionLineaReserva vlr, IHotelOffer o, Condiciones cpr);
}
