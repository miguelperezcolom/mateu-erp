package io.mateu.erp.model.importing;

/**
 * Created by Antonia on 26/03/2017.
 * OJO, CLASE DEPENDIENTE DE COMO ESTE ESTRUCTURADA LA RESERVA Y LOS SERVICIOS DE TRASLADO
 */
public class TransferBookingRequest {

   private TransferImportTask task;
   private String agencyReference;
   private String bookingCreationDate;
   private String bookingModificationDate;
   private String transferType;//Shuttle, Private, etc
    private String transferVehicle; //Si es un privado (taxi, minibus, etc)

    private enum RESERVATIONTYPE {INBOUND, OUTBOUND, BOTH};
    private RESERVATIONTYPE reservationType;

    //continuar....

    public String validate()
    {
        return "";
    }

    public String save()
    {
        return validate();
    }

    public String toXml()
    {
        return "";
    }

    public static TransferBookingRequest fromXml(String xml)
    {
        return new TransferBookingRequest();
    }
}
