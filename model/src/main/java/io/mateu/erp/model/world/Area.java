package io.mateu.erp.model.world;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for resorts. A resort is used to group several resorts under the same name (e.g. Calas de Mallorca)
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;


    @OneToMany
     private List<Resort> cities = new ArrayList<>();


    @Override
    public String toString() {
        return getName();
    }
}
