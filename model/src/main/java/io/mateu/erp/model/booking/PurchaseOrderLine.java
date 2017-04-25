package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 21/4/17.
 */
@Entity
@Getter
@Setter
public class PurchaseOrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @Ignored
    private Audit audit;

    @ManyToOne
    private PurchaseOrder order;


    private double units;
    @TextArea
    private String description;
    private PurchaseOrderLineAction action = PurchaseOrderLineAction.ADD;

}
