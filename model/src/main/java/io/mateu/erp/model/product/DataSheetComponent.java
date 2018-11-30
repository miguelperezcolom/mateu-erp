package io.mateu.erp.model.product;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class DataSheetComponent extends Composite {
    public DataSheetComponent(DataSheet dataSheet) {

        VerticalLayout vl = new VerticalLayout();
        setCompositionRoot(vl);
        vl.addStyleName("nopadding");

        if (dataSheet.getName() != null) {
            Label l;
            vl.addComponent(l = new Label(dataSheet.getName()));
            l.addStyleName(ValoTheme.LABEL_H2);
        }

        if (dataSheet.getDescription() != null) {
            Label l;
            vl.addComponent(l = new Label(dataSheet.getDescription().toString(), ContentMode.HTML));
            l.setWidth("100%");
        }

        try {
            if (dataSheet.getMainImage() != null) {
                Image img;
                vl.addComponent(img = new Image(null, new ExternalResource(dataSheet.getMainImage().toFileLocator().getUrl())));
                img.setWidth("200px");
            }

            CssLayout ims = new CssLayout();
            ims.addStyleName("nopadding");
            for (DataSheetImage i : dataSheet.getImages()) {
                if (i.getImage() != null) {
                    Image img;
                    ims.addComponent(img = new Image(null, new ExternalResource(i.getImage().toFileLocator().getUrl())));
                    img.setWidth("100px");
                }
            }
            if (ims.getComponentCount() > 0) vl.addComponent(ims);

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (FeatureValue fv : dataSheet.getFeatures()) {
            vl.addComponent(new Label(((fv.getFeature() != null)?fv.getFeature().getName().toString():"Unknown") + ":" + ((fv.getValue() != null)?fv.getValue():"No value")));
        }

    }
}
