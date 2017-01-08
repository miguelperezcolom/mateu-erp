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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="literal_seq_gen")
    @SequenceGenerator(name="literal_seq_gen", sequenceName="LITIDLIT_SEQ", allocationSize = 1)
    @Column(name = "LITIDLIT")
    private long id;

    @Column(name = "LITEN")
    private String en;

    @Column(name = "LITES")
    private String es;

    @Column(name = "LITDE")
    private String de;

    @Column(name = "LITFR")
    private String fr;

    @Column(name = "LITIT")
    private String it;

    @Column(name = "LITAR")
    private String ar;

    @Column(name = "LITCZ")
    private String cz;

    @Column(name = "LITRU")
    private String ru;

}
