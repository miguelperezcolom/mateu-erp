package io.mateu.erp.model.financials;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * holder for customers (e.g. a touroperator, a travel agency, ...)
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Table(name = "MA_CUSTOMER")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSIDCUS")
    private long id;

    @Column(name = "CUSNAME")
    private String name;


    @ManyToOne
    @JoinColumn(name = "CUSIDFAG")
    private FinancialAgent financialAgent;
}
