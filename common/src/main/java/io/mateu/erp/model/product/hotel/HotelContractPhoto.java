package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.util.XMLSerializable;
import io.mateu.ui.mdd.server.annotations.StartTabs;
import io.mateu.ui.mdd.server.annotations.Tab;
import io.mateu.ui.mdd.server.annotations.ValueClass;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@XmlRootElement(name = "terms")
public class HotelContractPhoto implements Serializable, Externalizable {



    @StartTabs
    @Tab("General")
    private String currencyIsoCode;

    private RatesType ratesType = RatesType.NET;

    private List<DoublePerDateRange> commission = new ArrayList<>();

    private boolean mandatoryRates;


    /**
     * groups related
     */
    private int maxPaxPerBooking;

    /**
     * groups related
     */
    private int maxRoomsPerBooking;

    /**
     * for allotment rescue
     */
    private int maxCheckoutRelease;


    /**
     * for warranties
     */
    private boolean zeroPricesAllowed;


    private String specialTerms;



    /**
     * inclusive
     */
    private int childStartAge;

    /**
     * inclusive
     */
    private int juniorStartAge;

    /**
     * inclusive
     */
    private int adultStartAge;

    private boolean youngestFirst;



    @Tab("Rooms")
    @ValueClass(RoomType.class)
    private List<String> rooms = new ArrayList<>();

    @Tab("Boards")
    @ValueClass(BoardType.class)
    private List<String> boards = new ArrayList<>();

    @Tab("Fares")
    private List<Fare> fares = new ArrayList<>();


    @Tab("Min. stay")
    private List<MinimumStayRule> minimumStayRules = new ArrayList<>();

    @Tab("Release")
    private List<ReleaseRule> releaseRules = new ArrayList<>();

    @Tab("Check in/out")
    private List<CheckinDaysRule> checkinDaysRules = new ArrayList<>();

    @Tab("Supplements")
    private List<Supplement> supplements = new ArrayList<>();

    @Tab("Galas")
    private List<Gala> galas = new ArrayList<>();

    @Tab("Allotment")
    private List<Allotment> allotment = new ArrayList<>();

    @Tab("Cancellation")
    private List<CancellationRule> cancellationRules = new ArrayList<>();

    @Tab("Clauses")
    private List<String> clauses = new ArrayList<>();


    @XmlAttribute
    public String getCurrencyIsoCode() {
        return currencyIsoCode;
    }

    public void setCurrencyIsoCode(String currencyIsoCode) {
        this.currencyIsoCode = currencyIsoCode;
    }

    @XmlAttribute
    public RatesType getRatesType() {
        return ratesType;
    }

    public void setRatesType(RatesType ratesType) {
        this.ratesType = ratesType;
    }

    public List<DoublePerDateRange> getCommission() {
        return commission;
    }

    public void setCommission(List<DoublePerDateRange> commission) {
        this.commission = commission;
    }

    @XmlAttribute
    public boolean isMandatoryRates() {
        return mandatoryRates;
    }

    public void setMandatoryRates(boolean mandatoryRates) {
        this.mandatoryRates = mandatoryRates;
    }

    @XmlAttribute
    public int getMaxPaxPerBooking() {
        return maxPaxPerBooking;
    }

    public void setMaxPaxPerBooking(int maxPaxPerBooking) {
        this.maxPaxPerBooking = maxPaxPerBooking;
    }

    @XmlAttribute
    public int getMaxRoomsPerBooking() {
        return maxRoomsPerBooking;
    }

    public void setMaxRoomsPerBooking(int maxRoomsPerBooking) {
        this.maxRoomsPerBooking = maxRoomsPerBooking;
    }

    @XmlAttribute
    public int getMaxCheckoutRelease() {
        return maxCheckoutRelease;
    }

    public void setMaxCheckoutRelease(int maxCheckoutRelease) {
        this.maxCheckoutRelease = maxCheckoutRelease;
    }

    @XmlAttribute
    public boolean isZeroPricesAllowed() {
        return zeroPricesAllowed;
    }

    public void setZeroPricesAllowed(boolean zeroPricesAllowed) {
        this.zeroPricesAllowed = zeroPricesAllowed;
    }

    @XmlAttribute
    public String getSpecialTerms() {
        return specialTerms;
    }

    public void setSpecialTerms(String specialTerms) {
        this.specialTerms = specialTerms;
    }

