package io.mateu.erp.model.product;


import io.mateu.common.model.multilanguage.Literal;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.world.Zone;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter@Setter
public class ZoneProductRemark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private ProductType productType;

    @ManyToOne
    private Country country;

    @ManyToOne
    private Destination destination;

    @ManyToOne
    private Zone zone;

    @ManyToOne
    private Literal text;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;


    private boolean active;

}
