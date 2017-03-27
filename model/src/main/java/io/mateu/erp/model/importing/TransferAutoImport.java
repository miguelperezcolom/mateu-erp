package io.mateu.erp.model.importing;

import io.mateu.erp.model.financials.Actor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Antonia on 26/03/2017.
 */

@Entity
@Getter
@Setter
public abstract class TransferAutoImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String url;
    private String login;
    private String password;

    @ManyToOne
    private Actor customer;//cliente de las reservas

    public abstract void getBookings(Date from, int days);
}
