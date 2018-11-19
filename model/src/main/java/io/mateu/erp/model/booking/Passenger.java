package io.mateu.erp.model.booking;

import io.mateu.mdd.core.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity@Getter@Setter
public class Passenger {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull@ManyToOne
    private Booking booking;

    private String firstName;

    private String surname;

    private int age;

    private LocalDate birthDate;

    @TextArea
    private String comments;

}
