package io.mateu.erp.model.product.hotel;

import com.google.common.base.Strings;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.annotations.FullWidth;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.StartTabs;
import io.mateu.mdd.core.annotations.Tab;
import io.mateu.mdd.core.util.DatesRange;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.XMLSerializable;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 1/10/16.
 */
@XmlRootElement(name = "terms")
public class HotelContractPhoto implements Serializable, Externalizable {

    @NotNull
    @Ignored
    private HotelContract contract;

    @Tab("Fares")
    @FullWidth
    private List<LinearFare> fares = new ArrayList<>();

    public LinearFare createFaresInstance() {
        return new LinearFare(this);
    }


    @Tab("Min. stay")
    @FullWidth
    private List<MinimumStayRule> minimumStayRules = new ArrayList<>();

    public MinimumStayRule createMinimumStayRulesInstance() {
        return new MinimumStayRule(this);
    }

    @Tab("Release")
    @FullWidth
    private List<ReleaseRule> releaseRules = new ArrayList<>();

    public ReleaseRule createReleaseRulesInstance() {
        return new ReleaseRule(this);
    }

    @Tab("Check in/out")
    @FullWidth
    private List<WeekDaysRule> weekDaysRules = new ArrayList<>();

    @Tab("Supplements")
    @FullWidth
    private List<Supplement> supplements = new ArrayList<>();

    public Supplement createSupplementsInstance() {
        return new Supplement(this);
    }

    @Tab("Galas")
    @FullWidth
    private List<Gala> galas = new ArrayList<>();

    public Gala createGalasInstance() {
        return new Gala(this);
    }

    @Tab("Allotment")
    @FullWidth
    private List<Allotment> allotment = new ArrayList<>();


    public Allotment createAllotmentInstance() {
        return new Allotment(this);
    }

    @Tab("Security allotment")
    @FullWidth
    private List<Allotment> securityAllotment = new ArrayList<>();


    public Allotment createSecurityAllotmentInstance() {
        return new Allotment(this);
    }

    public List<LinearFare> getFares() {
        return fares;
    }

