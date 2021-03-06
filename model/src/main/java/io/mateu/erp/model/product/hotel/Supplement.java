package io.mateu.erp.model.product.hotel;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.partners.Provider;
import io.mateu.mdd.core.annotations.ColumnWidth;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.ListColumn;
import io.mateu.mdd.core.annotations.ValueClass;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.XMLSerializable;
import org.jdom2.Element;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Supplement implements XMLSerializable {

    @Ignored
    private HotelContractPhoto photo;

    @ListColumn
    private int applicationOrder;

    @ListColumn
    private LocalDate start;
    @ListColumn
    private LocalDate end;

    /*
        private LocalDate stayStart;
    private LocalDate stayEnd;

    private LocalDate checkinStart;
    private LocalDate checkinEnd;

    private LocalDate checkoutStart;
    private LocalDate checkoutEnd;
     */

    @NotNull
    private HotelExtra extra;

    public DataProvider getExtraDataProvider() {
        List<HotelExtra> l = new ArrayList<>();
        for (HotelExtra r : photo.getContract().getHotel().getExtras()) {
            l.add(r);
        }
        return new ListDataProvider<HotelExtra>(l);
    }


    @ListColumn
    private boolean optional;

    @ListColumn
    private boolean affectedByOffers;

    @ListColumn
    @ColumnWidth(120)
    private SupplementPer per = SupplementPer.PAX;

    @ListColumn
    @ColumnWidth(120)
    private SupplementScope scope = SupplementScope.NIGHT;

    private boolean onRequest;

    @ListColumn
    private double percent;

    private boolean onStay;

    private boolean onMealplan;

    private boolean onAccumulated;


    @ListColumn
    private double value;

    @ValueClass(Provider.class)
    private long providerId;

    @ValueClass(BillingConcept.class)
    private String invoicingKey;

    private List<String> rooms = new ArrayList<>();

    public DataProvider getRoomsDataProvider() {
        List<RoomType> l = new ArrayList<>();
        Hotel h = photo.getContract().getHotel();
        for (Room r : h.getRooms()) {
            l.add(r.getType());
        }
        return new ListDataProvider<RoomType>(l);
    }

    private List<String> boards = new ArrayList<>();

    public DataProvider getBoardsDataProvider() {
        List<BoardType> l = new ArrayList<>();
        Hotel h = photo.getContract().getHotel();
        for (Board r : h.getBoards()) {
            l.add(r.getType());
        }
        return new ListDataProvider<BoardType>(l);
    }



    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isAffectedByOffers() {
        return affectedByOffers;
    }

    public void setAffectedByOffers(boolean affectedByOffers) {
        this.affectedByOffers = affectedByOffers;
    }

    public SupplementPer getPer() {
        return per;
    }

    public void setPer(SupplementPer per) {
        this.per = per;
    }

    public SupplementScope getScope() {
        return scope;
    }

    public void setScope(SupplementScope scope) {
        this.scope = scope;
    }

    public boolean isOnRequest() {
        return onRequest;
    }

    public void setOnRequest(boolean onRequest) {
        this.onRequest = onRequest;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getProviderId() {
        return providerId;
    }

    public void setProviderId(long providerId) {
        this.providerId = providerId;
    }

    public String getInvoicingKey() {
        return invoicingKey;
    }

    public void setInvoicingKey(String invoicingKey) {
        this.invoicingKey = invoicingKey;
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


    public Supplement(HotelContractPhoto photo) {
        this.photo = photo;
    }

    public Supplement(HotelContractPhoto photo, Element e) {
        this.photo = photo;
        fromXml(e);
    }

    public Supplement() {
    }

    public Supplement(LocalDate start, LocalDate end, boolean optional, boolean affectedByOffers, SupplementPer per, SupplementScope scope, boolean onRequest, double percent, double value, long providerId, String invoicingKey, List<String> rooms, List<String> boards) {
        this.start = start;
        this.end = end;
        this.optional = optional;
        this.affectedByOffers = affectedByOffers;
        this.per = per;
        this.scope = scope;
        this.onRequest = onRequest;
        this.percent = percent;
        this.value = value;
        this.providerId = providerId;
        this.invoicingKey = invoicingKey;
        this.rooms = rooms;
        this.boards = boards;
        if (this.rooms == null) this.rooms = new ArrayList<>();
        if (this.boards == null) this.boards = new ArrayList<>();
    }

    @Override
    public Element toXml() {

        Element e = new Element("supplement");

        if (getStart() != null) e.setAttribute("start", getStart().toString());
        if (getEnd() != null) e.setAttribute("end", getEnd().toString());
        if (isOptional()) e.setAttribute("optional", "");
        if (isAffectedByOffers()) e.setAttribute("affectedByOffers", "");
        if (getExtra() != null) e.setAttribute("extra", "" + getExtra().getId());
        e.setAttribute("per", "" + getPer());
        e.setAttribute("scope", "" + getScope());
        if (isOnRequest()) e.setAttribute("onRequest", "");
        e.setAttribute("percent", "" + getPercent());
        if (isOnStay()) e.setAttribute("onStay", "");
        if (isOnMealplan()) e.setAttribute("onMealplan", "");
        if (isOnAccumulated()) e.setAttribute("onAccumulated", "");
        e.setAttribute("applicationOrder", "" + getApplicationOrder());
        e.setAttribute("value", "" + getValue());
        e.setAttribute("providerId", "" + getProviderId());
        if (getInvoicingKey() != null) e.setAttribute("invoicingKey", "" + getScope());
        for (String k : getRooms()) e.addContent(new Element("room").setAttribute("id", "" + k));
        for (String k : getBoards()) e.addContent(new Element("board").setAttribute("id", "" + k));


        StringBuffer sb = new StringBuffer();

        if (getExtra() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(getExtra());
        }

        if (sb.length() > 0) sb.append(" ");
        sb.append((isOptional())?"(optional)":"(mandatory)");

        if (getStart() != null || getEnd() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(" for stays");
            if (getStart() != null) sb.append(" from " + getStart().format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (getEnd() != null) sb.append(" to " + getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (sb.length() > 0) sb.append(", ");
        sb.append((isAffectedByOffers())?"affected by offers":"offers independent");

        if (getPercent() != 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("" + getPercent() + " %");
            if (isOnStay()) sb.append(" on lodging");
            if (isOnMealplan()) {
                if (isOnStay()) sb.append(" and");
                sb.append(" on meal plan");
            }
            if (isOnAccumulated()) {
                if (isOnStay() || isOnMealplan()) sb.append(" and");
                sb.append(" on previous supplements/discounts");
            }
        }

        if (getValue() != 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("" + getValue() + "");

            sb.append(" per " + getPer() + "");
            sb.append(" for each " + getScope() + "");
        }

        if (getRooms().size() > 0) {
            sb.append(". Appliable on rooms ");
            int aux = 0;
            for (String rcode : getRooms()) {
                if (aux++ > 0) sb.append(", ");
                sb.append(rcode);
            }
        }
        if (getBoards().size() > 0) {
            sb.append((getRooms().size() > 0)?" and on boards ":". Appliable on boards ");
            int aux = 0;
            for (String rcode : getBoards()) {
                if (aux++ > 0) sb.append(", ");
                sb.append(rcode);
            }
        }

        sb.append(". Application order: " + getApplicationOrder());


        e.setAttribute("descriptionforpdf", sb.toString());

        return e;
    }

    @Override
    public void fromXml(Element e) {
        if (e.getAttribute("start") != null) setStart(LocalDate.parse(e.getAttributeValue("start")));
        if (e.getAttribute("end") != null) setEnd(LocalDate.parse(e.getAttributeValue("end")));
        if (e.getAttribute("optional") != null) setOptional(true);
        if (e.getAttribute("affectedByOffers") != null) setAffectedByOffers(true);
        if (e.getAttribute("extra") != null) setExtra(getExtraById(Long.parseLong(e.getAttributeValue("extra"))));
        if (e.getAttribute("per") != null) setPer(SupplementPer.valueOf(e.getAttributeValue("per")));
        if (e.getAttribute("scope") != null) setScope(SupplementScope.valueOf(e.getAttributeValue("scope")));
        if (e.getAttribute("onRequest") != null) setOnRequest(true);
        if (e.getAttribute("percent") != null) setPercent(Double.parseDouble(e.getAttributeValue("percent")));


        if (e.getAttribute("onStay") != null) setOnStay(true);
        if (e.getAttribute("onMealplan") != null) setOnMealplan(true);
        if (e.getAttribute("onAccumulated") != null) setOnAccumulated(true);
        if (e.getAttribute("applicationOrder") != null) setApplicationOrder(Integer.parseInt(e.getAttributeValue("applicationOrder")));

        if (e.getAttribute("value") != null) setValue(Double.parseDouble(e.getAttributeValue("value")));
        if (e.getAttribute("providerId") != null) setProviderId(Integer.parseInt(e.getAttributeValue("providerId")));
        if (e.getAttribute("invoicingKey") != null) setInvoicingKey(e.getAttributeValue("invoicingKey"));
        for (Element z : e.getChildren("room")) getRooms().add(z.getAttributeValue("id"));
        for (Element z : e.getChildren("board")) getBoards().add(z.getAttributeValue("id"));
    }

    public boolean isOnStay() {
        return onStay;
    }

    public void setOnStay(boolean onStay) {
        this.onStay = onStay;
    }

    public boolean isOnMealplan() {
        return onMealplan;
    }

    public void setOnMealplan(boolean onMealplan) {
        this.onMealplan = onMealplan;
    }

    public boolean isOnAccumulated() {
        return onAccumulated;
    }

    public void setOnAccumulated(boolean onAccumulated) {
        this.onAccumulated = onAccumulated;
    }

    public int getApplicationOrder() {
        return applicationOrder;
    }

    public void setApplicationOrder(int applicationOrder) {
        this.applicationOrder = applicationOrder;
    }

    public HotelExtra getExtra() {
        return extra;
    }

    public void setExtra(HotelExtra extra) {
        this.extra = extra;
    }


    private HotelExtra getExtraById(long id) {
        HotelExtra c = null;
        if (photo != null
                && photo.getContract() != null
                && photo.getContract().getHotel() != null) {
            for (HotelExtra r : photo.getContract().getHotel().getExtras()) {
                if (r.getId() == id) {
                    c = r;
                    return c;
                }
            }
        } else {
            try {
                c = Helper.find(HotelExtra.class, id);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return c;
    }
}
