package io.mateu.erp.model.world

import io.mateu.ui.mdd.server.annotations.Ignored
import lombok.Getter
import lombok.Setter

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull
import java.util.ArrayList

/**
 * holder for countries
 *
 * Created by miguel on 13/9/16.
 */
@Entity
//@QLForCombo(ql = "select x.isoCode, x.name from io.mateu.erp.model.world.Country x order by x.name")
class Country {

    @Id
    @NotNull
    var isoCode: String? = null

    @NotNull
    var name: String? = null

    @OneToMany(mappedBy = "country")
    @Ignored
    var states = ArrayList<State>()
}
