package io.mateu.erp.dispo.interfaces.product;

import java.time.LocalDate;

public interface IInventoryLine {

    public LocalDate getStart();

    public LocalDate getEnd();

    public int getQuantity();

    public String getRoomCode();
}
