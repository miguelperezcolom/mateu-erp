package io.mateu.erp.model.payments;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Entity@Getter@Setter
public class MethodOfPayment {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    private String name;

    private double transactionCostPercent;


    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof MethodOfPayment && id == ((MethodOfPayment) obj).getId());
    }

}
