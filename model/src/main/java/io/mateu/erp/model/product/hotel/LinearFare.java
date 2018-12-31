package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.product.hotel.offer.PriceOffer;
import io.mateu.mdd.core.annotations.ColumnWidth;
import io.mateu.mdd.core.annotations.FullWidth;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.NotInEditor;
import io.mateu.mdd.core.interfaces.UseCalendarToEdit;
import io.mateu.mdd.core.util.DatesRange;
import io.mateu.mdd.core.util.XMLSerializable;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class LinearFare implements XMLSerializable, UseCalendarToEdit {

    @Ignored
    private HotelContractPhoto photo;

    @Ignored
    private PriceOffer offer;

    @ColumnWidth(150)
    private String name;

    private List<DatesRange> dates = new ArrayList<>();

    @Override
    public String toString() {
        String s = "" + name + " (";
        boolean first = true;
        for (DatesRange r : dates) {
            if (first) first = false; else s += ",";
            s += r;
        }
        s += "): ";
        first = true;
        for (LinearFareLine l : lines) {
            if (first) first = false; else s += ", ";
            s += l.getLodgingPrice() + "/" + l.getAdultPrice();
        }
        return s;
    }


    @NotInEditor
    @ColumnWidth(400)
    private String period;

    public String getPeriod() {
        String s = "";
        boolean first = true;
        for (DatesRange r : dates) {
            if (first) first = false; else s += ", ";
            s += r;
        }
        return s;
    }

    @NotInEditor
    @ColumnWidth(550)
    private String prices;

    public String getPrices() {
        String s = "";
        boolean first = true;
        for (LinearFareLine l : lines) {
            if (first) first = false; else s += ", ";
            s += (l.getRoomTypeCode() != null?l.getRoomTypeCode().getCode():"--") + "x" + (l.getBoardTypeCode() != null?l.getBoardTypeCode().getCode():"--");
            s += "=" + l.getLodgingPrice() + "/" + l.getAdultPrice();
        }
        return s;
    }


    @FullWidth
    private List<LinearFareLine> lines = new ArrayList<>();

    public LinearFareLine createLinesInstance() {
        return new LinearFareLine(this);
    }

    public LinearFare(List<DatesRange> dates, String name, List<LinearFareLine> lines) {
        this.dates = dates;
        this.name = name;
        this.lines = lines;
    }

    public LinearFare() {
    }

    public LinearFare(HotelContractPhoto photo) {
        this.photo = photo;
    }

    public LinearFare(HotelContractPhoto photo, Element e) {
        this.photo = photo;
        fromXml(e);
    }

    @Override
    public Element toXml() {
        Element e = new Element("fare");
        if (getName() != null) e.setAttribute("name", getName());
        if (getDates() != null) {
            Element x;
            e.addContent(x = new Element("dates"));
            for (DatesRange r : getDates()) x.addContent(r.toXml());
        }

        Element els;
        e.addContent(els = new Element("lines"));
        for (LinearFareLine l : getLines()) {
            els.addContent(l.toXml());
        }
        return e;
    }

    @Override
    public void fromXml(Element e) {
        if (e.getAttribute("name") != null) setName(e.getAttributeValue("name"));
        if (e.getChild("dates") != null) for (Element z: e.getChild("dates").getChildren()) getDates().add(new DatesRange(z));
        if (e.getChild("lines") != null) for (Element z : e.getChild("lines").getChildren("line")) getLines().add(new LinearFareLine(this, z));
    }

    @Override
    public List<LocalDate> getCalendarDates() {
        return null;
    }

    @Override
    public String getCalendarText() {
        return null;
    }

    @Override
    public String getCalendarCss() {
        return null;
    }

    @Override
    public String getNamePropertyName() {
        return "name";
    }

    @Override
    public String getDatesRangesPropertyName() {
        return "dates";
    }


    public LinearFare cloneAsConverted() {
        return new LinearFare(photo, toXml());
    }
}
