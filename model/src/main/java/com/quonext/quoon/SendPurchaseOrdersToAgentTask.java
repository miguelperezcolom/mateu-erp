package com.quonext.quoon;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.ui.mdd.server.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter@Setter
public class SendPurchaseOrdersToAgentTask extends SendPurchaseOrdersTask {

    @ManyToOne
    @Output
    private Agent agent;

    private String xml;

    private boolean readyToSend;

    @Temporal(TemporalType.TIMESTAMP)
    private Date enqueued;

    @Override
    public void runParticular(EntityManager em, User user) throws Throwable {
        setXml(createXml(em));
        setReadyToSend(true);
    }

    private String createXml(EntityManager em) {
        //todo: crear xml
        return "";
    }
}
