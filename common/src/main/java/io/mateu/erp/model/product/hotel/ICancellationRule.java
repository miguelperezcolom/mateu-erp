package io.mateu.erp.model.product.hotel;

import java.time.LocalDate;
import java.util.List;

public interface ICancellationRule {

    LocalDate getStart();

    LocalDate getEnd();

    int getRelease();

    double getAmount();

    double getPercent();

    int getFirstNights();

}
