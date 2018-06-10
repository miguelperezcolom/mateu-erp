package io.mateu.erp.dispo.model.common;

import io.mateu.erp.dispo.model.integrations.Integration;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class Partner {

    @Id
    private long id;

    private String name;

    @OneToMany
    private List<Integration> integrations = new ArrayList<>();
}
