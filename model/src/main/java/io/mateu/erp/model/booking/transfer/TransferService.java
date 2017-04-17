package io.mateu.erp.model.booking.transfer;

import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.transfer.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.ERPServiceImpl;
import io.mateu.ui.mdd.server.JPAController;
import io.mateu.ui.mdd.server.JPAServerSideEditorViewController;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.apache.fop.fonts.type1.AdobeStandardEncoding.c;

/**
 * Created by miguel on 25/2/17.
 */
@Entity
@Getter
@Setter
public class TransferService extends Service implements WithTriggers {

    @StartsLine
    @Required
    @SearchFilter
    @ListColumn
    private TransferType transferType;

    @Required
    @ListColumn
    private int pax;

    @Output
    @ListColumn
    private TransferDirection direction;

    @StartsLine
    private String pickupText;
    @ManyToOne
    private TransferPoint pickup;
    @ManyToOne
    @Output
    private TransferPoint effectivePickup;


    @StartsLine
    private String dropoffText;
    @ManyToOne
    private TransferPoint dropoff;
    @ManyToOne
    @Output
    private TransferPoint effectiveDropoff;

    @StartsLine
    @ManyToOne
    private Vehicle preferredVehicle;

    @StartsLine
    private String flightNumber;
    private LocalDateTime flightTime;
    private String flightOriginOrDestination;

    @StartsLine
    private LocalDateTime pickupTime;
    private LocalDateTime pickupConfirmed;
    private PickupConfirmationWay pickupConfirmedThrough;



    /*
    private int bikes;
    private int golfBags;
    */

    @Action(name = "Save and return")
    public Data saveAndReturn(Data _data) throws Throwable {
        ERPServiceImpl s = new ERPServiceImpl();
        Data data = (Data) s.set("", TransferService.class.getName(), _data);
        Data aux = data.get("pickupText");
        data.set("pickupText", data.get("dropoffText"));
        data.set("dropoffText", aux);
        aux = data.get("pickup");
        data.set("pickup", data.get("dropoff"));
        data.set("dropoff", aux);
        data.remover("flightNumber");
        data.remover("flightTime");
        data.remover("flightOriginOrDestination");
        data.remover("pickupTime");
        data.remover("pickupConfirmed");
        data.remover("pickupConfirmedThrough");
        data.remover("_id");
        return data;
    }

    @Action(name = "Price")
    public static void price(@Selection List<Data> _selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data d : _selection) {
                    TransferService s = em.find(TransferService.class, d.get("_id"));
                    s.price(em);
                }
            }
        });
    }

    @Action(name = "Repair")
    public static void repair(@Selection List<Data> _selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data d : _selection) {
                    TransferService s = em.find(TransferService.class, d.get("_id"));
                    s.afterSet(em, false);
                    s.price(em);
                }
            }
        });
    }


    @Override
    public void beforeSet(EntityManager em, boolean isNew) {

    }

    @Override
    public void afterSet(EntityManager em, boolean isNew) throws Exception {
        if ((getPickupText() == null || "".equals(getPickupText().trim())) && getPickup() == null) throw new Exception("Pickup is required");
        if ((getDropoffText() == null || "".equals(getDropoffText().trim())) && getDropoff() == null) throw new Exception("Dropoff is required");

        setStart(getFlightTime().toLocalDate());
        setFinish(getFlightTime().toLocalDate());

        TransferPoint p = null;
        if (getPickup() != null) p = getPickup();
        setEffectivePickup(p);

        p = null;
        if (getDropoff() != null) p = getDropoff();
        setEffectiveDropoff(p);


        TransferDirection d = TransferDirection.POINTTOPOINT;
        if (getEffectivePickup() != null && (TransferPointType.AIRPORT.equals(getEffectivePickup().getType()) || TransferPointType.PORT.equals(getEffectivePickup().getType()))) d = TransferDirection.INBOUND;
        else if (getEffectiveDropoff() != null && (TransferPointType.AIRPORT.equals(getEffectiveDropoff().getType()) || TransferPointType.PORT.equals(getEffectiveDropoff().getType()))) d = TransferDirection.OUTBOUND;

        setDirection(d);

        price(em);


    }

    @Override
    public void beforeDelete(EntityManager em) {

    }

    @Override
    public void afterDelete(EntityManager em) {

    }

    @Override
    protected double rate(EntityManager em) throws Throwable {

        // verificamos que tenemos lo que necesitamos para valorar

        mapTransferPoints(em);

        if (getEffectivePickup() == null) throw new Throwable("Missing pickup. " + getPickupText() + " is not mapped.");
        if (getEffectiveDropoff() == null) throw new Throwable("Missing dropoff. " + getDropoffText() + " is not mapped.");

        // seleccionamos los contratos válidos
        List<Contract> contracts = new ArrayList<>();
        for (Contract c : (List<Contract>) em.createQuery("select x from " + Contract.class.getName() + " x").getResultList()) {
            boolean ok = true;
            ok &= ContractType.SALE.equals(c.getType());
            ok &= c.getTargets().size() == 0 || c.getTargets().contains(getBooking().getAgency());
            ok &= getTransferType().equals(c.getTransferType());
            ok &= c.getValidFrom().isBefore(getStart());
            ok &= c.getValidTo().isAfter(getFinish());
            if (ok) contracts.add(c);
        }
        if (contracts.size() == 0) throw new Exception("No valid contract");

        List<Price> prices = new ArrayList<>();
        for (Contract c : contracts) for (Price p : c.getPrices()) {
            boolean ok = true;
            ok &= ((p.getOrigin().getCities().contains(getEffectivePickup().getCity()) || p.getOrigin().getPoints().contains(getEffectivePickup()))
                    && (p.getDestination().getCities().contains(getEffectiveDropoff().getCity()) || p.getDestination().getPoints().contains(getEffectiveDropoff())))
                    ||
                    ((p.getOrigin().getCities().contains(getEffectiveDropoff().getCity()) || p.getOrigin().getPoints().contains(getEffectiveDropoff()))
                            && (p.getDestination().getCities().contains(getEffectivePickup().getCity()) || p.getDestination().getPoints().contains(getEffectivePickup())));
            ok &= p.getVehicle().getMinPax() <= getPax();
            ok &= p.getVehicle().getMaxPax() >= getPax();
            if (ok) prices.add(p);
        }
        if (prices.size() == 0) throw new Exception("No valid price in selectable contracts");

        // valoramos con cada uno de ellos y nos quedamos con el precio más económico
        double value = Double.MAX_VALUE;
        for (Price p : prices) {
            double v = p.getPrice();
            if (PricePer.PAX.equals(p.getPricePer())) v = getPax() * p.getPrice();
            if (v < value) value = v;
        }

        return value;
    }

    private void mapTransferPoints(EntityManager em) {
        if (getEffectivePickup() == null) setEffectivePickup(TransferPointMapping.getTransferPoint(em, getPickupText()));
        if (getEffectiveDropoff() == null) setEffectiveDropoff(TransferPointMapping.getTransferPoint(em, getDropoffText()));
    }



    @Subtitle
    public String getSubitle() {
        return super.toString();
    }
}
