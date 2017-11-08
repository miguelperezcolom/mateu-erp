package io.mateu.erp.model.thirdParties;

import io.mateu.erp.dispo.interfaces.integrations.IIntegration;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Integration implements IIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String baseUrl;

    private boolean active;

    private boolean providingHotels;

    private int maxResourcesPerRequest;

}
