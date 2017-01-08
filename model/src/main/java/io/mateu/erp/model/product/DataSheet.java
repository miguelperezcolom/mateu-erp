package io.mateu.erp.model.product;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Table(name = "MA_DATASHEET")
@Getter
@Setter
public class DataSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="datasheet_seq_gen")
    @SequenceGenerator(name="datasheet_seq_gen", sequenceName="DSH_SEQ", allocationSize = 1)
    @Column(name = "DSHIDDSH")
    private long id;

    @ManyToOne
    @JoinColumn(name = "DSHDESCRIPTIONIDLIT")
    private Literal description;

    @OneToMany(mappedBy = "dataSheet")
    private List<FeatureValue> features = new ArrayList<>();
}
