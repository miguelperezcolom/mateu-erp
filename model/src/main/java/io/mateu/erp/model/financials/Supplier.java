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
@Table(name = "MA_SUPPLIER")
@Getter
@Setter
public class Supplier {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="supplier_seq_gen")
    @SequenceGenerator(name="supplier_seq_gen", sequenceName="SUP_SEQ")
    @Column(name = "SUPIDSUP")
    private long id;

    @Column(name = "SUPNAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "SUPIDFAG")
    private FinancialAgent financialAgent;
}
