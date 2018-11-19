package io.mateu.erp.model.invoicing;


import com.vaadin.data.provider.DataProvider;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.File;
import io.mateu.erp.model.booking.Service;
import io.mateu.mdd.core.annotations.DependsOn;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class BookingCharge extends Charge {


    @ManyToOne
    private File file;

    @ManyToOne
    @NotNull
    private Booking booking;

    public void setBooking(Booking booking) {
        this.booking = booking;
        if (booking != null) setPartner(booking.getAgency());
    }

    @DependsOn("file")
    public DataProvider getBookingDataProvider() throws Throwable {
        return new JPQLListDataProvider(
                "select x from " + File.class.getName() + " y inner join y.bookings x " +
                        ((getFile() != null)?" where y.id = " + getFile().getId():" where y.id = 0"));
    }



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



    public BookingCharge() {
        setType(ChargeType.SALE);
    }

}
