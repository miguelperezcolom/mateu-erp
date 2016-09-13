package mateu.erp.model.financials;

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
    @GeneratedValue(strategy= GenerationType.AUTO, generator="customer_seq_gen")
    @SequenceGenerator(name="customer_seq_gen", sequenceName="CUS_SEQ")
    @Column(name = "CUSIDCUS")
    private long id;

    @Column(name = "CUSNAME", length = -1)
    private String name;


    @ManyToOne
    @Column(name = "CUSIDFAG")
    private FinancialAgent financialAgent;
}
