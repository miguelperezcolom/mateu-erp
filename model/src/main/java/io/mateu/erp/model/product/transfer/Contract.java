package io.mateu.erp.model.product.transfer;

import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.erp.model.world.Resort;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import java.util.*;

/**
 * Created by miguel on 31/1/17.
 */
@Entity(name = "TransferContract")
@Getter
@Setter
@QLForCombo(ql = "select x.id, x.title from io.mateu.erp.model.product.transfer.Contract x order by x.title")
public class Contract extends AbstractContract {

    @Tab("Transfers")
    private int minPaxPerBooking;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    @Ignored
    private List<Price> prices = new ArrayList<>();


    @Action
    public static void cloneContracts(EntityManager em, UserData user, Set<Contract> selection, @Caption("% increment") double percent, @Caption("Value increment") double amount) throws CloneNotSupportedException {
        ERPUser u = em.find(ERPUser.class, user.getLogin());
        for (Contract c0 : selection) {
            Contract c1 = c0.clone(em, u);
            c1.increment(percent, amount);
            em.persist(c1);
        }
    }


    public Contract clone(EntityManager em, ERPUser u) {
        Contract c = new Contract();
        c.setAudit(new Audit());
        c.setBillingConcept(getBillingConcept());
        c.setAveragePrice(getAveragePrice());
        c.setBookingWindowFrom(getBookingWindowFrom());
        c.setBookingWindowTo(getBookingWindowTo());
        c.setSpecialTerms(getSpecialTerms());
        c.setSupplier(getSupplier());
        c.getAgencies().addAll(getAgencies());
        c.setTitle("COPY OF " + getTitle());
        c.setType(getType());
        c.setValidFrom(getValidFrom());
        c.setValidTo(getValidTo());
        c.setVATIncluded(isVATIncluded());

        for (Price p0 : getPrices()) {
            Price p = p0.clone(em, u);
            p.setContract(c);
            c.getPrices().add(p);
        }
        return c;
    }

    private void increment(double percent, double amount) {
        for (Price p : getPrices()) {
            p.setPrice(Helper.roundEuros(p.getPrice() * (100d + percent) / 100d + amount));
        }
    }




    @Override
    public Document toXml(EntityManager em) {
        Document doc = super.toXml(em);

        Element xml = doc.getRootElement();



        List<Vehicle> vs = new ArrayList<>();
        List<io.mateu.erp.model.product.transfer.Zone> os = new ArrayList<>();
        List<io.mateu.erp.model.product.transfer.Zone> ds = new ArrayList<>();
        for (Price p : getPrices()) {
            if (!vs.contains(p.getVehicle())) vs.add(p.getVehicle());
            if (!os.contains(p.getOrigin())) os.add(p.getOrigin());
            if (!ds.contains(p.getDestination())) ds.add(p.getDestination());
        }

        Collections.sort(vs, new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle o1, Vehicle o2) {
                return o1.getMinPax() - o2.getMinPax();
            }
        });

        Collections.sort(os, new Comparator<io.mateu.erp.model.product.transfer.Zone>() {
            @Override
            public int compare(io.mateu.erp.model.product.transfer.Zone o1, io.mateu.erp.model.product.transfer.Zone o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        Collections.sort(ds, new Comparator<io.mateu.erp.model.product.transfer.Zone>() {
            @Override
            public int compare(io.mateu.erp.model.product.transfer.Zone o1, io.mateu.erp.model.product.transfer.Zone o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (io.mateu.erp.model.product.transfer.Zone o : os) for (io.mateu.erp.model.product.transfer.Zone d : ds) {
            Element xl;
            xml.addContent(xl = new Element("line"));
            xl.setAttribute("origin", o.getName());
            xl.setAttribute("destination", d.getName());
            for (Vehicle v : vs) {
                Element ep;
                xl.addContent(ep = new Element("price").setAttribute("vehicle", v.getName() + " " + v.getMinPax() + "-" + v.getMaxPax()));
                for (Price p : getPrices()) if (p.getOrigin().equals(o) && p.getDestination().equals(d) && p.getVehicle().equals(v)) {
                    ep.addContent(new Element("line").setAttribute("transfertype", "" + p.getTransferType()).setAttribute("frompax", "" + p.getFromPax()).setAttribute("topax", "" + p.getToPax()).setAttribute("price", "" + p.getPrice() + ((p.getReturnPrice() != 0)?"/" + p.getReturnPrice():"")).setAttribute("per", "" + p.getPricePer()));
                }
            }
        }

        List<io.mateu.erp.model.product.transfer.Zone> zs = new ArrayList<>(os);
        for (io.mateu.erp.model.product.transfer.Zone z : ds) if (!zs.contains(z)) zs.add(z);
        for (io.mateu.erp.model.product.transfer.Zone z : zs) {
            Element xz;
            xml.addContent(xz = new Element("resort").setAttribute("name", z.getName()));
            for (Resort c : z.getResorts()) {
                xz.addContent(new Element("resort").setAttribute("name", c.getName()));
            }
            for (TransferPoint p : z.getPoints()) {
                xz.addContent(new Element("point").setAttribute("name", p.getName()));
            }
        }

        for (Vehicle v : vs) {
            Element ev;
            xml.addContent(ev = new Element("vehicle").setAttribute("name", v.getName()).setAttribute("minpax", "" + v.getMinPax()).setAttribute("maxpax", "" + v.getMaxPax()));
            if (v.isOnRequest()) ev.setAttribute("onrequest", "");
        }



        return doc;
    }

    @Override
    public String getXslfo(EntityManager em) {
        return AppConfig.get(em).getXslfoForTransferContract();
    }

}
