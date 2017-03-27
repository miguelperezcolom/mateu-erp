package io.mateu.erp.model.importing;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.financials.Actor;

import javax.persistence.ManyToOne;

/**
 * Created by Antonia on 26/03/2017.
 */
public class ShuttleDirectImportTask extends TransferImportTask {


    public void ShuttleDirectImportTask(String name, User user, Actor customer, String file)
    {
       this.setCustomer(customer);

       this.setName(name);

       this.setAudit(null);//TODO

       this.setPriority(0);

       this.setStatus(STATUS.PENDING);

       this.setFile(file);

    }

    public void ShuttleDirectImportTask( User user, Actor customer, String file)
    {
        ShuttleDirectImportTask("ShuttleDirect", user, customer,file);
    }


    @Override
    public String importTask() {
        //recorre cada linea del fichero
        //por cada una rellena un "transferBookingRequest" y llama a save()
        //vamos guardando el resultado junto con la refAge para crear el informe final
        //finalmente se actualiza el audit y se cambia el estado a Done y se graba el informe final

        return "";
    }
}
