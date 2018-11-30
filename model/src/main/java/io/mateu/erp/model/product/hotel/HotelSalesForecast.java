package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.annotations.Position;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter@Setter
public class HotelSalesForecast {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private HotelContract hotelContract;

    @NotNull
    private LocalDate start;

    @NotNull
    @Column(name = "_end")
    private LocalDate end;

    private int overnights;

}
