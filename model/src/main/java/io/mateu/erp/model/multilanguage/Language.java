package io.mateu.erp.model.multilanguage;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * holder for supported languages. Languages are created manually by the dev team
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class Language {

    @Id
    @NotNull
    private String isoCode;

    @NotNull
    private String name;
}
