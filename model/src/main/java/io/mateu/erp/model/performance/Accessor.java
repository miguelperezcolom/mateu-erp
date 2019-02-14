package io.mateu.erp.model.performance;

import io.mateu.erp.model.product.generic.Contract;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
    private List<Contract> genericContracts = new ArrayList<>();

    @OneToMany
    private List<io.mateu.erp.model.product.transfer.Contract> transferContracts = new ArrayList<>();

    @OneToMany
    private List<io.mateu.erp.model.product.tour.Contract> tourContracts = new ArrayList<>();
;


}
