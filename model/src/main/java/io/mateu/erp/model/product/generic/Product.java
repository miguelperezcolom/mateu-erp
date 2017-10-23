package io.mateu.erp.model.product.generic;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 31/1/17.
 */
@Entity(name = "GenericProduct")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToOne
    private Shop shop;

    @OneToMany(mappedBy = "product")
    private List<Extra> extras = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Allotment> allotments = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<StopSales> stopSales = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Release> releases = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<MinimumStay> minimumStays = new ArrayList<>();
}
