package io.mateu.erp.model.booking.transfer;

import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
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
public class TransferService extends Service implements WithTriggers {

    @StartsLine
    @Required
    private TransferType transferType;

    @Required
    private int pax;

    @StartsLine
    private String pickupText;
    @ManyToOne
    @NotInList
    private TransferPoint pickup;
    @ManyToOne
    @Ignored
    private TransferPoint effectivePickup;


    @StartsLine
    private String dropoffText;
    @ManyToOne
    @NotInList
    private TransferPoint dropoff;
    @ManyToOne
    @Ignored
    private TransferPoint effectiveDropoff;

    @StartsLine
    private String flightNumber;
    private LocalDateTime flightTime;
    private String flightOriginOrDestination;

    @StartsLine
    private LocalDateTime pickupTime;
    private LocalDateTime pickupConfirmed;
    private PickupConfirmationWay pickupConfirmedThrough;



    /*
    private int bikes;
    private int golfBags;
    */



    @Override
    public void beforeSet(boolean isNew) {

    }

    @Override
    public void afterSet(boolean isNew) throws Exception {
        if ((getPickupText() == null || "".equals(getPickupText().trim())) && getPickup() == null) throw new Exception("Pickup is required");
        if ((getDropoffText() == null || "".equals(getDropoffText().trim())) && getDropoff() == null) throw new Exception("Dropoff is required");
    }

    @Override
    public void beforeDelete() {

    }

    @Override
    public void afterDelete() {

    }
}
