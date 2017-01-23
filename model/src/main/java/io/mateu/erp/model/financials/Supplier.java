package io.mateu.erp.model.financials;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * holder for suppliers (e.g. a hotel, a transfer company, an airline)
 *
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter
@Setter
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToOne
    private FinancialAgent financialAgent;
}
