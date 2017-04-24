package io.mateu.erp.model.importing;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.model.util.Constants;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPAHelper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;



import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.io.StringReader;
import java.util.List;

/**
 * Created by Antonia on 26/03/2017.
 */
@Entity
@Getter@Setter
public class ShuttleDirectImportTask extends TransferImportTask {

    public ShuttleDirectImportTask() {}

    public ShuttleDirectImportTask(String name, User user, Actor customer, String html, Office office, PointOfSale pos)
    {
       this.setCustomer(customer);

       this.setName(name);

       this.setAudit(new Audit(user));

       this.setPriority(0);

       this.setStatus(STATUS.PENDING);

       this.setHtml(html);

       setOffice(office);

       setPointOfSale(pos);

    }

    public ShuttleDirectImportTask( User user, Actor customer, String xml, Office office, PointOfSale pos)
    {
        this("ShuttleDirect", user, customer,xml, office, pos);//guardamos el xml en el campo del html
    }


    @Override
    public void execute(EntityManager em) {
        String result = "";
        int nOk = 0;
        try {

            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new StringReader(this.getHtml()));
            Element root = doc.getRootElement();

            //recorre cada transfer del fichero
            List<Element> ltr = root.getChild("transfers").getChildren();
            String res = "";
            for (Element tr : ltr) {
                try {
                    result += "Ref. " + tr.getChildText("barcode") + ": ";
                    //por cada uno rellena un "transferBookingRequest" y llama a updatebooking()
                    TransferBookingRequest rq = rellenarTransferBookingRequest(tr);
                    res = rq.updateBooking(em);
                    //vamos guardando el resultado junto con la refAge para crear el informe final
                    if (res.length() > 0)//hay errores
                        result += res;
                    else {
                        result += "Ok ";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result += "Error => " + e.getClass() + " - " + e.getMessage();
                }

            }
            this.setStatus(STATUS.OK);//fichero procesado

        } catch (Exception ex) {
            ex.printStackTrace();
            result += "General exception: " + ex.getClass() + " - " + ex.getMessage();
            this.setStatus(STATUS.ERROR);//fichero no procesado
        }

        this.getAudit().touch(em.find(User.class, Constants.IMPORTING_USER_LOGIN));
        this.setReport(result);

      }


