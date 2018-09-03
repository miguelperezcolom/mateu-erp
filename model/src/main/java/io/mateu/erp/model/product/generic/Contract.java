package io.mateu.erp.model.product.generic;

import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.erp.model.product.ContractType;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.QLForCombo;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by miguel on 31/1/17.
 */
@Entity(name = "GenericContract")
@Getter
@Setter
@QLForCombo(ql = "select x.id, x.title from io.mateu.erp.model.product.generic.Contract x order by x.title")
public class Contract extends AbstractContract {

    @OneToMany(mappedBy = "contract")
    private List<Price> prices = new ArrayList<>();





    @Override
    public Document toXml(EntityManager em) {
        Document xml = super.toXml(em);

        Element eprices;
        xml.getRootElement().addContent(eprices = new Element("prices"));

        for (Price p : prices) eprices.addContent(p.toXml());

        return xml;
    }

    @Override
    public String getXslfo(EntityManager em) {
        return AppConfig.get(em).getXslfoForGenericContract();
    }
}
