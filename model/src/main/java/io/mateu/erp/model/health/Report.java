package io.mateu.erp.model.health;


import io.mateu.mdd.core.annotations.Order;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class Report {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Order
    private LocalDateTime date;

    private boolean ok;

    @OneToMany(mappedBy = "report")
    @OrderColumn(name = "_order")
    private List<Check> checks = new ArrayList<>();

}
