package io.mateu.erp.model.workflow;

import com.quonext.quoon.Agent;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.hotel.HotelServiceLine;
import io.mateu.ui.mdd.server.annotations.Output;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

@Entity
@Getter@Setter
public class SendPurchaseOrdersToAgentTask extends SendPurchaseOrdersTask {

    @ManyToOne
    @Output
    private Agent agent;

    @Output
    private String xml;

    @Output
    private boolean readyToSend;

    @Temporal(TemporalType.TIMESTAMP)
    @Output
    private Date queued;

    @Override
    public void runParticular(EntityManager em, User user) throws Throwable {
        setXml(createXml(em));
        setReadyToSend(true);
        setQueued(null);
    }

    private String createXml(EntityManager em) {

        /*
        <?xml version="1.0" encoding="UTF-16" standalone="no"?>
<Reservations xmlns="urn:microsoft-dynamics-nav/xmlports/x71000">
  <Reservation>
    <RC_CRX>AVAILPRO</RC_CRX>
    <RC_CRX_Hotel>18363</RC_CRX_Hotel>
    <RC_Identifier>{152B15A0-81D5-48AD-9152-125C22A9A95D}</RC_Identifier>
    <RC_TTOO>WEB</RC_TTOO>
    <RC_Action />
    <RC_Client>WEB</RC_Client>
    <RC_Agency>WEB</RC_Agency>
    <RC_Date>2016-12-21</RC_Date>
    <RC_Hour>15:25:18</RC_Hour>
    <RC_Booking_reference />
    <RC_Voucher>JXIEB6</RC_Voucher>
    <ReservationLines>
      <ReservationLine>
        <RL_Line>1</RL_Line>
        <RL_CRX_Room_type>70991</RL_CRX_Room_type>
        <RL_Room_Quantity>1</RL_Room_Quantity>
        <RL_Check_in_Date>2017-01-03</RL_Check_in_Date>
        <RL_Check_out_Date>2017-01-05</RL_Check_out_Date>
        <RL_CRX_Board>BAR</RL_CRX_Board>
        <Observations>
          <Observation>
            <OB_Room>1</OB_Room>
            <OB_No._Line>1</OB_No._Line>
            <OB_Date>2016-12-21</OB_Date>
            <OB_Comment>Tarifa  fecha 03/01/17 importe 90</OB_Comment>
            <OB_Type>2</OB_Type>
          </Observation>
          <Observation>
            <OB_Room>1</OB_Room>
            <OB_No._Line>2</OB_No._Line>
            <OB_Date>2016-12-21</OB_Date>
            <OB_Comment>Tarifa  fecha 04/01/17 importe 100</OB_Comment>
            <OB_Type>2</OB_Type>
          </Observation>
        </Observations>
        <Occupants>
          <Occupant>
            <OC_Position>1</OC_Position>
            <OC_Person_type>0</OC_Person_type>
            <OC_Name>Hedoux</OC_Name>
            <OC_Last_name_1>Romain</OC_Last_name_1>
            <OC_Last_name2 />
            <OC_Document_No. />
            <OC_Date_of_birth />
            <OC_Sex>0</OC_Sex>
            <OC_Address />
            <OC_City />
            <OC_Province_State />
            <OC_Country_Code>FR</OC_Country_Code>
            <OC_Email>romain.hedoux@availpro.com</OC_Email>
            <OC_Telephone>+33 6123456789</OC_Telephone>
            <OC_Post_code />
            <OC_Country />
            <OC_Name2 />
            <OC_OTA_PrimaryIndicator>false</OC_OTA_PrimaryIndicator>
          </Occupant>
          <Occupant>
            <OC_Position>2</OC_Position>
            <OC_Person_type>0</OC_Person_type>
            <OC_Name />
            <OC_Last_name_1 />
            <OC_Last_name2 />
            <OC_Document_No. />
            <OC_Date_of_birth />
            <OC_Sex>1</OC_Sex>
            <OC_Address />
            <OC_City />
            <OC_Province_State />
            <OC_Country_Code />
            <OC_Email />
            <OC_Telephone />
            <OC_Post_code />
            <OC_Country />
            <OC_Name2 />
            <OC_OTA_PrimaryIndicator>false</OC_OTA_PrimaryIndicator>
          </Occupant>
        </Occupants>
        <ReservationRates>
          <ReservationRate>
            <RT_Date>2017-01-03</RT_Date>
            <RT_Room_price>90.00</RT_Room_price>
            <RT_Currency>EUR</RT_Currency>
            <RT_Price_types>0</RT_Price_types>
            <RT_Included_Taxes>true</RT_Included_Taxes>
          </ReservationRate>
          <ReservationRate>
            <RT_Date>2017-01-04</RT_Date>
            <RT_Room_price>100.00</RT_Room_price>
            <RT_Currency>EUR</RT_Currency>
            <RT_Price_types>0</RT_Price_types>
            <RT_Included_Taxes>true</RT_Included_Taxes>
          </ReservationRate>
        </ReservationRates>
        <ReservationCollections>
          <ReservationCollection>
            <CL_Collection_No.>0</CL_Collection_No.>
            <CL_Collection_Date />
            <CL_Register_type>0</CL_Register_type>
            <CL_Amount>0.00</CL_Amount>
            <CL_Currency />
            <CL_Credit_card_name />
            <CL_Card_authorization_No. />
            <CL_Card_number />
            <CL_Card_expires />
          </ReservationCollection>
        </ReservationCollections>
        <ReservationExtras>
          <ReservationExtra>
            <OF_Cod._Of_._Compl. />
            <OF_Date />
            <OF_Units>0</OF_Units>
            <OF_Service_amount>0.00</OF_Service_amount>
            <OF_Cost_Amount>0.00</OF_Cost_Amount>
            <OF_Currency />
          </ReservationExtra>
        </ReservationExtras>
      </ReservationLine>
    </ReservationLines>
  </Reservation>
  </Reservations>
         */

        Element ers = new Element("task").setAttribute("id", "" + getId());
        if (getAgent() != null) {
            ers.setAttribute("agentid", "" + getAgent().getId());
            if (getAgent().getName() != null) ers.setAttribute("agentname", getAgent().getName());
            if (getAgent().getDownloadQueue() != null) ers.setAttribute("agentdownloadqueue", getAgent().getDownloadQueue());
            if (getAgent().getDownloadQueue() != null) ers.setAttribute("agentuploadqueue", getAgent().getDownloadQueue());
            if (getAgent().getMQHost() != null) ers.setAttribute("agentmqhost", getAgent().getMQHost());
            if (getAgent().getMQUser() != null) ers.setAttribute("agentmquser", getAgent().getMQUser());
        }
        for (PurchaseOrder po : getPurchaseOrders()) {
            Element epo;
            ers.addContent(epo = new Element("purchaseorder").setAttribute("id", "" + po.getId()));
            for (Service s : po.getServices()) {
                Element es;
                epo.addContent(es = new Element("service").setAttribute("id", "" + s.getId()));
                if (s instanceof HotelService) {
                    es.setAttribute("type", "hotel");

                    HotelService h = (HotelService) s;

                    if (h.getHotel() != null) {
                        es.setAttribute("hotelid", "" + h.getHotel().getId());
                        if (h.getHotel().getQuoonId() != null) es.setAttribute("hotelquoonid", h.getHotel().getQuoonId());
                        if (h.getHotel().getName() != null) es.setAttribute("hotelname", h.getHotel().getName());
                    }

                    for (HotelServiceLine l : h.getLines()) {
                        Element el;
                        es.addContent(el = new Element("line").setAttribute("id", "" + l.getId()));

                        if (l.getStart() != null) el.setAttribute("start", l.getStart().format(DateTimeFormatter.ISO_DATE));
                        if (l.getEnd() != null) el.setAttribute("end", l.getEnd().format(DateTimeFormatter.ISO_DATE));
                        if (l.getRoomType() != null) {
                            el.setAttribute("roomid", l.getRoomType().getCode());
                            if (l.getRoomType().getName() != null && l.getRoomType().getName().getEs() != null)
                                el.setAttribute("roomname", "" + l.getRoomType().getName().getEs());
                        }
                        if (l.getRoomType() != null) {
                            el.setAttribute("roomid", l.getRoomType().getCode());
                            if (l.getRoomType().getName() != null && l.getRoomType().getName().getEs() != null)
                                el.setAttribute("roomname", "" + l.getRoomType().getName().getEs());
                        }
                        el.setAttribute("numberofrooms", "" + l.getNumberOfRooms());
                        el.setAttribute("paxperroom", "" + l.getPaxPerRoom());
                        if (l.getAges() != null) el.setAttribute("ages", Arrays.toString(l.getAges()).replaceAll("\\[", "").replaceAll("\\]", ""));
                    }

                } else es.setAttribute("type", "unknown");
            }
        }
        return new XMLOutputter(Format.getPrettyFormat()).outputString(ers);
    }
}
