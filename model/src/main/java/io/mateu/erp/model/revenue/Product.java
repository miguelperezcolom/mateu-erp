package io.mateu.erp.model.revenue;

import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity(name = "RevenueProduct")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @SearchFilter
    @ListColumn
    private String name;
}
