package io.mateu.erp.model.invoicing;


import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.partners.Agency;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.IFrame;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.interfaces.WizardPage;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.vaadinport.vaadin.MDDUI;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.persistence.EntityManager;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Getter@Setter
public class IssueInvoicesShowProformaPage implements WizardPage {

    @Ignored
    private final IssueInvoicesParametersPage issueInvoicesParametersPage;


    @IFrame
    private URL proforma;


    public IssueInvoicesShowProformaPage(IssueInvoicesParametersPage issueInvoicesParametersPage) throws Throwable {
        this.issueInvoicesParametersPage = issueInvoicesParametersPage;

        Helper.notransact(em -> {

            Map<Agency, List<BookingCharge>> chargesByPartner = split(em, issueInvoicesParametersPage.getPending());


            Document xml = new Document(new Element("invoices"));
            User u = em.find(User.class, MDD.getUserData().getLogin());
            chargesByPartner.keySet().forEach(p -> {
                    try {
                        xml.getRootElement().addContent(new IssuedInvoice(u, chargesByPartner.get(p), true, p.getCompany().getFinancialAgent(), p.getFinancialAgent(), null).toXml(em));
                    } catch (Throwable throwable) {
                        MDD.alert(throwable);
                    }
            });


            System.out.println(Helper.toString(xml.getRootElement()));


            String archivo = UUID.randomUUID().toString();

            File temp = (System.getProperty("tmpdir") == null) ? File.createTempFile(archivo, ".pdf") : new File(new File(System.getProperty("tmpdir")), archivo + ".pdf");


            System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
            System.out.println("Temp file : " + temp.getAbsolutePath());

            FileOutputStream fileOut = new FileOutputStream(temp);
            //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
            String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
            System.out.println("xml=" + sxml);
            fileOut.write(Helper.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForIssuedInvoice())), new StreamSource(new StringReader(sxml))));
            fileOut.close();


            String baseUrl = System.getProperty("tmpurl");
            if (baseUrl == null) {
                proforma = temp.toURI().toURL();
            }
            proforma = new URL(baseUrl + "/" + temp.getName());


        });
    }

    @Override
    public String toString() {
        return "Proforma";
    }

    @Override
    public WizardPage getPrevious() {
        return issueInvoicesParametersPage;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public WizardPage getNext() {
        return null;
    }

    @Override
    public void onOk() throws Throwable {

        Helper.transact(em -> {

            Map<Agency, List<BookingCharge>> chargesByPartner = split(em, issueInvoicesParametersPage.getPending());

            chargesByPartner.keySet().forEach(p -> {

                Invoice i = null;
                try {

                    i = new IssuedInvoice(em.find(ERPUser.class, MDD.getUserData().getLogin()), chargesByPartner.get(p), false, p.getCompany().getFinancialAgent(), p.getFinancialAgent(), null);

                    em.persist(i);

                    chargesByPartner.get(p).forEach(c -> em.merge(c));

                } catch (Throwable throwable) {
                    MDD.alert(throwable);
                }

            });

            chargesByPartner.values().forEach(l -> {


            });

        });

        MDDUI.get().getNavegador().goBack();

    }

    private Map<Agency,List<BookingCharge>> split(EntityManager em, Set<IssueInvoicesItem> pending) {
        Map<Agency, List<BookingCharge>> chargesByPartner = new HashMap<>();

        Set<Agency> partners = issueInvoicesParametersPage.getPending().stream().map(i -> i.getAgency()).collect(Collectors.toSet());

        for (BookingCharge c : (List<BookingCharge>) em.createQuery("select x from " + BookingCharge.class.getName() + " x where x.invoice = null and x.agency in :ps").setParameter("ps", partners).getResultList()) {
            Agency p = c.getBooking().getAgency();
            List<BookingCharge> charges = chargesByPartner.get(p);
            if (charges == null) {
                chargesByPartner.put(p, charges = new ArrayList<>());
            }
            charges.add(c);
        }
        return chargesByPartner;
    }
}
