package io.mateu.erp.model.world;

import io.mateu.erp.model.product.transfer.TransferPoint;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity@Getter@Setter
public class Airport {

    @Id@NotEmpty
    private String iataCode;

    @NotEmpty
    private String name;

    @ManyToOne@NotNull
    private Destination destination;

    @ManyToOne
    private TransferPoint transferPoint;

}
