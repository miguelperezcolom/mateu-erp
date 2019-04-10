package io.mateu.erp.model.authentication;

import io.mateu.erp.model.partners.Agency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class CustomUser extends io.mateu.mdd.core.model.authentication.User {


}
