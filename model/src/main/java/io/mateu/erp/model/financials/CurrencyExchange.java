package io.mateu.erp.model.financials;

import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.NotInList;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.model.util.Constants;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

@Entity
@Getter
@Setter
public class CurrencyExchange implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @NotInList
    private Audit audit;

    @ManyToOne
    @NotNull
    private Currency from;

    @ManyToOne
    @NotNull
    private Currency to;

    private double rate;


    public static void main(String[] args) throws Throwable {
        importFromECB();
    }


    @Action
    public static void importFromECB() throws Throwable {

        Document xml = new SAXBuilder().build(new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"));

        Namespace defaultNS = xml.getRootElement().getNamespace("");

        Helper.transact(em -> {
            for (Element e : xml.getRootElement().getChild("Cube", defaultNS).getChild("Cube", defaultNS).getChildren("Cube", defaultNS)) {
                System.out.println("" + e.getAttributeValue("currency") + "->" + e.getAttributeValue("rate"));

                // 4 dígitos

                Currency to = em.find(Currency.class, e.getAttributeValue("currency"));
                Currency eur = em.find(Currency.class, "EUR");

                if (to != null) {

                    List<CurrencyExchange> l = em.createQuery("select x from " + CurrencyExchange.class.getName() + " x where x.from = :f and x.to = :t").setParameter("f", eur).setParameter("t", to).getResultList();

                    CurrencyExchange ex = null;
                    if (l.size() > 0) {
                        ex = l.get(0);
                    } else {
                        ex = new CurrencyExchange();
                        ex.setAudit(new Audit(em.find(User.class, Constants.IMPORTING_USER_LOGIN)));
                        ex.setFrom(eur);
                        ex.setTo(to);
                        em.persist(ex);
                    }
                    ex.getAudit().touch(em.find(User.class, Constants.IMPORTING_USER_LOGIN));
                    ex.setRate(Double.parseDouble(e.getAttributeValue("rate")));

                    l = em.createQuery("select x from " + CurrencyExchange.class.getName() + " x where x.from = :f and x.to = :t").setParameter("f", to).setParameter("t", eur).getResultList();

                    ex = null;
                    if (l.size() > 0) {
                        ex = l.get(0);
                    } else {
                        ex = new CurrencyExchange();
                        ex.setAudit(new Audit(em.find(User.class, Constants.IMPORTING_USER_LOGIN)));
                        ex.setFrom(to);
                        ex.setTo(eur);
                        em.persist(ex);
                    }
                    ex.setRate(Math.round(10000d / Double.parseDouble(e.getAttributeValue("rate"))) / 10000d);

                }

            }
        });


    }

}
