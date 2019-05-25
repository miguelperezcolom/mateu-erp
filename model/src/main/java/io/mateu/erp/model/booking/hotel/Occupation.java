package io.mateu.erp.model.booking.hotel;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter@Setter
public class Occupation {

    @NotNull
    private int numberOfRooms;

    @NotNull
    private int paxPerRoom;

    private int[] ages;


    public Occupation() {

    }

    public Occupation(int numberOfRooms, int paxPerRoom, int[] ages) {
        this.numberOfRooms = numberOfRooms;
        this.paxPerRoom = paxPerRoom;
        this.ages = ages;
    }
}
