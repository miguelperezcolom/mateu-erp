package io.mateu.erp.model.authentication;

import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.product.transfer.TransferPoint;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class AirportUser extends io.mateu.mdd.core.model.authentication.User {

    @ManyToOne@NotNull
    private TransferPoint airport;


}
