package io.mateu.erp.model.product.generic;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class ReleaseOnGeneric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private GenericProduct product;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    private int releaseDays;

    private int lastAllowedHour;

}
