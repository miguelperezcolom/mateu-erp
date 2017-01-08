package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Table(name = "MA_ROOM")
@Getter
@Setter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="room_seq_gen")
    @SequenceGenerator(name="room_seq_gen", sequenceName="ROO_SEQ", allocationSize = 1)
    @Column(name = "ROOIDROO")
    private long id;

    @ManyToOne
    @JoinColumn(name = "ROOIDHOT")
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "ROORTYCODE")
    private RoomType type;

    @ManyToOne
    @JoinColumn(name = "ROODESCRIPTIONIDLIT")
    private Literal description;

    @Column(name = "ROOMAXCAPACITY")
    private String maxCapacity;

    @Column(name = "ROOMAXPAX")
    private int minPax;

    @Column(name = "ROOMINADFORCHDDISC")
    private int minAdultsForChildDiscount;

    @Column(name = "ROOINFALLOWED")
    private boolean infantsAllowed;

    @Column(name = "ROOCHDALLOWED")
    private boolean childrenAllowed;

    @Column(name = "ROOINFINBED")
    private boolean infantsInBed;
}
