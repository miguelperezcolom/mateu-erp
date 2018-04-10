package io.mateu.erp.model.product.generic;

import io.mateu.ui.mdd.server.annotations.OwnedList;
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

    private boolean active = true;

    @OneToMany(mappedBy = "product")
    @OwnedList
    private List<Extra> extras = new ArrayList<>();

}
