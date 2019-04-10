package io.mateu.erp.model.organization;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter@Setter
public class PointOfSaleSettlementFormLine {

    private long bookingId;

    private LocalDate date;

    private String leadName;

    private String description;

    private double value;

    private double cash;

    private double commissions;
}
