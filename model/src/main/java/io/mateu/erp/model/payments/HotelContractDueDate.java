package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.annotations.Position;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class HotelContractDueDate extends DueDate {

    @ManyToOne@Position(1)
    @NotNull
    private HotelContract hotelContract;
}
