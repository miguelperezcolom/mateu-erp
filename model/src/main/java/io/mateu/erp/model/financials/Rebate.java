package io.mateu.erp.model.financials;

import io.mateu.mdd.core.annotations.OwnedList;
import io.mateu.mdd.core.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Rebate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private RebateApplicationBasis applicationBasis;

    private LocalDate paymentDate;

    @TextArea
    private String comments;


    private double percent;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rebate")
    @OwnedList
    private List<RebateLine> lines = new ArrayList<>();

}
