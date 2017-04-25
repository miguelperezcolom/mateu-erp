package io.mateu.erp.model.booking;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by miguel on 19/4/17.
 */
public enum ProcessingStatus {
    INITIAL
    , DATA_OK
    , PURCHASEORDERS_READY
    , PURCHASEORDERS_SENT
    , PURCHASEORDERS_REJECTED
    , PURCHASEORDERS_CONFIRMED
}
