package io.mateu.erp.model.importing;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.model.util.Constants;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

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
        this.setAdditions(0);
        this.setCancellations(0);
        this.setModifications(0);
        this.setUnmodified(0);
        this.setErrors(0);
        this.setTotal(0);

        try {

            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new StringReader(this.getHtml()));
            Element root = doc.getRootElement();

            //recorre cada transfer del fichero
            List<Element> ltr = root.getChild("transfers").getChildren();
            String res = "";
            String aux = "";
            for (Element tr : ltr) {
                aux = "";
                this.increaseTotal();
                try {
                    aux = "\nRef. " + tr.getChildText("barcode") + ": ";
                    //por cada uno rellena un "transferBookingRequest" y llama a updatebooking()
                    TransferBookingRequest rq = rellenarTransferBookingRequest(tr);
                    res = rq.updateBooking(em);
                    //vamos guardando el resultado junto con la refAge para crear el informe final
                    if (res.length() > 0)//hay errores
                        result += aux + res;
                    /*else {
                        result += "Ok ";
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                    result += aux + "Error => " + e.getClass() + " - " + e.getMessage();
                }

            }
            this.setStatus(STATUS.OK);//fichero procesado

        } catch (Exception ex) {
            ex.printStackTrace();
            result += "General exception: " + ex.getClass() + " - " + ex.getMessage();
            this.setStatus(STATUS.ERROR);//fichero no procesado
        }

        this.getAudit().touch(em.find(User.class, Constants.IMPORTING_USER_LOGIN));
        this.setReport(result.replaceFirst("\n",""));

      }


    private TransferBookingRequest rellenarTransferBookingRequest(Element tr)
    {
        TransferBookingRequest rq = new TransferBookingRequest();
        rq.setTask(this);
        rq.setSource(new XMLOutputter(Format.getCompactFormat()).outputString(tr));
        rq.setCustomer(this.getCustomer());

        rq.setAgencyReference(tr.getChildText("barcode"));
        rq.setCreated(tr.getAttributeValue("bookingdate"));
        rq.setModified(tr.getAttributeValue("datemodified"));

        String type = tr.getChildText("type");
        if (type.toUpperCase().contains("SHUTTLE"))
            rq.setServiceType(TransferType.SHUTTLE);
        else if (type.toUpperCase().contains("EXECUTIVE"))
            rq.setServiceType(TransferType.EXECUTIVE);
        else
            rq.setServiceType(TransferType.PRIVATE);
        rq.setVehicle(type);

        rq.setPassengerName(tr.getChildText("passengername"));
        rq.setPhone(tr.getChildText("phone"));
        rq.setAdults(Integer.parseInt(tr.getChildText("adults")));
        rq.setChildren(Integer.parseInt(tr.getChildText("children")));
        rq.setBabies(Integer.parseInt(tr.getChildText("babies")));



        String extras = "";
        if (!tr.getChildText("extras").isEmpty())
            //rq.setExtras(Integer.parseInt(tr.getChildText("extras")));
            extras = tr.getChildText("extras");
        if (!"".equals(extras)) extras += " ";
        //rq.setComments(tr.getChildText("observations"));

        //ARRIVAL o DEPARTURE? En shuttleDirect cada reserva es 1 trayecto (no tenemos Both)
        boolean isArrival=false;
        rq.setTransferServices(TransferBookingRequest.TRANSFERSERVICES.DEPARTURE);//departure=el aerpuerto esta en el destino
        String origin = tr.getChildText("origin");
        //Buscamos el aeropuerto, formato "???? (IBZ)"
        if (origin.indexOf("(")>0 &&  origin.indexOf(")")>0
                && (origin.indexOf(")")-origin.indexOf("(")==4))
        {
            rq.setTransferServices(TransferBookingRequest.TRANSFERSERVICES.ARRIVAL);
            isArrival=true;
        }

        if (isArrival)
        {
            TransferBookingRequest.STATUS s =  TransferBookingRequest.STATUS.OK;
            if (tr.getChildText("status").toUpperCase().equals("CANCELLATION"))
                s= TransferBookingRequest.STATUS.CANCELLED;
            rq.setArrivalStatus(s);

            if (tr.getChildText("confirmationcode")!=null && !tr.getChildText("confirmationcode").isEmpty())
                rq.setArrivalConfirmed(true);

            rq.setArrivalAirport(tr.getChildText("origin"));
            rq.setArrivalResort(tr.getChildText("destination"));
            rq.setArrivalAddress(tr.getChildText("addressproperty"));
            rq.setArrivalFlightDate(tr.getChildText("arrivaldateflight"));
            rq.setArrivalFlightTime(tr.getChildText("arrivaltimeflight"));
            rq.setArrivalFlightNumber(tr.getChildText("flight"));
            rq.setArrivalFlightCompany(tr.getChildText("airline"));
            rq.setArrivalOriginAirport(tr.getChildText("originairport"));
            rq.setArrivalComments(extras + tr.getChildText("observations"));
            rq.setArrivalPickupDate(tr.getChildText("datetransfer"));
            rq.setArrivalPickupTime(tr.getChildText("timetransfer"));
        }
        else { //departure
            TransferBookingRequest.STATUS s =  TransferBookingRequest.STATUS.OK;
            if (tr.getChildText("status").toUpperCase().equals("CANCELLATION"))
                s= TransferBookingRequest.STATUS.CANCELLED;
            rq.setDepartureStatus(s);

            if (tr.getChildText("confirmationcode")!=null && !tr.getChildText("confirmationcode").isEmpty())
                rq.setDepartureConfirmed(true);

            rq.setDepartureAirport(tr.getChildText("destination"));
            rq.setDepartureResort(tr.getChildText("origin"));
            rq.setDepartureAddress(tr.getChildText("addressproperty"));
            rq.setDepartureFlightDate(tr.getChildText("departuredateflight"));
            rq.setDepartureFlightTime(tr.getChildText("departuretimeflight"));
            rq.setDepartureFlightNumber(tr.getChildText("flight"));
            rq.setDepartureFlightCompany(tr.getChildText("airline"));
            rq.setDepartureDestinationAirport(tr.getChildText("destinationairport"));
            rq.setDepartureComments(extras + tr.getChildText("observations"));
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
