package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * holder for bookings. Basically a booking locator associated with a customer, under which we will
 * keep a list of booked services, charges, etc
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Table(name = "MA_BOOKING")
@Getter
@Setter
public class Booking {

    @Id
    @Column(name = "BKNLOCATOR", length = -1)
    private String locator;

    @Column(name = "BKNCREATEDBY")
    private User createdBy;

    @Column(name = "BKNCREATED")
    @Temporal(TIMESTAMP)
    private Date created;

    @Column(name = "BKNMODIFIEDBY")
    private User modifiedBy;

    @Column(name = "BKNMODIFIED")
    @Temporal(TIMESTAMP)
    private Date modified;


}
