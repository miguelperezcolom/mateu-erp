package io.mateu.erp.model.revenue;

import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.annotations.Section;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class HandlingFeeLine {

    @Section("Info")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private HandlingFee handlingFee;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    private int groupsMinPax;

    private int groupsMinRooms;

    private boolean forHandledHotels;

    @SameLine
    private boolean forDirectHotels;

    @SameLine
    private boolean forTransfers;

    private boolean perNight;

    private boolean VATIncluded;

    private double percent;

    @Section("Individual files")
    private double individualBookingAmountPerAdult;

    private double individualBookingAmountPerChild;

    private double individualBookingAmountPerRoom;

    private double individualBookingAmountPerBooking;


    @Section("Group files")
    private double groupBookingAmountPerAdult;

    private double groupBookingAmountPerChild;

    private double groupBookingAmountPerRoom;

    private double groupBookingAmountPerBooking;


}
