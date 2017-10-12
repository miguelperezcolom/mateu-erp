package io.mateu.erp.dispo.interfaces.product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface IStopSaleLine {

    public LocalDate getStart();

    public LocalDate getEnd();

    public boolean isOnNormalInventory();
    public boolean isOnSecurityInventory();


    public List<String> getRoomIds();

    public List<Long> getActorIds();
}
