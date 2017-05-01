package io.mateu.erp.model.product.transfer;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.erp.model.world.City;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.ColumnAlignment;
import io.mateu.ui.core.server.BaseServerSideApp;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by miguel on 31/1/17.
 */
@Entity(name = "TransferContract")
@Getter
@Setter
@QLForCombo(ql = "select x.id, x.title from io.mateu.erp.model.product.transfer.Contract x order by x.title")
public class Contract extends AbstractContract {

    @Required
    @SearchFilter
    private TransferType transferType;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    @Ignored
    private List<Price> prices = new ArrayList<>();


    @Action(name = "Clone contracts")
    public static void cloneContracts(EntityManager em, UserData user, @Selection List<Data> selection, @Parameter(name = "% increment") double percent, @Parameter(name = "Value increment") double amount) throws CloneNotSupportedException {
        User u = em.find(User.class, user.getLogin());
        for (Data d : selection) {
            Contract c0 = em.find(Contract.class, d.get("_id"));
            Contract c1 = c0.clone(em, u);
            c1.increment(percent, amount);
            em.persist(c1);
        }
    }


    public Contract clone(EntityManager em, User u) {
        Contract c = new Contract();
        c.setAudit(new Audit());
        c.setTransferType(getTransferType());
        c.setBillingConcept(getBillingConcept());
        c.setAveragePrice(getAveragePrice());
        c.setBookingWindowFrom(getBookingWindowFrom());
        c.setBookingWindowTo(getBookingWindowTo());
        c.setSpecialTerms(getSpecialTerms());
        c.setSupplier(getSupplier());
        c.getTargets().addAll(getTargets());
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


    @Action(name = "Pdf")
    public URL toPdf() throws Throwable {
        //String xslfo = "contract.xsl";

        URL[] url = new URL[1];


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                long t0 = new Date().getTime();


                try {


                    Document xml = toXml();

                    try {
                        String archivo = UUID.randomUUID().toString();

                        File temp = (System.getProperty("tmpdir") == null)?File.createTempFile(archivo, ".pdf"):new File(new File(System.getProperty("tmpdir")), archivo + ".pdf");


                        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
                        System.out.println("Temp file : " + temp.getAbsolutePath());

                        FileOutputStream fileOut = new FileOutputStream(temp);
                        //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
                        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
                        System.out.println("xml=" + sxml);
                        fileOut.write(BaseServerSideApp.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForContract())), new StreamSource(new StringReader(sxml))));
                        fileOut.close();

                        String baseUrl = System.getProperty("tmpurl");
                        if (baseUrl == null) {
                            url[0] = temp.toURI().toURL();
                        } else url[0] = new URL(baseUrl + "/" + temp.getName());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } catch (Exception e1) {
                    e1.printStackTrace();
                }


            }
        });


        return url[0];
    }

    private Document toXml() {
        Element xml = new Element("contract");

        xml.setAttribute("id", "" + getId());
        if (getTitle() != null) xml.setAttribute("title", getTitle());
        xml.setAttribute("type", "" + getType());
        if (getBillingConcept() != null && getBillingConcept().getName() != null) xml.setAttribute("billingConcept", getBillingConcept().getName());
        if (getValidFrom() != null) xml.setAttribute("validFrom", getValidFrom().toString());
        if (getValidTo() != null) xml.setAttribute("validTo", getValidTo().toString());
        if (getSupplier() != null) xml.addContent(getSupplier().toXml().setName("supplier"));
        xml.setAttribute("transferType", "" + getTransferType());
        if (getSpecialTerms() != null) xml.setAttribute("specialTerms", getSpecialTerms());
        xml.setAttribute("vat", (isVATIncluded())?"Included":"Not included");
        if (getAudit() != null) xml.setAttribute("audit", getAudit().toString());
        for (Actor t : getTargets()) xml.addContent(t.toXml().setName("target"));

        List<Vehicle> vs = new ArrayList<>();
        List<Zone> os = new ArrayList<>();
        List<Zone> ds = new ArrayList<>();
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

        Collections.sort(os, new Comparator<Zone>() {
            @Override
            public int compare(Zone o1, Zone o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        Collections.sort(ds, new Comparator<Zone>() {
            @Override
            public int compare(Zone o1, Zone o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (Zone o : os) for (Zone d : ds) {
            Element xl;
            xml.addContent(xl = new Element("line"));
            xl.setAttribute("origin", o.getName());
            xl.setAttribute("destination", d.getName());
            for (Vehicle v : vs) {
                for (Price p : getPrices()) if (p.getOrigin().equals(o) && p.getDestination().equals(d) && p.getVehicle().equals(v)) {
                    xl.addContent(new Element("price").setAttribute("vehicle", p.getVehicle().getName()).setAttribute("price", "" + p.getPrice())).setAttribute("per", "" + p.getPricePer());
                    break;
                }
            }
        }

        List<Zone> zs = new ArrayList<>(os);
        for (Zone z : ds) if (!zs.contains(z)) zs.add(z);
        for (Zone z : zs) {
            Element xz;
            xml.addContent(xz = new Element("zone").setAttribute("name", z.getName()));
            for (City c : z.getCities()) {
                xz.addContent(new Element("city").setAttribute("name", c.getName()));
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


        return new Document(xml);
    }

}
