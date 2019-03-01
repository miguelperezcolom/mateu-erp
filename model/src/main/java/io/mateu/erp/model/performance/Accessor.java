package io.mateu.erp.model.performance;

import io.mateu.erp.model.product.generic.Contract;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity@Getter@Setter
public class Accessor {

    private static Accessor instance;

    @Id
    private long id = 1;

    public static Accessor get(EntityManager em) {
        if (instance == null) {
            instance = em.find(Accessor.class, 1l);
            if (instance == null) {
                instance = new Accessor();
                em.persist(instance);
            }
        }
        return instance;
    }


    @OneToMany
    @JoinTable(name = "accessor_genericcontract")
    private List<Contract> genericContracts = new ArrayList<>();

    @OneToMany
    @JoinTable(name = "accessor_transfercontract")
    private List<io.mateu.erp.model.product.transfer.Contract> transferContracts = new ArrayList<>();

    @OneToMany
    @JoinTable(name = "accessor_tourcontract")
    private List<io.mateu.erp.model.product.tour.Contract> tourContracts = new ArrayList<>();
;


}
