package io.mateu.erp.model.booking.generic;

import io.mateu.ui.mdd.server.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by miguel on 13/4/17.
 */
@Entity
@Getter
@Setter
public class PriceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @Ignored
    private GenericService service;

    private double units;
    private LocalDate start;
    private LocalDate finish;
    private String description;

    private double pricePerUnit;
    private double pricePerUnitAndNight;

    public Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();

        d.put("units", getUnits());
        d.put("start", (getStart() != null)?getStart().format(DateTimeFormatter.BASIC_ISO_DATE):"");
        d.put("finish", (getFinish() != null)?getFinish().format(DateTimeFormatter.BASIC_ISO_DATE):"");
        d.put("description", getDescription());

        return d;
    }
}
