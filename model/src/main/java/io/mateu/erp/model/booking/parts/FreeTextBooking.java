package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.Booking;
import io.mateu.mdd.core.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter@Setter
public class FreeTextBooking extends Booking {

    @TextArea
    @NotEmpty
    private String serviceDescription;

}
