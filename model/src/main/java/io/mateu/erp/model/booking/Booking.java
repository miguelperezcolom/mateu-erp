package io.mateu.erp.model.booking;

import io.mateu.mdd.core.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchFilter
    @Order(desc = true, priority = 10)
    private long id;

    @ManyToOne
    private File file;

    private boolean directSale;

    @OneToMany(mappedBy = "booking")
    @Ignored
    private List<Service> services = new ArrayList<>();

    private boolean cancelled;

    private LocalDate start;
    @Column(name = "_end")
    private LocalDate end;

    private int adults;
    private int children;
    private int[] ages;

    @TextArea
    private String specialRequests;

    @TextArea
    private String privateComments;


    @Override
    public String toString() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return getClass().getSimpleName() + " from " + ((start != null)?start.format(dtf):"-") + " to " + ((end != null)?end.format(dtf):"-");
    }



    @Action(order = 1)
    public static void searchAvailable() {

    }


}
