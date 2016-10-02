package io.mateu.erp.model.product;

import io.mateu.erp.model.authentication.User;

import java.util.Date;

/**
 * Created by miguel on 1/10/16.
 */
public class AbstractContract {

    private boolean saleNotPurchase;

    private String title;

    private Date validFrom;

    private Date validTo;


    private Date created;

    private User createdBy;

    private Date modified;

    private User modifiedBy;


    private double averagePrice;
}
