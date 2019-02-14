package io.mateu.erp.model.product.generic;

import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.mdd.core.annotations.QLForCombo;
import io.mateu.mdd.core.annotations.Tab;
import io.mateu.mdd.core.annotations.UseLinkToListView;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 31/1/17.
 */
@Entity(name = "GenericContract")
@Getter
@Setter
@QLForCombo(ql = "select x.id, x.title from io.mateu.erp.model.product.generic.Contract x order by x.title")
public class Contract extends AbstractContract {

    @Tab("Prices")
    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    @UseLinkToListView
    private List<Price> prices = new ArrayList<>();





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
        return AppConfig.get(em).getXslfoForGenericContract();
    }


}
