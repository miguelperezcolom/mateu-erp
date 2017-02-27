package io.mateu.erp.model.booking.transfer;

import io.mateu.erp.model.product.transfer.TransferPoint;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by miguel on 25/2/17.
 */
@Entity
@Getter
@Setter
public class Pickup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private TransferPoint point;

    private String comments;

    private LocalDateTime time;

    private LocalDateTime confirmed;
    private PickupConfirmationWay confirmedThrough;
}
