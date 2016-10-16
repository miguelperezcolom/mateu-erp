package io.mateu.erp.model.multilanguage;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * holder for supported languages. Languages are created manually by the dev team
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Table(name = "MA_LANGUAGE")
@Getter@Setter
public class Language {

    @Id
    @Column(name = "LANISOCODE")
    private String isoCode;

    @Column(name = "LANNAME")
    private String name;
}
