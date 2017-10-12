package io.mateu.erp.dispo.model.portfolio;

import io.mateu.erp.dispo.model.integrations.Integration;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter@Setter
public class Resource {

    @Id
    private long id;

    private String name;

    @ManyToOne
    private City city;

    private String foreignId;

    @ManyToOne
    private Integration integration;
}
