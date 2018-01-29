package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.XMLSerializable;
import io.mateu.ui.mdd.server.annotations.SameLine;
import io.mateu.ui.mdd.server.annotations.ValueClass;
import io.mateu.ui.mdd.server.interfaces.SupplementOrPositive;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.validation.constraints.NotNull;

@Getter@Setter
public class LinearFareLine implements XMLSerializable {

    @ValueClass(RoomType.class)
    @NotNull
    private String roomTypeCode;

    @ValueClass(BoardType.class)
    @NotNull
    @SameLine
    private String boardTypeCode;

    @NotNull
    private LinearFareKey key;



    private double lodgingPrice;



    private double adultPrice;
    @SameLine
    private FareValue juniorPrice;
    @SameLine
    private FareValue childPrice;
    @SameLine
    private FareValue infantPrice = new FareValue("0");




    private double mealAdultPrice;
    @SameLine
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

    @SameLine
    private FareValue singleUsePrice;



    public LinearFareLine(Element e) {
        setRoomTypeCode(e.getAttributeValue("room"));
        setBoardTypeCode(e.getAttributeValue("board"));

        setKey(LinearFareKey.valueOf(e.getAttributeValue("key")));

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


    public LinearFareLine(String roomTypeCode, String boardTypeCode, double lodging, double adult) {
        this.key = LinearFareKey.PAX;
        this.roomTypeCode = roomTypeCode;
        this.boardTypeCode = boardTypeCode;
        this.lodgingPrice = lodging;
        this.adultPrice = adult;
    }

    public LinearFareLine(String roomTypeCode, String boardTypeCode, double lodging, double adult, double junior, double child, double infant) {
        this.key = LinearFareKey.PAX;
        this.roomTypeCode = roomTypeCode;
        this.boardTypeCode = boardTypeCode;
        this.lodgingPrice = lodging;
        this.adultPrice = adult;
        this.juniorPrice = new FareValue("" + junior);
        this.childPrice = new FareValue("" + child);
        this.infantPrice = new FareValue("" + infant);
    }

    public LinearFareLine(String roomTypeCode, String boardTypeCode, LinearFareKey key, double lodging, double adult, double junior, double child, double infant) {
        this.roomTypeCode = roomTypeCode;
        this.boardTypeCode = boardTypeCode;
        this.key = key;
        this.lodgingPrice = lodging;
        this.adultPrice = adult;
        this.juniorPrice = new FareValue("" + junior);
        this.childPrice = new FareValue("" + child);
        this.infantPrice = new FareValue("" + infant);
    }

    public LinearFareLine(LinearFareLine o) {
        this.roomTypeCode = o.roomTypeCode;
        this.boardTypeCode = o.boardTypeCode;
        this.key = o.key;
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

    public LinearFareLine(String roomTypeCode, String boardTypeCode, LinearFareKey key, double lodgingPrice, double adultPrice, FareValue juniorPrice, FareValue childPrice, FareValue infantPrice, double mealAdultPrice, FareValue mealJuniorPrice, FareValue mealChildPrice, FareValue mealInfantPrice, FareValue extraAdultPrice, FareValue extraJuniorPrice, FareValue extraChildPrice, FareValue extraInfantPrice) {
        this.roomTypeCode = roomTypeCode;
        this.boardTypeCode = boardTypeCode;
        this.key = key;
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

        e.setAttribute("room", getRoomTypeCode());
        e.setAttribute("board", getBoardTypeCode());
        e.setAttribute("key", "" + getKey());


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
}
