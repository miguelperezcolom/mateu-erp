package io.mateu.erp.model.payments;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "paymentallocation")
@Getter
@Setter
public abstract class AbstractPaymentAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Payment payment;

    private double value;
}
