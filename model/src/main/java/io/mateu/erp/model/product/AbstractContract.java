package io.mateu.erp.model.product;

import io.mateu.erp.model.authentication.Audit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class AbstractContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private boolean saleNotPurchase;

    private String title;

    private LocalDate validFrom;

    private LocalDate validTo;


    private Audit audit;


    private double averagePrice;
}
