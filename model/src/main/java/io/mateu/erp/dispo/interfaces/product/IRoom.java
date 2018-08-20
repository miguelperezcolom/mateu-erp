package io.mateu.erp.dispo.interfaces.product;

public interface IRoom {
    public boolean fits(int adults, int children, int babies);

    public String getCode();

    public String getName();

    public long getId();

    public String getInventoryPropietaryRoomCode();

    public int getMinAdultsForChildDiscount();
}
