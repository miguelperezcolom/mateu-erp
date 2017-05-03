package io.mateu.erp.model.product.transfer;

import com.google.common.base.Strings;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.world.City;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.State;
import io.mateu.ui.core.server.BaseServerSideApp;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.persistence.*;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by miguel on 25/2/17.
 */
@Entity
@Getter
@Setter
@UseAutocompleteToSelect()
public class TransferPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Required
    private TransferPointType type;

    @Required
    @SearchFilter
    private String name;

    @ManyToOne
    @Required
    @SearchFilter
    private City city;

    @ManyToOne
    @NotInList
    private TransferPoint alternatePointForShuttle;

    @TextArea
    private String instructions;

    @StartsLine
    private String email;

    private String fax;


    @Override
    public String toString() {
        String s = getName();
        if (!Strings.isNullOrEmpty(getInstructions())) s += " / " + getInstructions();
        if (getAlternatePointForShuttle() != null) {
            s += " (SHUTTLE: " + getAlternatePointForShuttle().getName();
            if (!Strings.isNullOrEmpty(getAlternatePointForShuttle().getInstructions())) s += " / " + getAlternatePointForShuttle().getInstructions();
            s += ")";
        }
        return s;
    }


    @Action(name = "Print all")
    public static URL dumpToPdf() throws Throwable {
        URL[] url = new URL[1];


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                long t0 = new Date().getTime();


                try {


                    Document xml = dumpWholeTreeToXml();

                    try {
                        String archivo = UUID.randomUUID().toString();

                        File temp = (System.getProperty("tmpdir") == null) ? File.createTempFile(archivo, ".pdf") : new File(new File(System.getProperty("tmpdir")), archivo + ".pdf");


                        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
                        System.out.println("Temp file : " + temp.getAbsolutePath());

                        FileOutputStream fileOut = new FileOutputStream(temp);
                        //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
                        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
                        System.out.println("xml=" + sxml);
                        fileOut.write(BaseServerSideApp.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForWorld())), new StreamSource(new StringReader(sxml))));
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

    private static Document dumpWholeTreeToXml() throws Throwable {
        Element xml = new Element("all");

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                for (Country cou : (List<Country>) em.createQuery("select x from Country x order by x.name").getResultList()) {
                    Element ecou;
                    xml.addContent(ecou = new Element("country").setAttribute("name", cou.getName()));
                    for (State s : cou.getStates()) {
                        Element es;
                        ecou.addContent(es = new Element("state").setAttribute("name", s.getName()));
                        for (City c : s.getCities()) {
                            Element ec;
                            es.addContent(ec = new Element("city").setAttribute("name", c.getName()));
                            for (TransferPoint p : c.getTransferPoints()) {
                                Element ep;
                                ec.addContent(ep = new Element("transferpoint").setAttribute("type", "" + p.getType()).setAttribute("name", p.getName()));
                                if (p.getInstructions() != null) ep.setAttribute("instructions", p.getInstructions());
                                if (p.getAlternatePointForShuttle() != null) ep.setAttribute("alternatepointforshuttle", p.getAlternatePointForShuttle().getName());
                            }
                        }
                    }
                }
            }
        });

        return new Document(xml);
    }

    public static void main(String[] args) throws Throwable {
        System.out.println(new XMLOutputter(Format.getPrettyFormat()).outputString(dumpWholeTreeToXml()));
        System.out.println(dumpToPdf());
    }
}
