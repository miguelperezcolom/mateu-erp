package io.mateu.erp.model.financials;

import io.mateu.ui.mdd.server.annotations.OwnedList;
import io.mateu.ui.mdd.server.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Abseiling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private AbseilingApplicationBasis applicationBasis;

    private LocalDate paymentDate;

    @TextArea
    private String comments;


    private double percent;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "abseiling")
    @OwnedList
    private List<AbseilingLine> lines = new ArrayList<>();

}
