package io.mateu.erp.shared.financial;

import io.mateu.ui.core.communication.Service;

import java.net.URL;
import java.time.LocalDate;

/**
 * Created by miguel on 20/5/17.
 */
@Service(url = "financial")
public interface FinancialService {

    public void reprice(LocalDate from, LocalDate to) throws Throwable;

    public URL generalReport(LocalDate from, LocalDate to) throws Throwable;

    public URL exportToBeroni(LocalDate from, LocalDate to) throws Throwable;

}
