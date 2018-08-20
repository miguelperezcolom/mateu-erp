package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class ParoDia {

    private boolean allClosed;
    private boolean onNormalInventory = true;
    private boolean onSecurityInventory;
    private List<String> roomsClosed = new ArrayList<>();
    private List<Long> clientsClosed = new ArrayList<>();

}
