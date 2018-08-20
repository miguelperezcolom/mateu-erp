package io.mateu.erp.model.product.hotel;

import com.google.common.base.Strings;
import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.data.SupplementOrPositive;
import io.mateu.mdd.core.data.SupplementOrPositiveType;
import io.mateu.mdd.core.interfaces.ISupplementOrPositive;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.XMLSerializable;
import org.jdom2.Element;

public class FareValue implements XMLSerializable, ISupplementOrPositive {

    private boolean supplement;

    @SameLine
    private boolean discount;
    @SameLine
    private boolean percent;
    @SameLine
    private double value;

    public FareValue(FareValue v) {
        this.supplement = v.isSupplement();
        this.discount = v.isDiscount();
        this.percent = v.isPercent();
        this.value = v.getValue();
    }

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

    public FareValue(String s) {
        if (!Strings.isNullOrEmpty(s)) {
            if (s.startsWith("+") || s.startsWith("-")) setSupplement(true);
            if (s.endsWith("%")) {
                setSupplement(true);
                setPercent(true);
            }
            setValue(Helper.toDouble(s.replaceAll("%", "")));
        }
    }

    @Override
    public String toString() {
        String s = "";
        if (isSupplement() && getValue() >= 0) s += "+";
        s += getValue();
        if (isPercent()) s += "%";
        return s;
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

    public double applicarA(double base) {
        double v = getValue();
        if (isPercent()) v = base * v / 100d;
        if (isSupplement()) v = base + v;
        else if (isDiscount()) v = base - v;
        return v;
    }

    @Override
    public void fromData(SupplementOrPositive data) {
        setPercent(SupplementOrPositiveType.SUPPLEMENT_PERCENT.equals(data.getType()));
        setSupplement(SupplementOrPositiveType.SUPPLEMENT.equals(data.getType()));
        setValue(data.getValue());
    }


    @Override
    public SupplementOrPositive toData() {
        SupplementOrPositiveType t;
        if (isPercent()) t = SupplementOrPositiveType.SUPPLEMENT_PERCENT;
        else if (isSupplement()) t = SupplementOrPositiveType.SUPPLEMENT;
        else t = SupplementOrPositiveType.VALUE;
        return new SupplementOrPositive(t, getValue());
    }
}
