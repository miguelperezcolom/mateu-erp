package io.mateu.erp.model.financials;

import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.QLForCombo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for currencies
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
@QLForCombo(ql = "select x.isoCode, x.name from Currency x order by x.name")
public class Currency {

    @Id
    @NotNull
    private String isoCode;

    private int isoNumericCode;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "from")
    @Ignored
    private List<CurrencyExchange> exchanges = new ArrayList<>();

    @Override
    public String toString() {
        return getName();
    }
}
