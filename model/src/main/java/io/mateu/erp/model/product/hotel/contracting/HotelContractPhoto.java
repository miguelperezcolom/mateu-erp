package io.mateu.erp.model.product.hotel.contracting;

import io.mateu.erp.model.financials.Currency;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
public class HotelContractPhoto {

    private Currency currency;

    /**
     * inclusive
     */
    private int childrenStartAge;

    /**
     * inclusive
     */
    private int juniorStartAge;

    /**
     * inclusive
     */
    private int adultStartAge;


    private List<Fare> fares = new ArrayList<>();

    private List<MinimumStayRule> minimumStayRules = new ArrayList<>();

    private List<ReleaseRule> releaseRules = new ArrayList<>();

    private List<CheckinRule> checkinRules = new ArrayList<>();

}
