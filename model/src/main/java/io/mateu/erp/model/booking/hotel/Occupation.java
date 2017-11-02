package io.mateu.erp.model.booking.hotel;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter@Setter
public class Occupation {

    @NotNull
    private int numberOfRooms = 1;

    @NotNull
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
