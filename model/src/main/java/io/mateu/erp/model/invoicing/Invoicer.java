package io.mateu.erp.model.invoicing;

import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.partners.Agency;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Invoicer {

    public static List<IssuedInvoice> proform(EntityManager em, User user, List<BookingCharge> charges) throws Throwable {
        return invoice(em, user, charges, true);
    }

    public static List<IssuedInvoice> invoice(EntityManager em, User user, List<BookingCharge> charges) throws Throwable {
        return invoice(em, user, charges, false);
    }


    private static List<IssuedInvoice> invoice(EntityManager em, User user, List<BookingCharge> charges, boolean proforma) throws Throwable {

        // agrupar por clientes

        Map<Agency, List<BookingCharge>> bookingsByPartner = new HashMap<>();
        charges.stream().filter(c -> c.getInvoice() == null).forEach(c -> {
            List<BookingCharge> l = bookingsByPartner.get(c.getAgency());
            if (l == null) {
                bookingsByPartner.put(c.getAgency(), l = new ArrayList<>());
            }
            l.add(c);
        });

        // comprobamos que todos tienen datos de facturación

        for (Agency agency : bookingsByPartner.keySet()) {
            if (agency.getFinancialAgent() == null) throw new Exception("Missing financial agent for " + agency.getName());
        }


        // para cada cliente, agrupar según criterio

        Map<Agency, List<List<BookingCharge>>> chargesByPartner = new HashMap<>();
        bookingsByPartner.keySet().forEach(p -> {

            Map<String, List<BookingCharge>> chargesByKey = new HashMap<>();


            bookingsByPartner.get(p).forEach(c -> {

                String k;
                switch (p.getFinancialAgent().getInvoiceGrouping()) {
                    case BOOKING:
                        k = "" + (c instanceof BookingCharge?((BookingCharge) c).getBooking().getId():"--");
                        break;
                    case FILE:
                        k = "" + (c instanceof BookingCharge? (((BookingCharge) c).getBooking().getFile() != null?"file-" + ((BookingCharge) c).getBooking().getFile().getId():"booking-" + ((BookingCharge) c).getBooking().getId()):"--");
                        break;
                    case DEPARTURE:
                        k = "" + (c.getServiceDate() != null?c.getServiceDate():"--");
                        break;
                    case ALLINONEINVOICE:
                        k = "xx";
                        break;
                    default:
                        k = "zz";
                        break;
                }

                List<BookingCharge> currentCharges = chargesByKey.get(k);
                if (currentCharges == null) {
                    chargesByKey.put(k, currentCharges = new ArrayList<>());
                }
                currentCharges.add(c);

            });


            chargesByPartner.put(p, new ArrayList<>(chargesByKey.values()));

        });


        // para cada cliente, separar por régimen

        Map<Agency, List<List<BookingCharge>>> chargesByPartnerAndRegime = chargesByPartner;

        //todo: separar por régimen

        // para cada reserva....
        // actuamos en nombre propio respecto del viajero? o es facturación directa?
        // incluye transporte y/o hotel?
        // está sujeto a iva o exento?
        // aplicar proporcionalidad


        // crear facturas

        List<IssuedInvoice> invoices = new ArrayList<>();

        chargesByPartnerAndRegime.keySet().forEach(p -> {


            chargesByPartnerAndRegime.get(p).forEach(l -> {

                // creamos factura y la añadimos a la lista

                try {
                    IssuedInvoice i = new IssuedInvoice(user, l, proforma, p.getCompany().getFinancialAgent(), p.getFinancialAgent(), proforma?"PROFORMA":p.getCompany().getBillingSerial().createInvoiceNumber());
                    i.setAgency(p);
                    invoices.add(i);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            });

        });


        return invoices;
    }
}
