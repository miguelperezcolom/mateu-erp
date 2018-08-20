package io.mateu.erp.dispo;

public class Occupancy extends org.easytravelapi.hotel.Occupancy {

    private String boardId;


    public Occupancy(int numberOfRooms, int paxPerRoom, int[] ages) {
        super(numberOfRooms, paxPerRoom, ages);
    }

    public Occupancy(int numberOfRooms, int paxPerRoom, int[] ages, String boardId) {
        super(numberOfRooms, paxPerRoom, ages);
        this.boardId = boardId;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }
}
