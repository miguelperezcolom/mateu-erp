package io.mateu.erp.model.thirdParties;

import io.mateu.erp.dispo.interfaces.integrations.IIntegration;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.revenue.ProductLine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Integration implements IIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    private String name;

    @NotNull
    @ManyToOne
    private ProductLine productLine;

    @NotNull
    @ManyToOne
    private Office office;

    private String baseUrl;

    private boolean active;

    private boolean providingHotels;

    private int maxResourcesPerRequest;

}
