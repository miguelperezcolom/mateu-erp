package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Table(name = "MA_BOARD")
@Getter
@Setter
public class Board {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="board_seq_gen")
    @SequenceGenerator(name="board_seq_gen", sequenceName="BOA_SEQ")
    @Column(name = "BOAIDBOA")
    private long id;

    @ManyToOne
    @JoinColumn(name = "BOAIDHOT")
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "BOAIDBRT")
    BoardType type;

    @ManyToOne
    @JoinColumn(name = "BOADESCRIPTIONIDLIT")
    private Literal description;
}