    private TransferBookingRequest rellenarTransferBookingRequest(Element tr)
    {
        TransferBookingRequest rq = new TransferBookingRequest();
        rq.setTask(this);
        rq.setCustomer(this.getCustomer());

        rq.setAgencyReference(tr.getChildText("barcode"));
        rq.setCreated(tr.getAttributeValue("bookingdate"));
        rq.setModified(tr.getAttributeValue("datemodified"));

        String type = tr.getChildText("type");
        if (type.toUpperCase().contains("SHUTTLE"))
            rq.setServiceType(TransferBookingRequest.SERVICETYPE.SHUTTLE);
        else
            rq.setServiceType(TransferBookingRequest.SERVICETYPE.PRIVATE);
        rq.setVehicle(type);

        rq.setPassengerName(tr.getChildText("passengername"));
        rq.setPhone(tr.getChildText("phone"));
        rq.setAdults(Integer.parseInt(tr.getChildText("adults")));
        rq.setChildren(Integer.parseInt(tr.getChildText("children")));
        rq.setBabies(Integer.parseInt(tr.getChildText("babies")));

        if (!tr.getChildText("extras").isEmpty())
            rq.setExtras(Integer.parseInt(tr.getChildText("extras")));

        rq.setComments(tr.getChildText("observations"));

        //ARRIVAL o DEPARTURE? En shuttleDirect cada reserva es 1 trayecto (no tenemos Both)
        boolean isArrival=false;
        rq.setTransferType(TransferBookingRequest.TRANSFERTYPE.DEPARTURE);//departure=el aerpuerto esta en el destino
        String origin = tr.getChildText("origin");
        //Buscamos el aeropuerto, formato "???? (IBZ)"
        if (origin.indexOf("(")>0 &&  origin.indexOf(")")>0
                && (origin.indexOf(")")-origin.indexOf("(")==4))
        {
            rq.setTransferType(TransferBookingRequest.TRANSFERTYPE.ARRIVAL);
            isArrival=true;
        }

        if (isArrival)
        {
            rq.setArrivalAirport(tr.getChildText("origin"));
            rq.setArrivalResort(tr.getChildText("destination"));
            rq.setArrivalAddress(tr.getChildText("addressproperty"));
            rq.setArrivalFlightDate(tr.getChildText("arrivaldateflight"));
            rq.setArrivalFlightTime(tr.getChildText("arrivaltimeflight"));
            rq.setArrivalFlightNumber(tr.getChildText("flight"));
            rq.setArrivalFlightCompany(tr.getChildText("airline"));
            rq.setArrivalOriginAirport(tr.getChildText("originairport"));
            //rq.setArrivalComments();
            rq.setArrivalPickupDate(tr.getChildText("datetransfer"));
            rq.setArrivalPickupTime(tr.getChildText("timetransfer"));
        }
        else { //departure
            rq.setDepartureAirport(tr.getChildText("destination"));
            rq.setDepartureResort(tr.getChildText("origin"));
            rq.setDepartureAddress(tr.getChildText("addressproperty"));
            rq.setDepartureFlightDate(tr.getChildText("departuredateflight"));
            rq.setDepartureFlightTime(tr.getChildText("departuretimeflight"));
            rq.setDepartureFlightNumber(tr.getChildText("flight"));
            rq.setDepartureFlightCompany(tr.getChildText("airline"));
            rq.setDepartureDestinationAirport(tr.getChildText("destinationairport"));
           // rq.setDepartureComments(tr.getChildText("observations"));
            rq.setDeparturePickupDate(tr.getChildText("datetransfer"));
            rq.setDeparturePickupTime(tr.getChildText("timetransfer"));
        }

        return rq;
    }

/*
    public String execute2() {
        String[] result = new String[1];
        result[0]="";
        try {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Exception {

                    SAXBuilder builder = new SAXBuilder();
                    Document doc = builder.build(new StringReader(getHtml()));
                    Element root = doc.getRootElement();

                    //recorre cada transfer del fichero
                    List<Element> ltr = root.getChild("transfers").getChildren();
                    String res = "";
                    int nOk=0;
                    for (Element tr:ltr)
                    {
                        try {
                            result[0] += "Ref. " + tr.getChildText("barcode")+": ";
                            //por cada uno rellena un "transferBookingRequest" y llama a updatebooking()
                            TransferBookingRequest rq = rellenarTransferBookingRequest(tr);
                            res=rq.updateBooking();
                            //vamos guardando el resultado junto con la refAge para crear el informe final
                            if (res.length()>0)//hay errores
                                result[0] += res;
                            else {
                                result[0] += "Ok ";
                                nOk ++; //Vamos contando los que han ido bien
                            }
                        } catch (Exception e)
                        {
                            ex.printStackTrace();
                            result[0] += "Error: " + e.getClass() + " - " + e.getMessage();
                        }

                    }
                    //finalmente se actualiza el audit y se cambia el estado a Done y se graba el informe final
                    audit.touch(em,"SISTEMA");
                    if (nOk>0)
                        ShuttleDirectImportTask.this.setStatus(STATUS.OK);
                    else
                        ShuttleDirectImportTask.this.setStatus(STATUS.ERROR);
                    ShuttleDirectImportTask.this.setReport(result[0]);

                }
            });


        }
        catch (Exception ex) {
            ex.printStackTrace();
            result[0] += "General exception: " + ex.getClass() + " - " + ex.getMessage();
        }


        return result[0];
    }
    */
}
