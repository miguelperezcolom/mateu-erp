package io.mateu.erp.dispo.model.integrations;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter@Setter
public class Integration {

    @Id
    private long id;

    private String name;

    private String baseUrl;

    private boolean active;

    private boolean providingHotels;

}
