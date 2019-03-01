package io.mateu.erp.model.booking;

import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity@Getter@Setter
public class BookingChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @ManyToOne
    @Output
    private Booking booking;

    @Column(name = "_when")
    @Output
    private LocalDateTime when = LocalDateTime.now();

    @NotEmpty
    @Output
    private String key;

    @Output
    private String oldValue;

    @Output
    private String newValue;

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof BookingChange && id == ((BookingChange) obj).getId());
    }

    @Override
    public String toString() {
        return "" + when + " " + key + ": " + oldValue + " -> " + newValue;
    }


}
