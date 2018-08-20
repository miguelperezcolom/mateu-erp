package io.mateu.erp.dispo.interfaces.portfolio;

import io.mateu.erp.dispo.interfaces.product.*;

import java.util.List;

public interface IHotel {
    public long getId();

    public String getName();

    public String getLat();

    public String getLon();

    public String getCategoryId();

    public String getCategoryName();

    public List<? extends IStopSaleLine> getStopSalesLines();

    List <? extends IInventory> getInventories();

    List<? extends IRoom> getRooms();

    List<? extends IHotelContract> getContracts();

    List<? extends IHotelOffer> getOffers();

    List<? extends IBoard> getBoards();

    public int getChildStartAge();

    public int getJuniorStartAge();

    public int getAdultStartAge();

    public boolean isActive();
}
