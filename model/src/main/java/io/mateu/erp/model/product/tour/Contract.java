package io.mateu.erp.model.product.tour;


import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.mdd.core.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "TourContract")
@Getter@Setter
public class Contract extends AbstractContract {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contract")
    @Ignored
    private List<TourPrice> prices = new ArrayList<>();

    @Override
    public Document toXml(EntityManager em) {
        Document xml = super.toXml(em);

        Element eprices;
        xml.getRootElement().addContent(eprices = new Element("prices"));

        prices.stream().filter(p -> p.isActive())
                .sorted()
                .forEach(p -> eprices.addContent(p.toXml()));

        return xml;
    }

    @Override
    public String getXslfo(EntityManager em) {
        return AppConfig.get(em).getXslfoForTourContract();
    }
}
