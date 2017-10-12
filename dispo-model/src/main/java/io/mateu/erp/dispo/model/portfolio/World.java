package io.mateu.erp.dispo.model.portfolio;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class World {

    @Id
    private long id = 1;

    private List<Country> countries = new ArrayList<>();

}
