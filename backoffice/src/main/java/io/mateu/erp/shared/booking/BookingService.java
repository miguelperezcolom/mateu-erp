package io.mateu.erp.shared.booking;

import io.mateu.ui.core.communication.Service;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 23/4/17.
 */
@Service(url = "booking")
public interface BookingService {

    public Data getTransferSummary(Data parameters) throws Throwable;

}
