package io.mateu.erp.client.operations;

import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.product.ProductType;
import io.mateu.mdd.core.interfaces.AbstractJPQLListView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;

@Getter@Setter
public class GenericServiceCalendar extends AbstractJPQLListView<GenericServiceCalendar.Row> {


    private ProductType productType;


    @Getter@Setter
    public class Row {

        private LocalDate date;

        private int pax;

    }

    @Override
    public Query buildQuery(EntityManager em, boolean forCount) throws Throwable {
        return em.createQuery("select s.start, sum(s.pax) from " + HotelService.class.getName() + " s group by s.start order by s.start");
    }

}


