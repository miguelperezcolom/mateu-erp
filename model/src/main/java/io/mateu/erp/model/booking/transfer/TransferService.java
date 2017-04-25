package io.mateu.erp.model.booking.transfer;

import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.booking.*;
import io.mateu.erp.model.financials.Actor;
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
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @SearchFilter
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
    @Required
    @ListColumn(order = true)
    private LocalDateTime flightTime;
    private String flightOriginOrDestination;

    @StartsLine
    @ListColumn
    private LocalDateTime pickupTime;
    private LocalDateTime pickupConfirmed;
    private PickupConfirmationWay pickupConfirmedThrough;

    private boolean arrivalNoShowed;

    @Ignored
    @ManyToOne
    private TransferPoint airport;

    @ManyToOne
    @Ignored
    private TransferService returnTransfer;


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

    @Action(name = "Open return")
    public MDDLink openReturn() throws Exception {
        TransferService r = null;
        for (Service s : getBooking().getServices()) {
            if (s.getId() != getId() && s instanceof TransferService) {
                r = (TransferService) s;
                break;
            }
        }
        if (r != null) return new MDDLink(TransferService.class, ActionType.OPENEDITOR, new Data("_id", r.getId()));
        else throw new Exception("This is the only service in this booking");
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
    public void afterSet(EntityManager em, boolean isNew) throws Throwable {

        setProcessingStatus(ProcessingStatus.INITIAL);

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


        mapTransferPoints(em);

        TransferDirection d = TransferDirection.POINTTOPOINT;
        if (getEffectivePickup() != null && (TransferPointType.AIRPORT.equals(getEffectivePickup().getType()) || TransferPointType.PORT.equals(getEffectivePickup().getType()))) {
            d = TransferDirection.INBOUND;
            setAirport(getEffectivePickup());
        }
        else if (getEffectiveDropoff() != null && (TransferPointType.AIRPORT.equals(getEffectiveDropoff().getType()) || TransferPointType.PORT.equals(getEffectiveDropoff().getType()))) {
            d = TransferDirection.OUTBOUND;
            setAirport(getEffectiveDropoff());
        }
        if (getAirport() == null && getOffice() != null) {
            setAirport(getOffice().getDefaultAirportForTransfers());
        }

        setDirection(d);

        if (getEffectivePickup() != null && getEffectiveDropoff() != null) setProcessingStatus(ProcessingStatus.DATA_OK);

        price(em);

        try {
            checkPurchase(em);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Override
    public void beforeDelete(EntityManager em) {

    }

    @Override
    public void afterDelete(EntityManager em) {

    }

    @Override
    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("leadName", getBooking().getLeadName());
            m.put("flighttime", getFlightTime());
            m.put("flightnumber", getFlightNumber());
            m.put("pax", getPax());
            m.put("pickup", "" + getEffectivePickup());
            m.put("dropoff", "" + getEffectiveDropoff());
            m.put("transfertype", getTransferType());
            m.put("preferredvehicle", "" + getPreferredVehicle());
            m.put("comment", "" + getComment());
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public double rate(EntityManager em) throws Throwable {

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

    @Override
    public void generatePurchaseOrders(EntityManager em) throws Throwable {

        // si ya existe entonces

        // comprobar desglose

        // para cada purchaseOrder comprobar firma y actualizar estado si es necesario

        // puede que haya que eliminar purchaseOrders que sobren (o cancelarlas si ya se han enviado)

        // si no...

        // si es shuttle entonces buscar alguna petición que encaje

        // si no hay ninguna petición que podamos utilizar entonces crear una nueva

        // ...fin si no

        Actor provider = (getPreferredProvider() != null)?getPreferredProvider():findBestProvider(em);
        if (provider == null) throw new Throwable("Preferred provider needed for service " + getId());
        if (isHeld()) throw new Throwable("Service " + getId() + " is held");
        PurchaseOrder po = null;
        if (getPurchaseOrders().size() > 0) {
            po = getPurchaseOrders().get(getPurchaseOrders().size() - 1);
            if (!provider.equals(po.getProvider())) {
                po.cancel(em); // todo: controlar si es el único servicio en la purchase order
                po = null;
            }
        }
        if (po == null) {
            po = new PurchaseOrder();
            em.persist(po);
            po.setAudit(new Audit());
            po.getServices().add(this);
            getPurchaseOrders().add(po);
            po.setStatus(PurchaseOrderStatus.PENDING);
        }
        po.setOffice(getOffice());
        po.setProvider(provider);
        po.price(em);
        po.updateLinesFromServices(em);
    }

    private Actor findBestProvider(EntityManager em) throws Throwable {
        // verificamos que tenemos lo que necesitamos para valorar

        mapTransferPoints(em);

        if (getEffectivePickup() == null) throw new Throwable("Missing pickup. " + getPickupText() + " is not mapped.");
        if (getEffectiveDropoff() == null) throw new Throwable("Missing dropoff. " + getDropoffText() + " is not mapped.");

        // seleccionamos los contratos válidos
        List<Contract> contracts = new ArrayList<>();
        for (Contract c : (List<Contract>) em.createQuery("select x from " + Contract.class.getName() + " x").getResultList()) {
            boolean ok = true;
            ok &= ContractType.PURCHASE.equals(c.getType());
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
        Actor provider = null;
        for (Price p : prices) {
            double v = p.getPrice();
            if (PricePer.PAX.equals(p.getPricePer())) v = getPax() * p.getPrice();
            if (v < value) {
                value = v;
                provider = p.getContract().getSupplier();
            }
        }

        return provider;
    }

    @Override
    public List<PurchaseOrderLine> toPurchaseLines(EntityManager em) {
        List<PurchaseOrderLine> ls = new ArrayList<>();
        PurchaseOrderLine l;
        ls.add(l = new PurchaseOrderLine());
        l.setAction(PurchaseOrderLineAction.ADD);
        String d = "";
        d += " " + getDirection();
        d += " " + getTransferType();
        if (getPreferredVehicle() != null) d += " in " + getPreferredVehicle().getName();
        d += " for " + getPax() + " pax";
        d += " from " + getEffectivePickup().getName();
        d += " to " + getEffectiveDropoff().getName();
        d += " flight " + getFlightNumber();
        d += " arrival/departure " + getFlightTime().format(DateTimeFormatter.BASIC_ISO_DATE);
        d += " to/from " + getFlightOriginOrDestination();
        d += " (" + getComment() + ")";
        if (d.startsWith(" ")) d = d.substring(1);
        l.setDescription(d);
        l.setUnits(1);
        ls.add(l);
        return ls;
    }

    private void mapTransferPoints(EntityManager em) {
        if (getPickup() == null) setEffectivePickup(TransferPointMapping.getTransferPoint(em, getPickupText()));
        if (getDropoff() == null) setEffectiveDropoff(TransferPointMapping.getTransferPoint(em, getDropoffText()));
    }



    @Subtitle
    public String getSubitle() {
        String s = super.toString();
        TransferService r = null;
        for (Service sv : getBooking().getServices()) {
            if (sv.getId() != getId() && sv instanceof TransferService) {
                r = (TransferService) sv;
                break;
            }
        }
        if (r != null) {
            if (TransferDirection.OUTBOUND.equals(r.getDirection())) s += ". Returns " + r.getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
            else  s += ". Arrives " + r.getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        } else s += ". This is the only service in this booking";
        return s;
    }



}
