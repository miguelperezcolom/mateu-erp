package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.util.XMLSerializable;
import org.jdom2.Element;

public class FareValue implements XMLSerializable {

    private boolean supplement;
    private boolean discount;
    private boolean percent;
    private double value;



    public boolean isSupplement() {
        return supplement;
    }

    public void setSupplement(boolean supplement) {
        this.supplement = supplement;
    }

    public boolean isDiscount() {
        return discount;
    }

    public void setDiscount(boolean discount) {
        this.discount = discount;
    }

    public boolean isPercent() {
        return percent;
    }

    public void setPercent(boolean percent) {
        this.percent = percent;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public FareValue(Element e) {
        if (e.getAttribute("supplement") != null) setSupplement(true);
        if (e.getAttribute("discount") != null) setDiscount(true);
        if (e.getAttribute("percent") != null) setPercent(true);
        if (e.getAttribute("value") != null) setValue(Double.parseDouble(e.getAttributeValue("value")));
    }

    public FareValue() {
    }

    public FareValue(boolean supplement, boolean discount, boolean percent, double value) {
        this.supplement = supplement;
        this.discount = discount;
        this.percent = percent;
        this.value = value;
    }

    @Override
    public Element toXml() {
        Element e = new Element("fareValue");
        if (isSupplement()) e.setAttribute("supplement", "");
        if (isDiscount()) e.setAttribute("discount", "");
        if (isPercent()) e.setAttribute("percent", "");
        e.setAttribute("value","" + getValue());
        return e;
    }
}