    @XmlAttribute
    public int getChildStartAge() {
        return childStartAge;
    }

    public void setChildStartAge(int childStartAge) {
        this.childStartAge = childStartAge;
    }

    @XmlAttribute
    public int getJuniorStartAge() {
        return juniorStartAge;
    }

    public void setJuniorStartAge(int juniorStartAge) {
        this.juniorStartAge = juniorStartAge;
    }

    @XmlAttribute
    public int getAdultStartAge() {
        return adultStartAge;
    }

    public void setAdultStartAge(int adultStartAge) {
        this.adultStartAge = adultStartAge;
    }

    @XmlAttribute
    public boolean isYoungestFirst() {
        return youngestFirst;
    }

    public void setYoungestFirst(boolean youngestFirst) {
        this.youngestFirst = youngestFirst;
    }

    public List<Fare> getFares() {
        return fares;
    }

    public void setFares(List<Fare> fares) {
        this.fares = fares;
    }

    public List<MinimumStayRule> getMinimumStayRules() {
        return minimumStayRules;
    }

    public void setMinimumStayRules(List<MinimumStayRule> minimumStayRules) {
        this.minimumStayRules = minimumStayRules;
    }

    public List<ReleaseRule> getReleaseRules() {
        return releaseRules;
    }

    public void setReleaseRules(List<ReleaseRule> releaseRules) {
        this.releaseRules = releaseRules;
    }

    public List<CheckinDaysRule> getCheckinDaysRules() {
        return checkinDaysRules;
    }

    public void setCheckinDaysRules(List<CheckinDaysRule> checkinDaysRules) {
        this.checkinDaysRules = checkinDaysRules;
    }

    public List<Supplement> getSupplements() {
        return supplements;
    }

    public void setSupplements(List<Supplement> supplements) {
        this.supplements = supplements;
    }

    public List<Gala> getGalas() {
        return galas;
    }

    public void setGalas(List<Gala> galas) {
        this.galas = galas;
    }

    public List<Allotment> getAllotment() {
        return allotment;
    }

    public void setAllotment(List<Allotment> allotment) {
        this.allotment = allotment;
    }

    public List<CancellationRule> getCancellationRules() {
        return cancellationRules;
    }

    public void setCancellationRules(List<CancellationRule> cancellationRules) {
        this.cancellationRules = cancellationRules;
    }

    public List<String> getClauses() {
        return clauses;
    }

    public void setClauses(List<String> clauses) {
        this.clauses = clauses;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }

    public List<String> getBoards() {
        return boards;
    }

    public void setBoards(List<String> boards) {
        this.boards = boards;
    }

    @Override
    public String toString() {
        Element xml = new Element("terms");

        xml.setAttribute("adultStartAge", "" + getAdultStartAge());
        if (getSpecialTerms() != null) xml.setAttribute("specialTerms", "" + getSpecialTerms());
        xml.setAttribute("childStartAge", "" + getChildStartAge());
        xml.setAttribute("maxCheckoutRelease", "" + getMaxCheckoutRelease());
        xml.setAttribute("maxPaxPerBooking", "" + getMaxPaxPerBooking());
        xml.setAttribute("maxRoomsPerBooking", "" + getMaxRoomsPerBooking());
        xml.setAttribute("ratesType", "" + getRatesType());


        if (getCurrencyIsoCode() != null) xml.setAttribute("currencyIsoCode", getCurrencyIsoCode());

        {
            Element l = new Element("commissions");
            for (XMLSerializable x : getCommission()) l.addContent(x.toXml());
            xml.addContent(l);
        }

        {
            Element l = new Element("fares");
            for (XMLSerializable x : getFares()) l.addContent(x.toXml());
            xml.addContent(l);
        }

        {
            Element l = new Element("minimumStayRules");
            for (XMLSerializable x : getMinimumStayRules()) l.addContent(x.toXml());
            xml.addContent(l);
        }

        {
            Element l = new Element("releaseRules");
            for (XMLSerializable x : getReleaseRules()) l.addContent(x.toXml());
            xml.addContent(l);
        }

        {
            Element l = new Element("checkinDaysRules");
            for (XMLSerializable x : getCheckinDaysRules()) l.addContent(x.toXml());
            xml.addContent(l);
        }


        {
            Element l = new Element("supplements");
            for (XMLSerializable x : getSupplements()) l.addContent(x.toXml());
            xml.addContent(l);
        }

        {
            Element l = new Element("galas");
            for (XMLSerializable x : getGalas()) l.addContent(x.toXml());
            xml.addContent(l);
        }


        {
            Element l = new Element("allotment");
            for (XMLSerializable x : getAllotment()) l.addContent(x.toXml());
            xml.addContent(l);
        }


        {
            Element l = new Element("cancellationRules");
            for (XMLSerializable x : getCancellationRules()) l.addContent(x.toXml());
            xml.addContent(l);
        }


        {
            Element l = new Element("clauses");
            for (String x : getClauses()) l.addContent(new Element("clause").setText(x));
            xml.addContent(l);
        }

        {
            Element l = new Element("rooms");
            for (String x : getRooms()) l.addContent(new Element("room").setAttribute("id", "" + x));
            xml.addContent(l);
        }

        {
            Element l = new Element("boards");
            for (String x : getBoards()) l.addContent(new Element("board").setAttribute("id", "" + x));
            xml.addContent(l);
        }


        return new XMLOutputter().outputString(xml);
    }

