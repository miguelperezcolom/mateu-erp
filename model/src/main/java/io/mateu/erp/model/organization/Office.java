package io.mateu.erp.model.organization;

import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.ui.mdd.server.annotations.NotInList;
import io.mateu.ui.mdd.server.annotations.SameLine;
import io.mateu.ui.mdd.server.annotations.Separator;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    @NotNull
    private String name;

    @NotNull
    @ManyToOne
    @NotInList
    private TransferPoint defaultAirportForTransfers;


    @Separator("Email")
    @NotInList
    private String emailHost;
    @NotInList
    @SameLine
    private int emailPort;
    @NotInList
    private String emailUsuario;
    @NotInList
    @SameLine
    private String emailPassword;
    @NotInList
    private String emailFrom;
    @NotInList
    @SameLine
    private String emailCC;


}
