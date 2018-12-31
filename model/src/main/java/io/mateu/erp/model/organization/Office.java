package io.mateu.erp.model.organization;

import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.world.Zone;
import io.mateu.mdd.core.annotations.NotInList;
import io.mateu.mdd.core.annotations.Section;
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

    @Section("Info")
    @NotNull
    private String name;

    @NotNull
    @ManyToOne
    private Company company;

    @NotNull
    @ManyToOne
    private Currency currency;


    @NotNull
    @ManyToOne
    @NotInList
    private Zone city;


    private String email;

    private String telephone;

    private String fax;

    private String address;


    /*
    @NotNull
    @ManyToOne
    @NotInList
    @DataProvider(dataProvider = AirportDataProvider.class)
    private TransferPoint defaultAirportForTransfers;
    */

    @Section("Email")
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


    @Override
    public String toString() {
        return getName();
    }
}