    private void fill(Element e) {

        if (e.getAttribute("adultStartAge") != null) setAdultStartAge(Integer.parseInt(e.getAttributeValue("adultStartAge")));
        if (e.getAttribute("specialTerms") != null) setSpecialTerms(e.getAttributeValue("specialTerms"));
        if (e.getAttribute("childStartAge") != null) setChildStartAge(Integer.parseInt(e.getAttributeValue("childStartAge")));
        if (e.getAttribute("maxCheckoutRelease") != null) setMaxCheckoutRelease(Integer.parseInt(e.getAttributeValue("maxCheckoutRelease")));
        if (e.getAttribute("maxPaxPerBooking") != null) setMaxRoomsPerBooking(Integer.parseInt(e.getAttributeValue("maxPaxPerBooking")));
        if (e.getAttribute("maxRoomsPerBooking") != null) setMaxRoomsPerBooking(Integer.parseInt(e.getAttributeValue("maxRoomsPerBooking")));
        if (e.getAttribute("ratesType") != null) setRatesType(RatesType.valueOf(e.getAttributeValue("ratesType")));

        Element x = null;
        if ((x = e.getChild("commissions")) != null) for (Element z : x.getChildren()) getCommission().add(new DoublePerDateRange(z));
        if ((x = e.getChild("fares")) != null) for (Element z : x.getChildren()) getFares().add(new Fare(z));
        if ((x = e.getChild("minimumStayRules")) != null) for (Element z : x.getChildren()) getMinimumStayRules().add(new MinimumStayRule(z));
        if ((x = e.getChild("releaseRules")) != null) for (Element z : x.getChildren()) getReleaseRules().add(new ReleaseRule(z));
        if ((x = e.getChild("checkinDaysRules")) != null) for (Element z : x.getChildren()) getCheckinDaysRules().add(new CheckinDaysRule(z));
        if ((x = e.getChild("supplements")) != null) for (Element z : x.getChildren()) getSupplements().add(new Supplement(z));
        if ((x = e.getChild("galas")) != null) for (Element z : x.getChildren()) getGalas().add(new Gala(z));
        if ((x = e.getChild("allotment")) != null) for (Element z : x.getChildren()) getAllotment().add(new Allotment(z));
        if ((x = e.getChild("canellationRules")) != null) for (Element z : x.getChildren()) getCancellationRules().add(new CancellationRule(z));
        if ((x = e.getChild("clauses")) != null) for (Element z : x.getChildren()) getClauses().add(z.getText());
        if ((x = e.getChild("rooms")) != null) for (Element z : x.getChildren()) getRooms().add(z.getAttributeValue("id"));
        if ((x = e.getChild("boards")) != null) for (Element z : x.getChildren()) getBoards().add(z.getAttributeValue("id"));
    }

    public HotelContractPhoto() {

    }

    public HotelContractPhoto(String s) throws JDOMException, IOException {
        //System.out.println("s=" + s);
        fill(new SAXBuilder().build(new StringReader(s)).getRootElement());
    }

    public HotelContractPhoto(Element e) {
        fill(e);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(toString());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        try {
            Object o = in.readObject();
            if (o != null) fill(new SAXBuilder().build(new StringReader((String) o)).getRootElement());
        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }
}
