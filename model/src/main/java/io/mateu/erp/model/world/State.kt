package io.mateu.erp.model.world

import io.mateu.erp.model.product.transfer.TransferPoint
import io.mateu.ui.mdd.server.annotations.Ignored

import javax.persistence.*
import javax.validation.constraints.NotNull
import java.util.ArrayList

/**
 * holder for states
 *
 * Created by miguel on 13/9/16.
 */
@Entity
class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne
    @NotNull
    var country: Country? = null

    @NotNull
    var name: String? = null

    @OneToMany(mappedBy = "state")
    @Ignored
    var cities = ArrayList<City>()

    @OneToMany(mappedBy = "gatewayOf")
    @Ignored
    var gateways = ArrayList<TransferPoint>()

}
