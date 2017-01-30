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
@Getter
@Setter
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;


    @ManyToOne
    private FinancialAgent financialAgent;
}
