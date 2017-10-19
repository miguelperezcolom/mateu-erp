package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.financials.Actor;
import io.mateu.ui.mdd.server.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "HotelStopSales")
@Getter
@Setter
public class StopSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Hotel hotel;

    @Ignored
    @OneToMany(mappedBy = "stopSales", cascade = CascadeType.ALL)
    private List<StopSalesLine> lines = new ArrayList<>();


    @Ignored
    @OneToMany(mappedBy = "stopSales")
    private List<StopSalesOperation> operations = new ArrayList<>();


    public void build(EntityManager em) {

        //creamos la estructura

        StopSalesCube cube = new StopSalesCube(this);


        // grabamos la estructura

        cube.save(em);

    }

}
