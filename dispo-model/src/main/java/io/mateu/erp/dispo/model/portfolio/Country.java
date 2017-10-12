package io.mateu.erp.dispo.model.portfolio;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class Country {

    @Id
    private String code;

    private String name;

    @OneToMany(mappedBy = "country")
    private List<State> states = new ArrayList<>();

}
