package io.mateu.erp.model.product.hotel;

import java.time.LocalDate;

public interface ICancellationRule {

    LocalDate getStart();

    LocalDate getEnd();

    int getRelease();

    double getAmount();

    double getPercent();

    int getFirstNights();

}
