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
