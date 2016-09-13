package mateu.erp.model.financials;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * holder for a financial agent, tipically associated to a VAT ID (e.g. a customer, a supplier, ourselves)
 *
 * Sometimes the same financial agent will act as customer and as supplier. This is interesting to manage a balance of payments
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Table(name = "MA_FINANCIALAGENT")
@Getter@Setter
public class FinancialAgent {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="financialagent_seq_gen")
    @SequenceGenerator(name="financialagent_seq_gen", sequenceName="FAG_SEQ")
    @Column(name = "FAGIDFAG")
    private long id;

    @Column(name = "FAGNAME", length = -1)
    private String name;

}
