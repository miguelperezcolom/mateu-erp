package io.mateu.erp.model.multilanguage;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * holder for translations. Hardcoding translations is used for better performance
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class Literal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String en;

    private String es;

    private String de;

    private String fr;

    private String it;

    private String ar;

    private String cz;

    private String ru;

}