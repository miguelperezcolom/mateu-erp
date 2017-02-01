package io.mateu.erp.model.product;

import io.mateu.erp.model.authentication.Audit;

import java.time.LocalDate;

/**
 * Created by miguel on 1/10/16.
 */
public class AbstractContract {

    private boolean saleNotPurchase;

    private String title;

    private LocalDate validFrom;

    private LocalDate validTo;


    private Audit audit;


    private double averagePrice;
}
