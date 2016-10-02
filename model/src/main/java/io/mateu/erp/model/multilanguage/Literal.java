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
@Table(name = "MA_LITERAL")
@Getter@Setter
public class Literal {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator="literal_seq_gen")
    @SequenceGenerator(name="literal_seq_gen", sequenceName="LITIDLIT_SEQ")
    @Column(name = "LITIDLIT")
    private long id;

    @Column(name = "LITEN", length = -1)
    private String en;

    @Column(name = "LITES", length = -1)
    private String es;

    @Column(name = "LITDE", length = -1)
    private String de;

    @Column(name = "LITFR", length = -1)
    private String fr;

    @Column(name = "LITIT", length = -1)
    private String it;

    @Column(name = "LITAR", length = -1)
    private String ar;

    @Column(name = "LITCZ", length = -1)
    private String cz;

    @Column(name = "LITRU", length = -1)
    private String ru;

}
