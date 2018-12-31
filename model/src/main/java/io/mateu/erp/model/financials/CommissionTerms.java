package io.mateu.erp.model.financials;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class CommissionTerms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "terms")
    private List<CommissionTermsLine> lines = new ArrayList<>();
}
