package io.mateu.erp.model.booking;

import io.mateu.ui.mdd.server.annotations.Order;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class PriceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;



}
