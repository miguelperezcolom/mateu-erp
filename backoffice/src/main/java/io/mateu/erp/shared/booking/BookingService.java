package io.mateu.erp.shared.booking;

import io.mateu.ui.core.communication.Service;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;

import java.util.List;

/**
 * Created by miguel on 23/4/17.
 */
@Service(url = "booking")
public interface BookingService {

    public Data getTransferSummary(Data parameters) throws Throwable;

    public void retryImportationTasks(List<Data> selection) throws Throwable;

    public void cancelImportationTasks(List<Data> selection) throws Throwable;

    public void informPickupTime(UserData user, List<Data> selection) throws Throwable;

    public String importPickupTimeExcel(Data data) throws Throwable;

    public Data getAvailableHotels(Data parameters) throws Throwable;

}