    public void setFares(List<LinearFare> fares) {
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

    public List<WeekDaysRule> getWeekDaysRules() {
        return weekDaysRules;
    }

    public void setWeekDaysRules(List<WeekDaysRule> weekDaysRules) {
        this.weekDaysRules = weekDaysRules;
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


    public List<Allotment> getSecurityAllotment() {
        return securityAllotment;
    }

    public void setSecurityAllotment(List<Allotment> securityAllotment) {
        this.securityAllotment = securityAllotment;
    }

    public HotelContract getContract() {
        return contract;
    }

    public void setContract(HotelContract contract) {
        this.contract = contract;
    }

    @Override
    public String toString() {
        StringBuffer html = new StringBuffer();


        return html.toString();
    }

    public String serialize() {
        Element xml = new Element("terms");

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
            Element l = new Element("weekDaysRules");
            for (XMLSerializable x : getWeekDaysRules()) l.addContent(x.toXml());
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
            Element l = new Element("securityAllotment");
            for (XMLSerializable x : getSecurityAllotment()) l.addContent(x.toXml());
            xml.addContent(l);
        }

        return new XMLOutputter().outputString(xml);
    }

    private void fill(Element e) {

        Element x = null;
        if ((x = e.getChild("fares")) != null) for (Element z : x.getChildren()) getFares().add(new LinearFare(this, z));
        if ((x = e.getChild("minimumStayRules")) != null) for (Element z : x.getChildren()) getMinimumStayRules().add(new MinimumStayRule(this, z));
        if ((x = e.getChild("releaseRules")) != null) for (Element z : x.getChildren()) getReleaseRules().add(new ReleaseRule(this, z));
        if ((x = e.getChild("weekDaysRules")) != null) for (Element z : x.getChildren()) getWeekDaysRules().add(new WeekDaysRule(z));
        if ((x = e.getChild("supplements")) != null) for (Element z : x.getChildren()) getSupplements().add(new Supplement(this, z));
        // ordenamos los suplementos por orden de aplicación
        getSupplements().sort((s1, s2) -> s1.getApplicationOrder() - s2.getApplicationOrder());
        if ((x = e.getChild("galas")) != null) for (Element z : x.getChildren()) getGalas().add(new Gala(this, z));
        if ((x = e.getChild("allotment")) != null) for (Element z : x.getChildren()) getAllotment().add(new Allotment(this, z));
        if ((x = e.getChild("securityAllotment")) != null) for (Element z : x.getChildren()) getSecurityAllotment().add(new Allotment(this, z));
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


    public Element getXmlForPdf(EntityManager em, Map<String, Map<String, String>> roomData, Map<String, Map<String, String>> boardData) {
        Element xml = new Element("terms");


        List<String> rooms = new ArrayList<>();
        List<String> boards = new ArrayList<>();
        for (LinearFare f : getFares()) for (LinearFareLine l : f.getLines()) {
            if (!Strings.isNullOrEmpty(l.getRoomTypeCode()) && !rooms.contains(l.getRoomTypeCode())) rooms.add(l.getRoomTypeCode());
            if (!Strings.isNullOrEmpty(l.getBoardTypeCode()) && !boards.contains(l.getBoardTypeCode())) boards.add(l.getBoardTypeCode());
        }





        Element erooms;
        xml.addContent(erooms = new Element("rooms"));
        Element eds = null;

        for (String rcode : rooms) for (String bcode : boards) {
            Element eroom;
            erooms.addContent(eroom = new Element("room"));
            eroom.setAttribute("id", rcode);
            if (roomData.containsKey(rcode)) {
                if (roomData.get(rcode).get("name") != null) eroom.setAttribute("name", roomData.get(rcode).get("name"));
                else eroom.setAttribute("name", rcode);
                if (roomData.get(rcode).get("capacity") != null) eroom.setAttribute("capacity", roomData.get(rcode).get("capacity"));
            } else eroom.setAttribute("name", rcode);

            if (boardData.containsKey(bcode)) {
                if (boardData.get(bcode).get("board") != null) eroom.setAttribute("board", boardData.get(bcode).get("board"));
                else eroom.setAttribute("board", bcode);
            } else eroom.setAttribute("board", bcode);


            Element el = null;
            Map<String, Element> els = null;
            int pos = 0;
            for (LinearFare f : getFares()) {


                Map<String, LinearFareLine> linesMap = new HashMap<>();
                for (LinearFareLine l : f.getLines()) {
                    linesMap.put(l.getRoomTypeCode() + "-" + l.getBoardTypeCode(), l);
                }

                if (pos++ % 8 == 0) {

                    if (el != null) {
                        List<Element> eliminar = new ArrayList<>();
                        for (Element l : el.getChildren("line")) {
                            if (Strings.isNullOrEmpty(l.getChildText("price"))) eliminar.add(l);
                        }
                        for (Element l : eliminar) el.removeContent(l);
                    }

                    eroom.addContent(el = new Element("row"));
                    el.addContent(eds = new Element("dates"));

                    els = new HashMap<>();
                    els.put("room", new Element("line").setAttribute("tipo", "base").setAttribute("description", "Room"));
                    els.put("adult", new Element("line").setAttribute("tipo", "base").setAttribute("description", "Adult"));
                    els.put("adultmeal", new Element("line").setAttribute("tipo", "base").setAttribute("description", "Meal"));

                    els.put("junior", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Junior"));
                    els.put("juniormeal", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Junior meal"));
                    els.put("child", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Child"));
                    els.put("childmeal", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Child meal"));
                    els.put("infant", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Infant"));
                    els.put("infantmeal", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Infant meal"));

                    els.put("extraadult", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Extra adult"));
                    els.put("extraadultmeal", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Extra meal"));
                    els.put("extrajunior", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Extra junior"));
                    els.put("extrajuniormeal", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Extra junior meal"));
                    els.put("extrachild", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Extra child"));
                    els.put("extrachildmeal", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Extra child meal"));
                    els.put("extrainfant", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Extra infant"));
                    els.put("extrainfantmeal", new Element("line").setAttribute("tipo", "suplemento").setAttribute("description", "Extra infant meal"));


                    el.addContent(els.get("room"));
                    el.addContent(els.get("adult"));
                    el.addContent(els.get("adultmeal"));

                    el.addContent(els.get("junior"));
                    el.addContent(els.get("juniormeal"));
                    el.addContent(els.get("child"));
                    el.addContent(els.get("childmeal"));
                    el.addContent(els.get("infant"));
                    el.addContent(els.get("infantmeal"));

                    el.addContent(els.get("extraadult"));
                    el.addContent(els.get("extraadultmeal"));
                    el.addContent(els.get("extrajunior"));
                    el.addContent(els.get("extrajuniormeal"));
                    el.addContent(els.get("extrachild"));
                    el.addContent(els.get("extrachildmeal"));
                    el.addContent(els.get("extrainfant"));
                    el.addContent(els.get("extrainfantmeal"));

                }


                Element ers;
                eds.addContent(ers = new Element("ranges"));
                for (DatesRange dr : f.getDates()) {
                    ers.addContent(new Element("range").setAttribute("start", dr.getStart().format(DateTimeFormatter.ISO_DATE)).setAttribute("end", dr.getEnd().format(DateTimeFormatter.ISO_DATE)));
                };


                LinearFareLine l = linesMap.get(rcode + "-" + bcode);

                if (l != null) {

                    els.get("room").addContent(new Element("price").setText("" + l.getLodgingPrice()));
                    els.get("adult").addContent(new Element("price").setText("" + l.getAdultPrice()));
                    els.get("adultmeal").addContent(new Element("price").setText("" + l.getMealAdultPrice()));

                    if (l.getJuniorPrice() != null) els.get("junior").addContent(new Element("price").setText(l.getJuniorPrice().toString()));
                    if (l.getMealJuniorPrice() != null) els.get("juniormeal").addContent(new Element("price").setText(l.getMealJuniorPrice().toString()));
                    if (l.getChildPrice() != null) els.get("child").addContent(new Element("price").setText(l.getChildPrice().toString()));
                    if (l.getMealChildPrice() != null) els.get("childmeal").addContent(new Element("price").setText(l.getMealChildPrice().toString()));
                    if (l.getInfantPrice() != null) els.get("infant").addContent(new Element("price").setText(l.getInfantPrice().toString()));
                    if (l.getMealInfantPrice() != null) els.get("infantmeal").addContent(new Element("price").setText(l.getMealInfantPrice().toString()));

                    if (l.getExtraAdultPrice() != null) els.get("extraadult").addContent(new Element("price").setText(l.getExtraAdultPrice().toString()));
                    if (l.getExtraJuniorPrice() != null) els.get("extrajunior").addContent(new Element("price").setText(l.getExtraJuniorPrice().toString()));
                    if (l.getExtraChildPrice() != null) els.get("extrachild").addContent(new Element("price").setText(l.getExtraChildPrice().toString()));
                    if (l.getExtraInfantPrice() != null) els.get("extrainfant").addContent(new Element("price").setText(l.getExtraInfantPrice().toString()));

                }


            }

            List<Element> eliminar = new ArrayList<>();
            for (Element l : el.getChildren("line")) {
                if (Strings.isNullOrEmpty(l.getChildText("price"))) eliminar.add(l);
            }
            for (Element l : eliminar) el.removeContent(l);


        }


        {
            Element rs;
            xml.addContent(rs = new Element("releases"));
            for (ReleaseRule r : getReleaseRules()) {
                rs.addContent(r.toXml());
            }
        }

        {
            Element rs;
            xml.addContent(rs = new Element("allotment"));
            for (Allotment r : getAllotment()) {
                rs.addContent(r.toXml());
            }
        }

        {
            Element rs;
            xml.addContent(rs = new Element("securityAllotment"));
            for (Allotment r : getSecurityAllotment()) {
                rs.addContent(r.toXml());
            }
        }

        {
            Element rs;
            xml.addContent(rs = new Element("supplements"));
            for (Supplement r : getSupplements()) {
                rs.addContent(r.toXml());
            }
        }

        {
            Element rs;
            xml.addContent(rs = new Element("minimumStays"));
            for (MinimumStayRule r : getMinimumStayRules()) {
                rs.addContent(r.toXml());
            }
        }

        {
            Element rs;
            xml.addContent(rs = new Element("weekDays"));
            for (WeekDaysRule r : getWeekDaysRules()) {
                rs.addContent(r.toXml());
            }
        }

        return xml;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof HotelContractPhoto && serialize().equals(((HotelContractPhoto)obj).serialize());
    }

    public HotelContractPhoto increment(double incrementPercent) {
        for (LinearFare f : fares) {
            for (LinearFareLine l : f.getLines()) {
                l.setLodgingPrice(Helper.roundEuros(l.getLodgingPrice() * (100d + incrementPercent)) / 100d);
                l.setAdultPrice(Helper.roundEuros(l.getAdultPrice() * (100d + incrementPercent)) / 100d);
            }
        }
        return this;
    }

    public HotelContractPhoto cloneAsConverted() throws Throwable {
        return new HotelContractPhoto(serialize());
    }
}