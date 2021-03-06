package io.mateu.erp.model.product.hotel;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.UseCheckboxes;
import io.mateu.mdd.core.data.FareValue;
import io.mateu.mdd.core.util.XMLSerializable;
import org.jdom2.Element;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Gala implements XMLSerializable {

    @Ignored
    private HotelContractPhoto photo;

    private LocalDate date;

    private double pricePerPax;

    private FareValue childPrice;

    private FareValue juniorPrice;

    @UseCheckboxes(editableInline = true)
    private List<String> boards = new ArrayList<>();

    public DataProvider getBoardsDataProvider() {
        List<BoardType> l = new ArrayList<>();
        Hotel h = photo.getContract().getHotel();
        for (Board r : h.getBoards()) {
            l.add(r.getType());
        }
        return new ListDataProvider<BoardType>(l);
    }



    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getPricePerPax() {
        return pricePerPax;
    }

    public void setPricePerPax(double pricePerPax) {
        this.pricePerPax = pricePerPax;
    }

    public FareValue getChildPrice() {
        return childPrice;
    }

    public void setChildPrice(FareValue childPrice) {
        this.childPrice = childPrice;
    }

    public FareValue getJuniorPrice() {
        return juniorPrice;
    }

    public void setJuniorPrice(FareValue juniorPrice) {
        this.juniorPrice = juniorPrice;
    }

    public List<String> getBoards() {
        return boards;
    }

    public void setBoards(List<String> boards) {
        this.boards = boards;
    }

    public Gala(HotelContractPhoto photo) {
        this.photo = photo;
    }

    public Gala(HotelContractPhoto photo, Element e) {
        this.photo = photo;
        fromXml(e);
    }

    public Gala() {
    }

    public Gala(LocalDate date, double pricePerPax, FareValue childPrice, FareValue juniorPrice, List<String> boards) {
        this.date = date;
        this.pricePerPax = pricePerPax;
        this.childPrice = childPrice;
        this.juniorPrice = juniorPrice;
        this.boards = boards;
        if (this.boards == null) this.boards = new ArrayList<>();
    }

    @Override
    public Element toXml() {
        Element e = new Element("gala");

        if (getDate() != null) e.setAttribute("date", "" + getDate());
        e.setAttribute("pricePerPax", "" + getPricePerPax());
        for (String k : getBoards()) e.addContent(new Element("board").setAttribute("id", "" + k));
        if (childPrice != null) e.addContent(childPrice.toXml().setText("childPrice"));
        if (juniorPrice != null) e.addContent(juniorPrice.toXml().setText("juniorPrice"));


        return e;
    }

    @Override
    public void fromXml(Element e) {
        if (e.getAttribute("date") != null) setDate(LocalDate.parse(e.getAttributeValue("date")));
        if (e.getAttribute("pricePerPax") != null) setPricePerPax(Double.parseDouble(e.getAttributeValue("pricePerPax")));
        for (Element z : e.getChildren("board")) getBoards().add(z.getAttributeValue("id"));
        if (e.getChild("childPrice") != null) setChildPrice(new FareValue(e.getChild("childPrice")));
        if (e.getChild("juniorPrice") != null) setJuniorPrice(new FareValue(e.getChild("junorPrice")));
    }
}
