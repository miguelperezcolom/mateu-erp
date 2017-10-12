package io.mateu.erp.model.product.hotel.contracting;

import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.HotelContractPhoto;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class HotelContract extends AbstractContract implements IHotelContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Tab("Related to")
    @ManyToMany
    private List<Hotel> hotels = new ArrayList<>();

    @ManyToMany(mappedBy = "contracts")
    private List<AbstractHotelOffer> offers = new ArrayList<>();

    @Tab("Terms")
    @Column(name = "terms2")
    @Convert(converter = HotelContractPhotoConverter.class)
    private HotelContractPhoto terms;

}
