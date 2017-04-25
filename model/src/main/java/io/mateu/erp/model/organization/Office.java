package io.mateu.erp.model.organization;

import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.ui.mdd.server.annotations.NotInList;
import io.mateu.ui.mdd.server.annotations.Required;
import io.mateu.ui.mdd.server.annotations.StartsLine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * holder for offices (e.g. Central, Ibiza, Tokio)
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Required
    private String name;

    @Required
    @ManyToOne
    @NotInList
    private TransferPoint defaultAirportForTransfers;


    @StartsLine
    @NotInList
    private String emailHost;
    @NotInList
    private int emailPort;
    @NotInList
    private String emailUsuario;
    @NotInList
    private String emailPassword;
    @NotInList
    private String emailFrom;
    @NotInList
    private String emailCC;


}
