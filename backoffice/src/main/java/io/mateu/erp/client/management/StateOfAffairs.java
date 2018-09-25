package io.mateu.erp.client.management;

import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter@Setter
public class StateOfAffairs {

    @Output
    private String stateOfAffairs;

    {
        try {
            stateOfAffairs = Helper.leerFichero(StateOfAffairs.class.getResourceAsStream("data.html"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
