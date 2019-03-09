package io.mateu.erp.model.commissions;

import io.mateu.erp.model.booking.Booking;
import io.mateu.mdd.core.annotations.Indelible;
import io.mateu.mdd.core.annotations.NewNotAllowed;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.UseLinkToListView;
import io.mateu.mdd.core.model.authentication.Audit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity@Getter@Setter@NewNotAllowed@Indelible
public class CommissionSettlement {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded@Output
    private Audit audit;

    @Output
    private double total;

    @Output
    private double totalCash;

    @Output
    private double totalCommission;

    @Output
    private double balance;

    @OneToMany(mappedBy = "commissionSettlement")@UseLinkToListView
    private List<Booking> bookings = new ArrayList<>();


}


