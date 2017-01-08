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
@Table(name = "MA_INVOICE")
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="invoice_seq_gen")
    @SequenceGenerator(name="invoice_seq_gen", sequenceName="INV_SEQ", allocationSize = 1)
    @Column(name = "INVIDINV")
    private long id;

    @Temporal(TIMESTAMP)
    @Column(name = "INVCREATED")
    private Date created;

    @ManyToOne
    @JoinColumn(name = "INVUSRLOGIN")
    private User createdBy;


    @Column(name = "INVNUMBER")
    private String number;

    @Column(name = "INVISSUEDATE")
    private LocalDate issueDate;


    @Column(name = "INVTAXDATE")
    private LocalDate taxDate;


    @ManyToOne
    @JoinColumn(name = "INVISSUERIDFAG")
    private FinancialAgent issuer;

    @ManyToOne
    @JoinColumn(name = "INVRECIPIENTIDFAG")
    private FinancialAgent recipient;


    @OneToMany(mappedBy = "invoice")
    private List<AbstractInvoiceLine> lines = new ArrayList<>();



}
