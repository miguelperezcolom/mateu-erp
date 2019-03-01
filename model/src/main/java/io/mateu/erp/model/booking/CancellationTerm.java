package io.mateu.erp.model.booking;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity@Getter@Setter
public class CancellationTerm {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne@NotNull
    private Booking booking;

    @NotNull
    private LocalDate date;

    private double amount;

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof CancellationTerm && id == ((CancellationTerm) obj).getId());
    }

    @Override
    public String toString() {
        return "" + date + " " + amount;
    }
}
