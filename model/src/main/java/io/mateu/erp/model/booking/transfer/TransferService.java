package io.mateu.erp.model.booking.transfer;

import com.google.common.base.Strings;
import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.ServiceType;
import io.mateu.erp.model.booking.hotel.HotelServiceLine;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.transfer.*;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SMSTask;
import io.mateu.erp.model.workflow.SendEmailTask;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by miguel on 25/2/17.
 */
@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class TransferService extends Service {

    @NotNull
    @SearchFilter
    @ListColumn
    @ColumnWidth(150)
    @KPI
    private TransferType transferType;

    @Section("Extras")
    @Output
    private int bigLuggages;

    @SameLine
    @Output
    private int golf;

    @SameLine
    @Output
    private int bikes;

    @SameLine
    @Output
    private int wheelChairs;


    @KPI
    @SearchFilter
    @ListColumn
    @ColumnWidth(150)
    private TransferDirection direction;

    @ManyToOne
    @Position(8)
    private Vehicle preferredVehicle;

    @Section("Pickup")
    @Output
    private String pickupText;
    @ManyToOne
    @Output
    private TransferPoint pickup;

    public void setPickup(TransferPoint pickup) {
        this.pickup = pickup;
        setEffectiveTransferPoints();
    }

    @ManyToOne
    @Output
    @SearchFilter
    private TransferPoint effectivePickup;

    public void setEffectivePickup(TransferPoint p) {
        this.effectivePickup = p;
    }


    @Section("Dropoff")
    @Output
    private String dropoffText;
    @ManyToOne
    @Output
    private TransferPoint dropoff;

    public void setDropoff(TransferPoint dropoff) {
        this.dropoff = dropoff;
        setEffectiveTransferPoints();
    }

    @ManyToOne
    @Output
    @SearchFilter
    private TransferPoint effectiveDropoff;

    public void setEffectiveDropoff(TransferPoint effectiveDropoff) {
        this.effectiveDropoff = effectiveDropoff;
    }

    @Section("Flight")
    @Output
    private String flightNumber;
    @NotNull
    @ListColumn
    @Output
    private LocalDateTime flightTime;
    @Output
    private String flightOriginOrDestination;
    @Output
    private boolean flightChecked;

    @Section("Pickup info")
    @ListColumn
    @Output
    private LocalDateTime pickupTime;

    private LocalDateTime importedPickupTime;

    public void setImportedPickupTime(LocalDateTime importedPickupTime) {
        this.importedPickupTime = importedPickupTime;
        pickupTime = importedPickupTime;
    }

    @Output
    private LocalDateTime pickupConfirmedByTelephone;
    @Output
    private LocalDateTime pickupConfirmedByWeb;

    @Output
    private LocalDateTime pickupConfirmedByEmailToHotel;
    @Output
    private LocalDateTime pickupConfirmedBySMS;

    @Ignored
    private boolean arrivalNoShow;

    @Ignored
    @ManyToOne
    private TransferPoint airport;

    @ManyToOne
    @Ignored
    private TransferService returnTransfer;

    @ManyToMany
    @Ignored
    private List<AbstractTask> tasks = new ArrayList<>();

    /*
    private int bikes;
    private int golfBags;
    */


    public TransferService() {
        setServiceType(ServiceType.TRANSFER);
        setDirection(TransferDirection.POINTTOPOINT);
        setIcons(FontAwesome.BUS.getHtml());
    }


    /*
    public static void price(UserData user, @Selection List<Data> _selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data d : _selection) {
                    TransferService s = em.find(TransferService.class, d.get("_id"));
                    s.price(em, user);
                }
            }
        });
    }
    */


    @PrePersist@PreUpdate
    public void preSet() throws Error {
        if ((getPickupText() == null || "".equals(getPickupText().trim())) && getPickup() == null) throw new Error("Pickup is required");
        if ((getDropoffText() == null || "".equals(getDropoffText().trim())) && getDropoff() == null) throw new Error("Dropoff is required");

        LocalDate s = getFlightTime().toLocalDate();
        if (getFlightTime().getHour() < 6) s = s.minusDays(1);
        setStart(s);
        setFinish(s);
        setAvailable(true);
        super.pre();
    }

    @Override
    protected String getDescription() {
        String d = "" + getTransferType() + " transfer";
        return d;
    }

    public void complete(EntityManager em) throws Throwable {

        System.out.println("transferservice " + getId() + ".complete");

    }

    public void updateDirection() {
        TransferDirection d = TransferDirection.POINTTOPOINT;
        if (getEffectivePickup() != null && getEffectivePickup().isAirport()) {
            d = TransferDirection.INBOUND;
            setAirport(getEffectivePickup());
        }
        else if (getEffectiveDropoff() != null && (TransferPointType.AIRPORT.equals(getEffectiveDropoff().getType()) || TransferPointType.PORT.equals(getEffectiveDropoff().getType()))) {
            d = TransferDirection.OUTBOUND;
            setAirport(getEffectiveDropoff());
        }

        setDirection(d);
    }

    @Override
    public boolean isAllMapped(EntityManager em) {
        return getEffectivePickup() != null && getEffectiveDropoff() != null;
    }

    @Override
    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("leadName", this.getBooking().getLeadName());
            m.put("flightTime", (getFlightTime() != null)?getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")):"");
            m.put("flightNumber", getFlightNumber());
            m.put("flightOriginOrDestination", getFlightOriginOrDestination());
            m.put("pax", getPax());
            m.put("bigLuggages", getBigLuggages());
            m.put("bikes", getBikes());
            m.put("golf", getGolf());
            m.put("wheelChairs", getWheelChairs());
            m.put("pickup", "" + getEffectivePickup());
            m.put("pickupText", getPickupText());
            m.put("dropoff", "" + getEffectiveDropoff());
            m.put("dropoffText", getDropoffText());
            m.put("transferType", getTransferType());
            m.put("preferredVehicle", "" + getPreferredVehicle());
            m.put("preferredProvider", "" + getPreferredProvider());
            if (!TransferType.SHUTTLE.equals(getTransferType())) m.put("pickupTime", getPickupTime());
            m.put("opsComment", "" + getOperationsComment());
            m.put("comment", "" + getBooking().getSpecialRequests());
            //m.put("held", "" + isHeld());
            m.put("cancelled", "" + !isActive());
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public double rateSale(EntityManager em, PrintWriter report) throws Throwable {
        if (!isActive()) return 0;

        // verificamos que tenemos lo que necesitamos para valorar
        if (getEffectivePickup() == null) throw new Throwable("Missing pickup. " + getPickupText() + " is not mapped.");
        if (getEffectiveDropoff() == null) throw new Throwable("Missing dropoff. " + getDropoffText() + " is not mapped.");

        // seleccionamos los contratos válidos
        List<Contract> contracts = new ArrayList<>();
        for (Contract c : (List<Contract>) em.createQuery("select x from " + Contract.class.getName() + " x").setFlushMode(FlushModeType.COMMIT).getResultList()) {
            boolean ok = true;
            ok &= ContractType.SALE.equals(c.getType());
            ok &= c.getAgencies().size() == 0 || c.getAgencies().contains(this.getBooking().getAgency());
            ok &= c.getValidFrom().isBefore(getStart()) || c.getValidFrom().equals(getStart());
            ok &= c.getValidTo().isAfter(getFinish()) || c.getValidTo().equals(getFinish());
            LocalDate created = (getAudit() != null && getAudit().getCreated() != null)?getAudit().getCreated().toLocalDate():LocalDate.now();
            ok &= c.getBookingWindowFrom() == null || c.getBookingWindowFrom().isBefore(created) || c.getBookingWindowFrom().equals(created);
            ok &= c.getBookingWindowTo() == null || c.getBookingWindowTo().isAfter(created) || c.getBookingWindowTo().equals(created);
            if (ok) contracts.add(c);
        }
        if (contracts.size() == 0) throw new Exception("No valid contract");

        List<Contract> propietaryContracts = contracts.stream().filter((c) -> c.getAgencies().size() > 0).collect(Collectors.toList());

        if (propietaryContracts.size() > 0) contracts = propietaryContracts;

        List<Price> prices = new ArrayList<>();
        for (Contract c : contracts) for (Price p : c.getPrices()) {
            boolean ok = true;
            ok &= getTransferType().equals(p.getTransferType());
            ok &= ((p.getOrigin().getResorts().contains(getPickup().getResort()) || p.getOrigin().getPoints().contains(getPickup()))
                    && (p.getDestination().getResorts().contains(getDropoff().getResort()) || p.getDestination().getPoints().contains(getDropoff())))
                    ||
                    ((p.getOrigin().getResorts().contains(getDropoff().getResort()) || p.getOrigin().getPoints().contains(getDropoff()))
                            && (p.getDestination().getResorts().contains(getPickup().getResort()) || p.getDestination().getPoints().contains(getPickup())));
            ok &= p.getVehicle().getMinPax() <= getPax();
            ok &= p.getVehicle().getMaxPax() >= getPax();
            if (ok) prices.add(p);
        }
        if (prices.size() == 0) throw new Exception("No valid price in selectable contracts");

        // valoramos con cada uno de ellos y nos quedamos con el precio más económico
        double value = Double.MAX_VALUE;
        Price bestPrice = null;
        for (Price p : prices) {
            double v = p.getPrice();
            if (PricePer.PAX.equals(p.getPricePer())) {
                int pax = getPax();
                if (p.getContract().getMinPaxPerBooking() > pax) pax = p.getContract().getMinPaxPerBooking();
                v = pax * p.getPrice();
            }
            if (v < value) {
                value = v;
                bestPrice = p;
            }
        }

        if (bestPrice != null) {
            report.print("Used price from " + bestPrice.getOrigin().getName() + " to " + bestPrice.getDestination().getName() + " in " + bestPrice.getVehicle().getName() + " from contract " + bestPrice.getContract().getTitle());
        }

        return value;
    }

    @Override
    public double rateCost(EntityManager em, Provider supplier, PrintWriter report) throws Throwable {

        if (!isActive()) return 0;

        // verificamos que tenemos lo que necesitamos para valorar
        if (getEffectivePickup() == null) throw new Throwable("Missing pickup. " + getPickupText() + " is not mapped.");
        if (getEffectiveDropoff() == null) throw new Throwable("Missing dropoff. " + getDropoffText() + " is not mapped.");

        // seleccionamos los contratos válidos
        List<Contract> contracts = new ArrayList<>();
        for (Contract c : (List<Contract>) em.createQuery("select x from " + Contract.class.getName() + " x").setFlushMode(FlushModeType.COMMIT).getResultList()) {
            boolean ok = true;
            ok &= ContractType.PURCHASE.equals(c.getType());
            ok &= c.getAgencies().size() == 0 || c.getAgencies().contains(this.getBooking().getAgency());
            ok &= supplier == null || supplier.equals(c.getSupplier());
            ok &= c.getValidFrom().isBefore(getStart()) || c.getValidFrom().equals(getStart());
            ok &= c.getValidTo().isAfter(getFinish()) || c.getValidTo().equals(getFinish());
            LocalDate created = (getAudit() != null && getAudit().getCreated() != null)?getAudit().getCreated().toLocalDate():LocalDate.now();
            ok &= c.getBookingWindowFrom() == null || c.getBookingWindowFrom().isBefore(created) || c.getBookingWindowFrom().equals(created);
            ok &= c.getBookingWindowTo() == null || c.getBookingWindowTo().isAfter(created) || c.getBookingWindowTo().equals(created);
            if (ok) contracts.add(c);
        }
        if (contracts.size() == 0) throw new Exception("No valid contract");

        List<Contract> propietaryContracts = contracts.stream().filter((c) -> c.getAgencies().size() > 0).collect(Collectors.toList());

        if (propietaryContracts.size() > 0) contracts = propietaryContracts;

        List<Price> prices = new ArrayList<>();
        for (Contract c : contracts) for (Price p : c.getPrices()) {
            boolean ok = true;
            ok &= getTransferType().equals(p.getTransferType());
            ok &= ((p.getOrigin().getResorts().contains(getEffectivePickup().getResort()) || p.getOrigin().getPoints().contains(getEffectivePickup()))
                    && (p.getDestination().getResorts().contains(getEffectiveDropoff().getResort()) || p.getDestination().getPoints().contains(getEffectiveDropoff())))
                    ||
                    ((p.getOrigin().getResorts().contains(getEffectiveDropoff().getResort()) || p.getOrigin().getPoints().contains(getEffectiveDropoff()))
                            && (p.getDestination().getResorts().contains(getEffectivePickup().getResort()) || p.getDestination().getPoints().contains(getEffectivePickup())));
            ok &= p.getVehicle().getMinPax() <= getPax();
            ok &= p.getVehicle().getMaxPax() >= getPax();
            if (ok) prices.add(p);
        }
        if (prices.size() == 0) throw new Exception("No valid price in selectable contracts");

        // valoramos con cada uno de ellos y nos quedamos con el precio más económico
        double value = Double.MAX_VALUE;
        Price bestPrice = null;
        for (Price p : prices) {
            double v = p.getPrice();
            if (PricePer.PAX.equals(p.getPricePer())) {
                int pax = getPax();
                if (p.getContract().getMinPaxPerBooking() > pax) pax = p.getContract().getMinPaxPerBooking();
                v = pax * p.getPrice();
            }
            if (v < value) {
                value = v;
                bestPrice = p;
            }
        }

        if (bestPrice != null) {
            report.print("Used price from " + bestPrice.getOrigin().getName() + " to " + bestPrice.getDestination().getName() + " in " + bestPrice.getVehicle().getName() + " from contract " + bestPrice.getContract().getTitle());
        }

        return value;
    }

    @Override
    public Provider findBestProvider(EntityManager em) throws Throwable {
        // verificamos que tenemos lo que necesitamos para valorar
        if (getEffectivePickup() == null) throw new Throwable("Missing pickup. " + getPickupText() + " is not mapped.");
        if (getEffectiveDropoff() == null) throw new Throwable("Missing dropoff. " + getDropoffText() + " is not mapped.");

        // seleccionamos los contratos válidos
        List<Contract> contracts = new ArrayList<>();
        for (Contract c : (List<Contract>) em.createQuery("select x from " + Contract.class.getName() + " x").setFlushMode(FlushModeType.COMMIT).getResultList()) {
            boolean ok = true;
            ok &= ContractType.PURCHASE.equals(c.getType());
            ok &= c.getAgencies().size() == 0 || c.getAgencies().contains(this.getBooking().getAgency());
            ok &= !c.getValidFrom().isAfter(getStart());
            ok &= c.getValidTo().isAfter(getFinish());
            LocalDate created = (getAudit() != null && getAudit().getCreated() != null)?getAudit().getCreated().toLocalDate():LocalDate.now();
            ok &= c.getBookingWindowFrom() == null || c.getBookingWindowFrom().isBefore(created) || c.getBookingWindowFrom().equals(created);
            ok &= c.getBookingWindowTo() == null || c.getBookingWindowTo().isAfter(created) || c.getBookingWindowTo().equals(created);
            if (ok) contracts.add(c);
        }
        if (contracts.size() == 0) throw new Exception("No valid contract");

        List<Price> prices = new ArrayList<>();
        for (Contract c : contracts) for (Price p : c.getPrices()) {
            boolean ok = true;
            ok &= getTransferType().equals(p.getTransferType());
            ok &= ((p.getOrigin().getResorts().contains(getEffectivePickup().getResort()) || p.getOrigin().getPoints().contains(getEffectivePickup()))
                    && (p.getDestination().getResorts().contains(getEffectiveDropoff().getResort()) || p.getDestination().getPoints().contains(getEffectiveDropoff())))
                    ||
                    ((p.getOrigin().getResorts().contains(getEffectiveDropoff().getResort()) || p.getOrigin().getPoints().contains(getEffectiveDropoff()))
                            && (p.getDestination().getResorts().contains(getEffectivePickup().getResort()) || p.getDestination().getPoints().contains(getEffectivePickup())));
            ok &= p.getVehicle().getMinPax() <= getPax();
            ok &= p.getVehicle().getMaxPax() >= getPax();
            if (ok) prices.add(p);
        }
        if (prices.size() == 0) throw new Exception("No valid price in selectable contracts");

        // valoramos con cada uno de ellos y nos quedamos con el precio más económico
        double value = Double.MAX_VALUE;
        Provider provider = null;
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

    public Map<String,Object> getData() {
        return getData(false);
    }

    public Map<String,Object> getData(boolean forcePickupTime) {
        Map<String, Object> d = super.getData();

        d.put("id", getId());
        d.put("locator", this.getBooking().getId());
        d.put("leadName", this.getBooking().getLeadName());
        d.put("agency", this.getBooking().getAgency().getName());
        d.put("agencyReference", this.getBooking().getAgencyReference());
        d.put("status", (isActive())?"ACTIVE":"CANCELLED");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        d.put("office", getOffice().getName());
        d.put("pickupConfirmationTelephone", getOffice().getPickupConfirmationTelephone());
        d.put("company", getOffice().getCompany().getName());

        d.put("direction", "" + getDirection());
        d.put("pax", getPax());

        d.put("pickup", "" + getPickup().getName());
        d.put("pickupResort", "" + getPickup().getResort().getName());
        if (getEffectivePickup() != null && !getEffectivePickup().equals(getPickup())) {
            d.put("effectivePickup", "" + getEffectivePickup().getName());
            d.put("effectivePickupResort", "" + getEffectivePickup().getResort().getName());
        }
        d.put("dropoff", "" + getDropoff().getName());
        d.put("dropoffResort", "" + getDropoff().getResort().getName());
        if (getEffectiveDropoff() != null && !getEffectiveDropoff().equals(getDropoff())) {
            d.put("effectiveDropoff", "" + getEffectiveDropoff().getName());
            d.put("effectiveDropoffResort", "" + getEffectiveDropoff().getResort().getName());
        }
        if (getProvider() != null) d.put("providers", getProvider().getName());
        if (forcePickupTime || !TransferType.SHUTTLE.equals(getTransferType()) || (getBooking() instanceof TransferBooking && ((TransferBooking)getBooking()).getOverridePickupTime() != null)) {
            d.put("pickupDate", (getPickupTime() != null)?getPickupTime().format(DateTimeFormatter.ofPattern("E dd MMM")):"");
            d.put("pickupDate_es", (getPickupTime() != null)?getPickupTime().format(DateTimeFormatter.ofPattern("E dd MMM", new Locale("es", "ES"))):"");
            d.put("pickupTime", (getPickupTime() != null)?getPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")):"");
        }
        d.put("transferType", "" + getTransferType());
        d.put("flight", getFlightNumber());
        d.put("flightDate", getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd")));
        d.put("flightDate_es", getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd", new Locale("es", "ES"))));
        d.put("flightTime", getFlightTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        d.put("flightOriginOrDestination", getFlightOriginOrDestination());
        d.put("preferredVehicle", (getPreferredVehicle() != null)?getPreferredVehicle().getName():"");

        return d;
    }

    private void setEffectiveTransferPoints() {


        TransferPoint p = null;
        if (getPickup() != null) p = getPickup();

        TransferPoint d = null;
        if (getDropoff() != null) d = getDropoff();


        /*
        if (getPickup() == null) p = TransferPointMapping.getTransferPoint(em, getPickupText(), this);
        if (getDropoff() == null) d = TransferPointMapping.getTransferPoint(em, getDropoffText(), this);
        */

        if (p != null && p.getAlternatePointForShuttle() != null && (TransferType.SHUTTLE.equals(getTransferType()) || (TransferType.PRIVATE.equals(getTransferType()) && p.isAlternatePointForNonExecutive()))) p = p.getAlternatePointForShuttle();
        if (d != null && d.getAlternatePointForShuttle() != null && (TransferType.SHUTTLE.equals(getTransferType()) || (TransferType.PRIVATE.equals(getTransferType()) && d.isAlternatePointForNonExecutive()))) d = d.getAlternatePointForShuttle();

        setEffectivePickup(p);
        setEffectiveDropoff(d);

        updateDirection();

    }



    public String getSubitle() {
        String s = super.toString();
        TransferService r = null;
        if (this.getBooking() != null) for (Service sv : this.getBooking().getServices()) {
            if (sv.getId() != getId() && sv instanceof TransferService) {
                r = (TransferService) sv;
                break;
            }
        }
        s += ". ";
        if (r != null) {
            if (TransferDirection.OUTBOUND.equals(r.getDirection())) s += "Returns " + r.getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")) + ". ";
            else  s += "Arrives " + r.getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")) + ". ";
        } else s += "This is the only service in this file. ";
        if (isArrivalNoShow()) {
            s += "ArrivalBooking was no show. ";
        }
        return s;
    }

    @Action("Inform pickup time")
    public static void informPickupTimeBatch(UserData user, EntityManager em, Set<TransferService> selection) throws Throwable {
        for (TransferService s : selection) {

            s.informPickupTime(em);

        }
    }


    @Action("Mark as purchased")
    public static void markAsConfirmed(UserData user, EntityManager em, Set<TransferService> selection) throws Throwable {
        for (TransferService s : selection) {

            s.setAlreadyPurchased(true);

        }
    }

    @Action("Inform pickup time")
    public void informPickupTime(EntityManager em) {
        if (getPickupTime() != null) {
            try {
                sendSMS(em);
            } catch (Throwable throwable) {
                System.out.println("" + throwable.getMessage());
            }
            try {
                sendEmailToHotel(em);
            } catch (Throwable throwable) {
                System.out.println("" + throwable.getMessage());
            }
        }
    }

    @Action(value = "Send email to hotel", saveBefore = true)
    public void sendEmailToHotel(EntityManager em) throws Throwable {
        if (getPickupTime() != null) {
            boolean sent = false;

            if (getPickup() != null && !Strings.isNullOrEmpty(getPickup().getEmail())) {
                SendEmailTask t = new SendEmailTask();
                t.setOffice(getOffice());
                t.setAudit(new Audit(MDD.getCurrentUser()));
                t.setCc(getOffice().getEmailCC());
                t.setMessage(Helper.freemark(AppConfig.get(em).getPickupEmailTemplate(), getData(true)));
                t.setSubject("TRANSFER PICKUP INFORMATION FOR " + this.getBooking().getLeadName());
                t.setTo(getPickup().getEmail());
                //t.run(em, em.find(User.class, user.getLogin()));
                getTasks().add(t);
                t.getServices().add(this);
                em.persist(t);
                setPickupConfirmedByEmailToHotel(LocalDateTime.now());
                sent = true;
            }

            if (!Strings.isNullOrEmpty(getBooking().getEmail())) {
                SendEmailTask t = new SendEmailTask();
                t.setOffice(getOffice());
                t.setAudit(new Audit(MDD.getCurrentUser()));
                t.setCc(getOffice().getEmailCC());
                t.setMessage(Helper.freemark(AppConfig.get(em).getPickupEmailTemplate(), getData(true)));
                t.setSubject("TRANSFER PICKUP INFORMATION FOR " + this.getBooking().getLeadName());
                t.setTo(getBooking().getEmail());
                //t.run(em, em.find(User.class, user.getLogin()));
                getTasks().add(t);
                t.getServices().add(this);
                em.persist(t);
                setPickupConfirmedByEmailToHotel(LocalDateTime.now());
                sent = true;
            }

            if (!sent) throw new Exception("No pickup or missing email for it. Please set before sending the email");
        } else throw new Exception("No pickup time. Please set before sending the email");
    }

    @Action(value = "Send SMS", saveBefore = true)
    public void sendSMS(EntityManager em) throws Throwable {
        if (getPickupTime() != null) {
            long tel = 0;
            try {
                tel = Long.parseLong(this.getBooking().getTelephone().replaceAll("[\\(\\)\\+]", ""));
            } catch (Exception e) {

            }
            if (tel > 0 && AppConfig.get(em).isClickatellEnabled() && !Strings.isNullOrEmpty(AppConfig.get(em).getClickatellApiKey())) {
                SMSTask t = new SMSTask(tel, Helper.freemark((("" + tel).startsWith("34"))?AppConfig.get(em).getPickupSmsTemplateEs():AppConfig.get(em).getPickupSmsTemplate(), getData(true)));
                getTasks().add(t);
                t.setAudit(new Audit(MDD.getCurrentUser()));
                //t.run(em, em.find(User.class, user.getLogin()));
                setPickupConfirmedBySMS(LocalDateTime.now());
                em.persist(t);
            } else throw new Exception("No telephone or clickatell api. Please set before sending the sms");
        } else throw new Exception("No pickup time. Please set before sending the sms");
    }

    @Action("Test Email")
    public void testEmail(UserData user, EntityManager em, @Caption("your email") String email) throws Throwable {
        if (getPickupTime() != null) {

            AppConfig appconfig = AppConfig.get(em);

            if (getEffectivePickup() != null) {
                TransferPoint p = getEffectivePickup();
                if (p.getAlternatePointForShuttle() != null && !TransferType.EXECUTIVE.equals(getTransferType()) && (TransferType.SHUTTLE.equals(getTransferType()) || p.isAlternatePointForNonExecutive())) {
                    p = p.getAlternatePointForShuttle();
                }
                SendEmailTask t = new SendEmailTask();
                t.setOffice(getOffice());
                t.setAudit(new Audit(em.find(ERPUser.class, user.getLogin())));
                t.setCc(getOffice().getEmailCC());
                t.setMessage(Helper.freemark(AppConfig.get(em).getPickupEmailTemplate(), getData(true)));
                t.setSubject("TRANSFER PICKUP INFORMATION FOR " + this.getBooking().getLeadName());
                t.setTo(email);
                //t.run(em, em.find(User.class, user.getLogin()));
                getTasks().add(t);
                t.getServices().add(this);
                em.persist(t);
                setPickupConfirmedByEmailToHotel(LocalDateTime.now());
            } else throw new Exception("No effective pickup. Please set before sending the email");
        } else throw new Exception("No pickup time. Please set before sending the email");
    }

    @Action("Test SMS")
    public void testPickupTime(UserData user, EntityManager em, @Caption("mobile nr. (34628...)") String sms) throws Throwable {
        if (getPickupTime() != null) {

            AppConfig appconfig = AppConfig.get(em);

            long tel = 0;
            try {
                tel = Long.parseLong(sms.replaceAll("[\\(\\)\\+]", ""));
            } catch (Exception e) {

            }
            if (tel > 0 && !Strings.isNullOrEmpty(appconfig.getClickatellApiKey())) {
                SMSTask t = new SMSTask(tel, Helper.freemark((("" + tel).startsWith("34"))?AppConfig.get(em).getPickupSmsTemplateEs():AppConfig.get(em).getPickupEmailTemplate(), getData(true)));
                getTasks().add(t);
                t.getServices().add(this);
                t.setAudit(new Audit(em.find(ERPUser.class, user.getLogin())));
                //t.run(em, em.find(User.class, user.getLogin()));
                em.persist(t);
            } else throw new Exception("No telephone or clickatell api. Please set before sending the sms");
        } else throw new Exception("No pickup time. Please set before sending the sms");
    }

    @Override
    public BillingConcept getBillingConcept(EntityManager em) {
        return AppConfig.get(em).getBillingConceptForTransfer();
    }


    @Override
    public Element toXml() {
        Element xml = super.toXml();

        xml.setAttribute("type", "transfer");

        xml.setAttribute("header", "" + getTransferType().name() + " " + "" + getDirection().name());

        xml.setAttribute("pickup", getPickup().getName());
        xml.setAttribute("dropoff", getDropoff().getName());

        xml.setAttribute("flightDate", getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        xml.setAttribute("flightTime", getFlightTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        if (getFlightNumber() != null) xml.setAttribute("flightNumber", getFlightNumber());
        if (getFlightOriginOrDestination() != null) xml.setAttribute("flightOriginOrDestination", getFlightOriginOrDestination());
        if (isFlightChecked()) xml.setAttribute("flightChecked", "");

        xml.setAttribute("pax", "" + getPax());

        return xml;
    }


    @Override
    public String getChargeSubject() {
        String d = "Inbound";
        if (TransferDirection.OUTBOUND.equals(getDirection())) d = "Outbound";
        else if (TransferDirection.POINTTOPOINT.equals(getDirection())) d = "Point to point";
        return d + " transfer from " + getPickup().getName() + " to " + getDropoff().getName() + (getPreferredVehicle() != null?" in " + getPreferredVehicle().getName():"") + " for " + getPax() + " pax";
    }


    @Override
    public void addInstructions(Element xml) {
        super.addInstructions(xml);
        if (TransferDirection.OUTBOUND.equals(getDirection())) {
            String txt = getDropoff().getDepartureInstructions() != null?getDropoff().getDepartureInstructions().get(getBooking().getLanguage().name()):"";
            if (!Strings.isNullOrEmpty(txt)) xml.addContent(new Element("instructions").setText(txt));
        } else if (TransferType.SHUTTLE.equals(getTransferType())) {
            String txt = getPickup().getArrivalInstructionsForShuttle() != null?getPickup().getArrivalInstructionsForShuttle().get(getBooking().getLanguage().name()):"";
            if (!Strings.isNullOrEmpty(txt)) xml.addContent(new Element("instructions").setText(txt));
        } else if (TransferType.EXECUTIVE.equals(getTransferType())) {
            String txt = getPickup().getArrivalInstructionsForExecutive() != null?getPickup().getArrivalInstructionsForExecutive().get(getBooking().getLanguage().name()):"";
            if (!Strings.isNullOrEmpty(txt)) xml.addContent(new Element("instructions").setText(txt));
        } else {
            String txt = getPickup().getArrivalInstructionsForPrivate() != null?getPickup().getArrivalInstructionsForPrivate().get(getBooking().getLanguage().name()):"";
            if (!Strings.isNullOrEmpty(txt)) xml.addContent(new Element("instructions").setText(txt));
        }
    }
}
