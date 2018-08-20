package io.mateu.erp.model.importing;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.model.util.Constants;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Created by Antonia on 26/03/2017.
 */
@Entity
@Getter
@Setter
public class ShuttleDirectAutoImport extends TransferAutoImport {
    private String idTransportista;

    public ShuttleDirectAutoImport()
    {
    }

    public ShuttleDirectAutoImport(String url, String login, String pwd, Partner cus, String idtransportista) {
        this.setLogin(login);
        this.setPassword(pwd);
        this.setUrl(url);
        this.setCustomer(cus);
        this.setIdTransportista(idtransportista);
    }

    public void getBookings(LocalDate from, int days)
    {

        try {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    //ir a la web, hacer login y recuperar fichero
                    String xml = "";
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    DateTimeFormatter dfh = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    String fdesde = from.format(df);
                    String fhasta = from.plusDays(days).format(df);
                    try {
                        xml = recuperarXml(fdesde,fhasta);
                    } catch (Exception e)
                    {
                        addHistory(LocalDateTime.now().format(dfh) + " - Error: " + e.getMessage() + " \n " + e.getStackTrace());
                        e.printStackTrace();
                        return; //Salimos porque sin el fichero no podemos hacer nada
                    }

                    //crear nueva ShuttleDirectImportTask
                    if (xml!=null && xml.length()>0)
                    {
                        User u = em.find(User.class, Constants.IMPORTING_USER_LOGIN);
                        ShuttleDirectImportTask t = new ShuttleDirectImportTask(getName()+ " (" + fdesde + "-" + fhasta+")",u, getCustomer(),xml, getOffice(), getPointOfSale());
                        em.persist(t);
                        addHistory(LocalDateTime.now().format(dfh)+ " - Tarea creada");
                    }
                    else {
                        System.out.println("Error: el xml esta vacio!");
                        addHistory(LocalDateTime.now().format(dfh) + " - Error: el xml esta vacio!");
                        return; //Salimos porque sin el fichero no podemos hacer nada
                    }


                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }


     }

    private String recuperarXml(String fini, String ffin) throws Exception {
        HashMap<String, String> params = new HashMap<String,String>();
        params.put("codeSupplierViajesAlameda",this.getLogin());
        params.put("passwordSupplierViajesAlameda",this.getPassword());
        params.put("comboFecha", "-1");
        params.put("fechaInicio", fini);
        params.put("fechaFin", ffin);
        params.put("opcionListado", "0");
        params.put("orderQuery", "fechaTraslado desc");
        params.put("fechaInicioElegida", fini);
        params.put("fechaFinElegida", ffin);
        params.put("Logeando", "1");
        params.put("idTransportista", this.getIdTransportista());
        params.put("idioma", "En");
        params.put("buscarFecha", "calendario");

        String xml = doPost(this.getUrl(), params );
        return xml;

    }

    /*
    public static void main(String[] args)
    {
        try {
            String url = "http://www2.shuttledirect.com/supplier/admin/exportarListadoTransportista2Xml.php";
            String login = "VIB";
            String pwd = "f8qu34w8f8aq";
            String idTrans = "476";

            ShuttleDirectAutoImport auto = new ShuttleDirectAutoImport(url,login,pwd,null,idTrans );
            String s = auto.recuperarXml("01/01/2017", "01/12/2017");
            auto.getBookings(LocalDate.now(), 10);
            System.out.println(s);
        } catch (Exception e){
           e.printStackTrace();
        };

    }*/

}
