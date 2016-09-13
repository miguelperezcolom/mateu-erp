package mateu.erp.model.multilanguage;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * holder for translations. Hardcoding translations is used for better performance
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Table(name = "MA_TRANSLATION")
@Getter@Setter
public class Translation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator="translation_seq_gen")
    @SequenceGenerator(name="translation_seq_gen", sequenceName="TRAIDTRA_SEQ")
    @Column(name = "TRAIDTRA")
    private long id;

    @Column(name = "TRAEN", length = -1)
    private String en;

    @Column(name = "TRAES", length = -1)
    private String es;

    @Column(name = "TRADE", length = -1)
    private String de;

    @Column(name = "TRAFR", length = -1)
    private String fr;

    @Column(name = "TRAIT", length = -1)
    private String it;

    @Column(name = "TRAAR", length = -1)
    private String ar;

    @Column(name = "TRACZ", length = -1)
    private String cz;

    @Column(name = "TRARU", length = -1)
    private String ru;

}
