package com.quonext.quoon;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.ui.mdd.server.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private boolean active;

    @OneToOne(mappedBy = "agent")
    private Actor provider;

    private String MQHost;

    private String uploadQueue;

    private String downloadQueue;

    private String MQUser;

    private String MQPassword;

    @Temporal(TemporalType.TIMESTAMP)
    @Output
    private Date lastSentMessage;

    @Temporal(TemporalType.TIMESTAMP)
    @Output
    private Date lastReceivedMessage;

    @Output
    private QueueStatus downloadQueueStatus = QueueStatus.UNUSED;

    @Output
    private QueueStatus uploadQueueStatus = QueueStatus.UNUSED;

    @Output
    private String downloadQueueError;

    @Output
    private String uploadQueueError;


}
