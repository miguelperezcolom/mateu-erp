package io.mateu.erp.model.financials;

import io.mateu.erp.model.product.hotel.ICancellationRule;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter@Setter
public class CancellationRule implements ICancellationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Actor actor;

    private LocalDate start;
    private LocalDate end;
    private int release;
    private double amount;
    private double percent;
    private int firstNights;

}
