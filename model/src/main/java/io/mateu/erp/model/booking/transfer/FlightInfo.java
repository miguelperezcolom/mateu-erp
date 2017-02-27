package io.mateu.erp.model.booking.transfer;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

/**
 * Created by miguel on 25/2/17.
 */
@Embeddable
@Getter@Setter
public class FlightInfo {

    private String number;
    private LocalDateTime time;
    private String originOrDestination;

}
