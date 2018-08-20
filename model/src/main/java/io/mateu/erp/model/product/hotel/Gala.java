package io.mateu.erp.model.product.hotel;

import io.mateu.mdd.core.annotations.ValueClass;
import io.mateu.mdd.core.util.XMLSerializable;
import org.jdom2.Element;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Gala implements XMLSerializable {

    private LocalDate date;

    private double pricePerPax;

    private List<Double> childDiscounts = new ArrayList<>();

    @ValueClass(BoardType.class)
    private List<String> boards = new ArrayList<>();



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

    public List<Double> getChildDiscounts() {
        return childDiscounts;
    }

    public void setChildDiscounts(List<Double> childDiscounts) {
        this.childDiscounts = childDiscounts;
    }

    public List<String> getBoards() {
        return boards;
    }

    public void setBoards(List<String> boards) {
        this.boards = boards;
    }

    public Gala(Element e) {
        if (e.getAttribute("date") != null) setDate(LocalDate.parse(e.getAttributeValue("date")));
        if (e.getAttribute("pricePerPax") != null) setPricePerPax(Double.parseDouble(e.getAttributeValue("pricePerPax")));
        for (Element z : e.getChildren("board")) getBoards().add(z.getAttributeValue("id"));
        for (Element z : e.getChildren("childDiscount")) getChildDiscounts().add(Double.parseDouble(z.getAttributeValue("percent")));
    }

    public Gala() {
    }

    public Gala(LocalDate date, double pricePerPax, List<Double> childDiscounts, List<String> boards) {
        this.date = date;
        this.pricePerPax = pricePerPax;
        this.childDiscounts = childDiscounts;
        this.boards = boards;
        if (this.boards == null) this.boards = new ArrayList<>();
    }

    @Override
    public Element toXml() {
        Element e = new Element("gala");

        if (getDate() != null) e.setAttribute("date", "" + getDate());
        e.setAttribute("pricePerPax", "" + getPricePerPax());
        for (String k : getBoards()) e.addContent(new Element("board").setAttribute("id", "" + k));
        for (double k : getChildDiscounts()) e.addContent(new Element("chldDiscount").setAttribute("percent", "" + k));


        return e;
    }
}
