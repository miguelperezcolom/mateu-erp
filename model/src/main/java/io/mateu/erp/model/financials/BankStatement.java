package io.mateu.erp.model.financials;

import io.mateu.erp.model.payments.Account;
import io.mateu.erp.model.payments.Payment;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class BankStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Account account;

    @NotNull
    private LocalDateTime created;

    private LocalDate date;

    private String text;

    private double value;

    @ManyToOne
    private Payment payment;

}
