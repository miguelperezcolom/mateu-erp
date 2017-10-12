package io.mateu.erp.dispo.interfaces.product;

import org.easytravelapi.hotel.Occupancy;

public interface IRoom {
    public boolean fits(Occupancy o);

    public String getCode();

    public String getName();

    public long getId();
}
