package mateu.erp.model.booking;

import lombok.Getter;
import lombok.Setter;
import mateu.erp.model.authentication.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

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
    private Date created;

    @Column(name = "BKNMODIFIEDBY")
    private User modifiedBy;

    @Column(name = "BKNMODIFIED")
    private Date modified;


}
