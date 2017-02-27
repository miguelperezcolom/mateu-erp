package io.mateu.erp.model.product.generic;

import io.mateu.erp.model.product.AbstractContract;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 31/1/17.
 */
@Entity
@Getter
@Setter
public class Contract extends AbstractContract {

    @OneToMany(mappedBy = "contract")
    private List<Price> prices = new ArrayList<>();
}
