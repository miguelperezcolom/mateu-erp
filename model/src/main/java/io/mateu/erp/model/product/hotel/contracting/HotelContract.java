package io.mateu.erp.model.product.hotel.contracting;

import com.vaadin.data.provider.DataProvider;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.invoicing.WarrantySettlementCharge;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.HotelContractPhoto;
import io.mateu.erp.model.product.hotel.Inventory;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.tour.TourPrice;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;
import io.mateu.mdd.core.interfaces.CalendarLimiter;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class HotelContract extends AbstractContract implements IHotelContract, CalendarLimiter {

    @SearchFilter
    @ManyToOne
    @NotNull@Position(2)
    private Hotel hotel;

    @ManyToOne
    @NotNull@Position(3)
    private Inventory inventory;


    @DependsOn("hotel")
    public DataProvider getInventoryDataProvider() throws Throwable {
        return new JPQLListDataProvider("select x from " + Inventory.class.getName() + " x " + ((getHotel() != null)?" where x.hotel.id = " + getHotel().getId():""));
    }


    @ManyToOne@Position(4)
    private HotelContract parent;

    @Tab("Offers")
    @SearchFilter
    @ManyToMany(mappedBy = "contracts")
    private List<AbstractHotelOffer> offers = new ArrayList<>();

    @Tab("Warranty")
    private boolean warranty;

    @NotNull
    private WarrantySettlementBasis warrantySettlementBasis = WarrantySettlementBasis.NONE;

    private double warrantyPercent;

    private double extrasIncludedInWarranty;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotelContract")
    private List<WarrantySettlementCharge> warrantySettlements = new ArrayList<>();


    @Tab("Terms")
    @DoNotIncludeSeparator
    @Column(name = "terms_hotel")
    @Convert(converter = HotelContractPhotoConverter.class)
    @FullWidth
    private HotelContractPhoto terms;






    @Override
    public Document toXml(EntityManager em) {
        Document doc = super.toXml(em);

        Element xml = doc.getRootElement();

        if (getHotel() != null) {
            Element h;
            xml.addContent(h = new Element("hotel"));
            if (getHotel().getCategoryName() != null) h.setAttribute("category", getHotel().getCategoryName());
            if (getHotel().getName() != null) h.setAttribute("name", getHotel().getName());
            if (getSupplier() != null && getSupplier().getFinancialAgent() != null && getSupplier().getFinancialAgent().getName() != null) h.setAttribute("bussinessName", getSupplier().getFinancialAgent().getBusinessName());
            if (getSupplier() != null && getSupplier().getFinancialAgent() != null && getSupplier().getFinancialAgent().getVatIdentificationNumber() != null) h.setAttribute("vatid", getSupplier().getFinancialAgent().getVatIdentificationNumber());
            if (getHotel().getAddress() != null) h.setAttribute("address", getHotel().getAddress());
            if (getHotel().getTelephone() != null) h.setAttribute("tel", getHotel().getTelephone());
            if (getHotel().getFax() != null) h.setAttribute("fax", getHotel().getFax());

            h.setAttribute("childStartAge", "" + getHotel().getChildStartAge());
            h.setAttribute("juniorStartAge", "" + getHotel().getJuniorStartAge());
            h.setAttribute("adultStartAge", "" + getHotel().getAdultStartAge());
            if (getTerms() != null && getTerms().isYoungestFirst()) h.setAttribute("youngestFirst", "");

        }


        if (getTerms() != null) xml.addContent(getTerms().getXmlForPdf(em, new HashMap<>(), new HashMap<>()));

        return doc;
    }

    @Override
    public String getXslfo(EntityManager em) {
        return AppConfig.get(em).getXslfoForHotelContract();
    }



    public static void main(String... args) throws Throwable {

        System.setProperty("appconf", "/home/miguel/quonext/quoon.properties");

        Helper.loadProperties();


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {


                HotelContract c = em.find(HotelContract.class, 24l);


                if( Desktop.isDesktopSupported() )
                {
                    new Thread(() -> {
                        try {
                            Desktop.getDesktop().browse( c.pdf().toURI() );
                        } catch (IOException | URISyntaxException e1) {
                            e1.printStackTrace();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }).start();
                }


            }
        });


    }

    @Override
    public LocalDate getBegining() {
        return getValidFrom();
    }

    @Override
    public LocalDate getEnding() {
        return getValidTo();
    }
}
