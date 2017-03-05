package io.mateu.erp.model.product.transfer;

import io.mateu.erp.model.product.AbstractContract;
import io.mateu.ui.mdd.server.annotations.QLForCombo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 31/1/17.
 */
@Entity(name = "TransferContract")
@Getter
@Setter
@QLForCombo(ql = "select x.id, x.title from io.mateu.erp.model.product.transfer.Contract x order by x.title")
public class Contract extends AbstractContract {

    @OneToMany(mappedBy = "contract")
    private List<Price> prices = new ArrayList<>();
}
