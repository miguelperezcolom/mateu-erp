package io.mateu.erp.consolidador;

import io.mateu.erp.dispo.Occupancy;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class DispoRQ {

    private String token;
    private List<String> resorts;
    private int checkIn;
    private int checkout;
    private List<Occupancy> occupancies = new ArrayList<>();
    private boolean includeStaticInfo;

    public DispoRQ(String token, List<String> resorts, int checkIn, int checkout, List<Occupancy> occupancies, boolean includeStaticInfo) {
        this.token = token;
        this.resorts = resorts;
        this.checkIn = checkIn;
        this.checkout = checkout;
        this.occupancies = occupancies;
        this.includeStaticInfo = includeStaticInfo;
    }
}
