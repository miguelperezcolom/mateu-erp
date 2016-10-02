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
    @GeneratedValue(strategy= GenerationType.AUTO, generator="customer_seq_gen")
    @SequenceGenerator(name="customer_seq_gen", sequenceName="CUS_SEQ")
    @Column(name = "INVIDINV")
    private long id;

    @Temporal(TIMESTAMP)
    @Column(name = "INVCREATED")
    private Date created;

    @ManyToOne
    @Column(name = "INVUSRLOGIN")
    private User createdBy;


    @Column(name = "INVNUMBER", length = -1)
    private String number;

    @Temporal(DATE)
    @Column(name = "INVISSUEDATE")
    private LocalDate issueDate;


    @Temporal(DATE)
    @Column(name = "INVTAXDATE")
    private LocalDate taxDate;


    @ManyToOne
    @Column(name = "INVISSUERIDFAG")
    private FinancialAgent issuer;

    @ManyToOne
    @Column(name = "INVRECIPIENTIDFAG")
    private FinancialAgent recipient;


    private List<AbstractInvoiceLine> lines = new ArrayList<>();



}
