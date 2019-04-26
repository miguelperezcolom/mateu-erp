package io.mateu.erp.model.invoicing;


import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.organization.PointOfSaleSettlement;
import io.mateu.erp.model.partners.Agency;
import io.mateu.mdd.core.annotations.KPI;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter@Setter
public class BookingCharge extends Charge {


    @ManyToOne
    @NotNull
    private Agency agency;


    @ManyToOne
    @NotNull
    private Booking booking;

    @KPI
    private boolean extra = false;

    @ManyToOne@Output
    private PointOfSaleSettlement pointOfSaleSettlement;


    public void setBooking(Booking booking) {
        this.booking = booking;
        if (booking != null) setAgency(booking.getAgency());
    }

    /*
    @ManyToOne
    private Service service;

    public void setService(Service service) {
        this.service = service;
        if (service != null) setOffice(service.getOffice());
    }


    @DependsOn("booking")
    public DataProvider getServiceDataProvider() throws Throwable {
        return new JPQLListDataProvider(
                "select x from " + Booking.class.getName() + " y inner join y.services x " +
                        ((getBooking() != null)?" where y.id = " + getBooking().getId():" where y.id = 0"));
    }
    */




    @Override
    public void setTotal(double total) {
        super.setTotal(total);
        if (booking != null) booking.setUpdateRqTime(LocalDateTime.now());
    }

    public BookingCharge() {
        setType(ChargeType.SALE);
    }

    public BookingCharge(@NotNull Booking booking) {
        this();
        this.booking = booking;
    }

    @Override
    public void totalChanged() {
        super.totalChanged();
        if (booking != null) booking.setUpdateRqTime(LocalDateTime.now());
    }

}
