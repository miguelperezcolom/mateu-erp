package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * holder for booking. Basically a booking locator associated with a customer, under which we will
 * keep a list of booked services, charges, etc
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter
@Setter
public class Booking {

    @Id
    private String locator;

    @ManyToOne
    private User createdBy;

    @Temporal(TIMESTAMP)
    private Date created;

    @ManyToOne
    private User modifiedBy;

    @Temporal(TIMESTAMP)
    private Date modified;


}
