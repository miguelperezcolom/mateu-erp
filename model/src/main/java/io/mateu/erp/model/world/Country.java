package io.mateu.erp.model.world;

import io.mateu.erp.model.taxes.VAT;
import io.mateu.mdd.core.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for countries
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
//@QLForCombo(ql = "select x.isoCode, x.name from io.mateu.erp.model.world.Country x order by x.name")
public class Country {

    @Id
    @NotNull
    private String isoCode;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "country")
    @Ignored
    private List<Destination> destinations = new ArrayList<>();

    @ManyToOne
    private VAT vat;
}