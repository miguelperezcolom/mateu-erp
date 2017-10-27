package io.mateu.erp.model.booking.hotel;

import io.mateu.ui.mdd.server.annotations.Required;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class Occupation {

    @Required
    private int numberOfRooms = 1;

    @Required
    private int paxPerRoom = 2;

    private int[] ages;


    public Occupation() {

    }

    public Occupation(int numberOfRooms, int paxPerRoom, int[] ages) {
        this.numberOfRooms = numberOfRooms;
        this.paxPerRoom = paxPerRoom;
        this.ages = ages;
    }
}
