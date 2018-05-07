package io.mateu.erp.model.financials;

import io.mateu.erp.model.partners.Actor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class CommissionTermsLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Getter@Setter
    private Actor agent;

    private double percent;

}
