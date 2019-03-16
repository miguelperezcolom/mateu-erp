package io.mateu.erp.model.financials;

import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.QLForCombo;
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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for currencies
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class Currency {

    @Id
    @NotNull
    private String isoCode;

    private int isoNumericCode;

    @NotNull
    private String name;

    private double exchangeRateToNucs;

    @Override
    public String toString() {
        return getName();
    }


    public static void main(String[] args) throws Throwable {
        importFromECB();
    }


    @Action
    public static void importFromECB() throws Throwable {

        Document xml = new SAXBuilder().build(new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"));

        Namespace defaultNS = xml.getRootElement().getNamespace("");

        Helper.transact(em -> {
            for (Element e : xml.getRootElement().getChild("Cube", defaultNS).getChild("Cube", defaultNS).getChildren("Cube", defaultNS)) {
                System.out.println("" + e.getAttributeValue("currency") + "->" + e.getAttributeValue("rateCost"));

                // 4 dígitos

                Currency to = em.find(Currency.class, e.getAttributeValue("currency"));

                if (to != null) {

                    to.setExchangeRateToNucs(Math.round(10000d / Double.parseDouble(e.getAttributeValue("rateCost"))) / 10000d);

                }

            }
        });


    }

}
