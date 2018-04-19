package io.mateu.erp.model.financials;

import io.mateu.erp.model.payments.Payment;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter@Setter
public class BankRemittance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String remittanceId;

    private LocalDate date;

    @ManyToOne
    @NotNull
    private Payment payment;


    private boolean confirmed;


}
