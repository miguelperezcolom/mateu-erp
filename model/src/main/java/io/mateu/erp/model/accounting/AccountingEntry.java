package io.mateu.erp.model.accounting;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.ui.mdd.server.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class AccountingEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @Output
    private Audit audit;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entry")
    @OrderColumn(name = "orderInsideEntry")
    private List<LineItem> lines = new ArrayList<>();

}
