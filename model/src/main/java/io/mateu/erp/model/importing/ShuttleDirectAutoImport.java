package io.mateu.erp.model.importing;

import io.mateu.erp.model.financials.Actor;

import java.util.Date;

/**
 * Created by Antonia on 26/03/2017.
 */
public class ShuttleDirectAutoImport extends TransferAutoImport {

    public void ShuttleDirectAutoImport(String url, String login, String pwd, Actor cus) {
        this.setLogin(login);
        this.setPassword(pwd);
        this.setUrl(url);
        this.setCustomer(cus);
    }

    public void getBookings(Date from, int days)
    {
        //ir a la web, hacer login y recuperar fichero

        //crear nueva ShuttleDirectImportTask

    }

}
