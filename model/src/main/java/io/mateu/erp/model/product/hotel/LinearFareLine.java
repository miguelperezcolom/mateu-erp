package io.mateu.erp.model.product.hotel;


import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.mdd.core.annotations.ColumnWidth;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.ListColumn;
import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.data.FareValue;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.XMLSerializable;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class LinearFareLine implements XMLSerializable {

    @Ignored
    private LinearFare fare;

    @ListColumn
    @ColumnWidth(400)
    private RoomType roomTypeCode;

    public DataProvider getRoomTypeCodeDataProvider() {
        List<RoomType> l = new ArrayList<>();
        Hotel h = null;
        if (fare != null) h = fare.getPhoto().getContract().getHotel();
        for (Room r : h.getRooms()) {
            l.add(r.getType());
        }
        return new ListDataProvider<RoomType>(l);
    }


    @SameLine
    @ListColumn
    @ColumnWidth(400)
    private BoardType boardTypeCode;

    public DataProvider getBoardTypeCodeDataProvider() {
        List<BoardType> l = new ArrayList<>();
        Hotel h = null;
        if (fare != null) h = fare.getPhoto().getContract().getHotel();
        for (Board r : h.getBoards()) {
            l.add(r.getType());
        }
        return new ListDataProvider<BoardType>(l);
    }



    @ListColumn
    private double lodgingPrice;
    @SameLine
    private FareValue singleUsePrice;

    @ListColumn
    private double adultPrice;
    @SameLine
    private double mealAdultPrice;

    private FareValue juniorPrice;
    @SameLine
    private FareValue childPrice;
    @SameLine
    private FareValue infantPrice = new FareValue("0");




    private FareValue mealJuniorPrice;
    @SameLine
    private FareValue mealChildPrice;
    @SameLine
    private FareValue mealInfantPrice;




    private FareValue extraAdultPrice;
    @SameLine
    private FareValue extraJuniorPrice;
    @SameLine
    private FareValue extraChildPrice;
    @SameLine
    private FareValue extraInfantPrice;


    public LinearFareLine() {

    }

    public LinearFareLine(LinearFare fare) {
        this.fare = fare;
    }

    public LinearFareLine(LinearFare fare, Element e) {
        this.fare = fare;
        fromXml(e);
    }


    public LinearFareLine(LinearFare fare, String roomTypeCode, String boardTypeCode, double lodging, double adult) {
        this.fare = fare;
        this.roomTypeCode = getRoom(roomTypeCode);
        this.boardTypeCode = getBoard(boardTypeCode);
        this.lodgingPrice = lodging;
        this.adultPrice = adult;
    }

    private RoomType getRoom(String roomTypeCode) {
        RoomType c = null;
        if (fare != null
                && fare.getPhoto() != null
                && fare.getPhoto().getContract() != null
                && fare.getPhoto().getContract().getHotel() != null) {
            for (Room r : fare.getPhoto().getContract().getHotel().getRooms()) {
                if (r.getType().getCode().equals(roomTypeCode)) {
                    c = r.getType();
                    return c;
                }
            }
        } else {
            try {
                c = Helper.find(RoomType.class, roomTypeCode);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return c;
    }

    private BoardType getBoard(String boardTypeCode) {
        BoardType c = null;
        if (fare != null
                && fare.getPhoto() != null
                && fare.getPhoto().getContract() != null
                && fare.getPhoto().getContract().getHotel() != null) {
            for (Board r : fare.getPhoto().getContract().getHotel().getBoards()) {
                if (r.getType().getCode().equals(boardTypeCode)) {
                    c = r.getType();
                    return c;
                }
            }
        } else {
            try {
                c = Helper.find(BoardType.class, boardTypeCode);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return c;
    }

    public LinearFareLine(LinearFare fare, String roomTypeCode, String boardTypeCode, double lodging, double adult, double junior, double child, double infant) {
        this.fare = fare;
        this.roomTypeCode = getRoom(roomTypeCode);
        this.boardTypeCode = getBoard(boardTypeCode);
        this.lodgingPrice = lodging;
        this.adultPrice = adult;
        this.juniorPrice = new FareValue("" + junior);
        this.childPrice = new FareValue("" + child);
        this.infantPrice = new FareValue("" + infant);
    }

    public LinearFareLine(LinearFareLine o) {
        this.fare = o.fare;
        this.roomTypeCode = o.roomTypeCode;
        this.boardTypeCode = o.boardTypeCode;
        this.lodgingPrice = o.lodgingPrice;
        this.adultPrice = o.adultPrice;
        if (o.juniorPrice != null) this.juniorPrice = o.juniorPrice;
        if (o.childPrice != null) this.childPrice = o.childPrice;
        if (o.infantPrice != null) this.infantPrice = o.infantPrice;
        this.mealAdultPrice = o.mealAdultPrice;
        if (o.mealJuniorPrice != null) this.mealJuniorPrice = o.mealJuniorPrice;
        if (o.mealChildPrice != null) this.mealChildPrice = o.mealChildPrice;
        if (o.mealInfantPrice != null) this.mealInfantPrice = o.mealInfantPrice;
        if (o.extraAdultPrice != null) this.extraAdultPrice = o.extraAdultPrice;
        if (o.extraJuniorPrice != null) this.extraJuniorPrice = o.extraJuniorPrice;
        if (o.extraChildPrice != null) this.extraChildPrice = o.extraChildPrice;
        if (o.extraInfantPrice != null) this.extraInfantPrice = o.extraInfantPrice;
    }

    public LinearFareLine(LinearFare fare, String roomTypeCode, String boardTypeCode, double lodgingPrice, double adultPrice, FareValue juniorPrice, FareValue childPrice, FareValue infantPrice, double mealAdultPrice, FareValue mealJuniorPrice, FareValue mealChildPrice, FareValue mealInfantPrice, FareValue extraAdultPrice, FareValue extraJuniorPrice, FareValue extraChildPrice, FareValue extraInfantPrice) {
        this.fare = fare;
        this.roomTypeCode = getRoom(roomTypeCode);
        this.boardTypeCode = getBoard(boardTypeCode);
       this.lodgingPrice = lodgingPrice;
        this.adultPrice = adultPrice;
        this.juniorPrice = juniorPrice;
        this.childPrice = childPrice;
        this.infantPrice = infantPrice;
        this.mealAdultPrice = mealAdultPrice;
        this.mealJuniorPrice = mealJuniorPrice;
        this.mealChildPrice = mealChildPrice;
        this.mealInfantPrice = mealInfantPrice;
        this.extraAdultPrice = extraAdultPrice;
        this.extraJuniorPrice = extraJuniorPrice;
        this.extraChildPrice = extraChildPrice;
        this.extraInfantPrice = extraInfantPrice;
    }

    @Override
    public Element toXml() {
        Element e = new Element("line");

        if (getRoomTypeCode() != null) e.setAttribute("room", getRoomTypeCode().getCode());
        if (getBoardTypeCode() != null) e.setAttribute("board", getBoardTypeCode().getCode());


        e.setAttribute("lodging", "" + getLodgingPrice());

        e.setAttribute("adult", "" + getAdultPrice());

        if (getJuniorPrice() != null) e.setAttribute("junior", "" + getJuniorPrice());
        if (getChildPrice() != null) e.setAttribute("child", "" + getChildPrice());
        if (getInfantPrice() != null) e.setAttribute("infant", "" + getInfantPrice());

        e.setAttribute("mealadult", "" + getMealAdultPrice());
        if (getMealJuniorPrice() != null) e.setAttribute("mealjunior", "" + getMealJuniorPrice());
        if (getMealChildPrice() != null) e.setAttribute("mealchild", "" + getMealChildPrice());
        if (getMealInfantPrice() != null) e.setAttribute("mealinfant", "" + getMealInfantPrice());

        if (getExtraAdultPrice() != null) e.setAttribute("extraadult", "" + getExtraAdultPrice());
        if (getExtraJuniorPrice() != null) e.setAttribute("extrajunior", "" + getExtraJuniorPrice());
        if (getExtraChildPrice() != null) e.setAttribute("extrachild", "" + getExtraChildPrice());
        if (getExtraInfantPrice() != null) e.setAttribute("extrainfant", "" + getExtraInfantPrice());

        if (getSingleUsePrice() != null) e.setAttribute("singleuse", "" + getSingleUsePrice());

        return e;
    }

    @Override
    public void fromXml(Element e) {
        if (e.getAttribute("room") != null) setRoomTypeCode(getRoom(e.getAttributeValue("room")));
        if (e.getAttribute("board") != null) setBoardTypeCode(getBoard(e.getAttributeValue("board")));


        setLodgingPrice(Helper.toDouble(e.getAttributeValue("lodging")));

        setAdultPrice(Helper.toDouble(e.getAttributeValue("adult")));

        if (e.getAttribute("junior") != null) setJuniorPrice(new FareValue(e.getAttributeValue("junior")));
        if (e.getAttribute("child") != null) setChildPrice(new FareValue(e.getAttributeValue("child")));
        if (e.getAttribute("infant") != null) setInfantPrice(new FareValue(e.getAttributeValue("infant")));

        if (e.getAttribute("mealadult") != null) setMealAdultPrice(Helper.toDouble(e.getAttributeValue("mealadult")));
        if (e.getAttribute("mealjunior") != null) setMealJuniorPrice(new FareValue(e.getAttributeValue("mealjunior")));
        if (e.getAttribute("mealchild") != null) setMealChildPrice(new FareValue(e.getAttributeValue("mealchild")));
        if (e.getAttribute("mealinfant") != null) setMealInfantPrice(new FareValue(e.getAttributeValue("mealinfant")));

        if (e.getAttribute("extraadult") != null) setExtraAdultPrice(new FareValue(e.getAttributeValue("extraadult")));
        if (e.getAttribute("extrajunior") != null) setExtraJuniorPrice(new FareValue(e.getAttributeValue("extrajunior")));
        if (e.getAttribute("extrachild") != null) setExtraChildPrice(new FareValue(e.getAttributeValue("extrachild")));
        if (e.getAttribute("extrainfant") != null) setExtraInfantPrice(new FareValue(e.getAttributeValue("extrainfant")));

        if (e.getAttribute("singleuse") != null) setSingleUsePrice(new FareValue(e.getAttributeValue("singleuse")));
    }
}
