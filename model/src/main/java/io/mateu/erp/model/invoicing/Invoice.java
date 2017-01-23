package io.mateu.erp.model.invoicing;

import lombok.Getter;
import lombok.Setter;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.financials.FinancialAgent;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Temporal(TIMESTAMP)
    private Date created;

    @ManyToOne
    private User createdBy;


    private String number;

    private LocalDate issueDate;

    private LocalDate taxDate;


    @ManyToOne
    private FinancialAgent issuer;

    @ManyToOne
    private FinancialAgent recipient;


    @OneToMany(mappedBy = "invoice")
    private List<AbstractInvoiceLine> lines = new ArrayList<>();



}
