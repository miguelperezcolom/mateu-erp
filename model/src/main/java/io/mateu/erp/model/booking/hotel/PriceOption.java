package io.mateu.erp.model.booking.hotel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceOption {

    private String rooms;

    private String board;

    private double price;

    private String currency;

    private String key;

}
