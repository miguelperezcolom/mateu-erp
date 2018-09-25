package io.mateu.erp.client.booking.views;

import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.mdd.core.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PickupConfirmationRow {

    @Ignored
    private long id;

    private String reference;

    private String leadName;

    private TransferType transferType;

    private int pax;

    private LocalDateTime pickupTime;

    private String pickup;

    private String alternatePickup;


    public PickupConfirmationRow(long id, String reference, String leadName, TransferType transferType, int pax, LocalDateTime pickupTime, String pickup, Object alternatePickup) {
        this.id = id;
        this.reference = reference;
        this.leadName = leadName;
        this.transferType = transferType;
        this.pax = pax;
        this.pickupTime = pickupTime;
        this.pickup = pickup;
        this.alternatePickup = alternatePickup != null?"" + alternatePickup:null;
    }

    @Override
    public String toString() {
        return "" + id;
    }
}
