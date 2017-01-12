package io.mateu.erp.model.product;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Table(name = "MA_FEATUREVALUE")
@Getter
@Setter
public class FeatureValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FVAIDFVA")
    private long id;

    @ManyToOne
    @JoinColumn(name = "FVAIDDSH")
    DataSheet dataSheet;

    @ManyToOne
    @JoinColumn(name = "FVAIDFEA")
    Feature feature;

    @Column(name = "FVAVALUE")
    private String value;

}
