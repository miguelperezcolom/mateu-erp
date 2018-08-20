package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IHotelContract;

public interface ModeloDispo {
    IHotelContract getHotelContract(long id);
}
