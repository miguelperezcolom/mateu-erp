package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.Passenger;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "paymentallocation")
@Getter
@Setter
public abstract class AbstractPaymentAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne@NotNull
    private Payment payment;

    public void setPayment(Payment payment) {
        this.payment = payment;
        pushUp();
    }

    private double value;

    public void setValue(double value) {
        this.value = value;
        pushUp();
    }

    public void pushUp() {
        if (payment != null) {
            payment.updateBalance();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof Payment && id == ((Payment) obj).getId());
    }

    @Override
    public String toString() {
        return "" + payment.getDate() + " " + value;
    }
}
