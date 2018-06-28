package io.mateu.erp.model.thirdParties;

import io.mateu.erp.dispo.interfaces.integrations.IIntegration;
import io.mateu.erp.model.revenue.ProductLine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Integration implements IIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @NotNull
    @ManyToOne
    private ProductLine product;

    private String baseUrl;

    private boolean active;

    private boolean providingHotels;

    private int maxResourcesPerRequest;

}
