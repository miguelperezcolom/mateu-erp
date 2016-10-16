package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Table(name = "MA_ROOMTYPE")
@Getter
@Setter
public class RoomType {

    @Id
    @Column(name = "RTYCODE")
    private String code;

    @ManyToOne
    @JoinColumn(name = "RTYNAMEIDLIT")
    private Literal name;

}
