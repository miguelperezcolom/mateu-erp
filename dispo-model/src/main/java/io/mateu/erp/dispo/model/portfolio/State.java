package io.mateu.erp.dispo.model.portfolio;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class State {

    @Id
    private long id;

    private String name;

    @ManyToOne
    private Country country;

    @OneToMany(mappedBy = "state")
    private List<City> cities = new ArrayList<>();

}
