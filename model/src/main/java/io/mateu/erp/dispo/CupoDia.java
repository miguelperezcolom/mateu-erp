package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class CupoDia {

    private Map<String, Integer> disponible = new HashMap<>();


}
