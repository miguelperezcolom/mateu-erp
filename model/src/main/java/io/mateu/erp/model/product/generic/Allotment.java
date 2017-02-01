package io.mateu.erp.model.product.generic;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 31/1/17.
 */
@Entity
@Getter
@Setter
public class Allotment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Product product;

    private LocalDate from;

    @ElementCollection
    private List<Integer> contracted = new ArrayList<>();

    @ElementCollection
    private List<Integer> available = new ArrayList<>();

    @ElementCollection
    private List<Integer> booked = new ArrayList<>();



}
