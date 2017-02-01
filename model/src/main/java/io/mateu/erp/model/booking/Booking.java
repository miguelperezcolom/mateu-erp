package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.Audit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for booking. Basically a booking locator associated with a customer, under which we will
 * keep a list of booked services, charges, etc
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    private Audit audit;

    private String leadName;

    private String email;

    private String customerComments;

    private String internalComments;

    private int start;

    private int end;

    private boolean cancelled;

    private double total;

    private double balance;


    @OneToMany(mappedBy = "booking")
    private List<Service> services = new ArrayList<>();

}
