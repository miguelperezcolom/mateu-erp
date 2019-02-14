package io.mateu.erp.model.invoicing;


import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.financials.Amount;
import io.mateu.mdd.core.annotations.KPI;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class BookingCharge extends Charge {


    @ManyToOne
    @NotNull
    private Booking booking;

    @KPI
    private boolean extra = false;

    public void setBooking(Booking booking) {
        this.booking = booking;
        if (booking != null) setPartner(booking.getAgency());
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
    public void setTotal(Amount total) {
        super.setTotal(total);
        if (booking != null) booking.setUpdatePending(true);
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
        if (booking != null) booking.setUpdatePending(true);
    }
}
