package io.mateu.erp.model.product.hotel;

import com.google.common.base.Strings;
import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IStopSaleLine;
import io.mateu.erp.model.caval.CAVALClient;
import io.mateu.erp.model.caval.CavalIdDataProvider;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.erp.model.product.DataSheet;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.multilanguage.Literal;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class Hotel extends AbstractProduct implements IHotel {

    private String lat;

    private String lon;

    private String address;

    private String zip;

    private String telephone;

    private String fax;

    private String email;

    @ManyToOne
    @NotNull
    @ListColumn
    @ColumnWidth(150)
    private HotelType hotelType;


    @ManyToOne
    @NotNull
    @ListColumn
    @ColumnWidth(150)
    private HotelCategory category;

    @ManyToOne
    private Partner hotelChain;


    /**
     * inclusive
     */
    private int childStartAge;

    /**
     * inclusive
     */
    private int juniorStartAge;

    /**
     * inclusive
     */
    private int adultStartAge;


    private boolean youngestFirst;


    @ManyToOne
    private TransferPoint transferPoint;


    @ManyToOne(cascade = CascadeType.ALL)
    @TextArea
    @Html
    private Literal importantInfo;


    private String idFromCaval;



    @Section("Related")
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<Board> boards = new ArrayList<>();


    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<HotelExtra> extras = new ArrayList<>();


    @ManyToOne(cascade = CascadeType.ALL)
    @Output
    private StopSales stopSales;

    @OneToMany(mappedBy = "hotel")
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<Inventory> inventories = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @Output
    private Inventory realInventory;

    @OneToMany(mappedBy = "hotel")
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<HotelContract> contracts = new ArrayList<>();

    @OneToMany(mappedBy = "hotel")
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<AbstractHotelOffer> offers = new ArrayList<>();




    @Override
    public String getCategoryId() {
        return (getCategory() != null)?getCategory().getCode():null;
    }

    @Override
    public String getCategoryName() {
        return (getCategory() != null)?getCategory().getName().getEs():null;
    }

    @Override
    public List<? extends IStopSaleLine> getStopSalesLines() {
        return getStopSales().getLines();
    }


    @PostPersist@PostUpdate
    public void afterSet() throws Exception, Throwable {

        if (getStopSales() == null) {
            WorkflowEngine.add((() -> {
                try {
                    Helper.transact(em -> {
                        Hotel h = em.merge(this);
                        h.setStopSales(new StopSales());
                        h.getStopSales().setHotel(this);
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }));
        }

    }


    @Action(order = 1)
    public URL showInGoogleMaps() throws MalformedURLException {
        if (!Strings.isNullOrEmpty(getLat()) && !Strings.isNullOrEmpty(getLon())) {
            // http://www.google.com/maps/place/lat,lng
            // http://maps.google.com/maps?q=24.197611,120.780512
            return new URL("http://www.google.com/maps/place/" + getLat() + "," + getLon());
        } else throw new Error("Latitude and longitude mut not be empty. Please fill and try again");
    }

    @Action(order = 2)
    public void importCavalData(EntityManager em, @DataProvider(dataProvider = CavalIdDataProvider.class)@NotNull String idAtCaval) throws IOException {
        System.out.println("hola!!!" + idAtCaval);
        if (!Strings.isNullOrEmpty(idAtCaval)) {

            setIdFromCaval(idAtCaval);

            if (getDataSheet() == null) {
                setDataSheet(new DataSheet());
            }
            CAVALClient.get().updateDataSheet(em, idAtCaval, getDataSheet());
        }
    }


}
