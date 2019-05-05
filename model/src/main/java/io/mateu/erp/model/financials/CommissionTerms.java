package io.mateu.erp.model.financials;

import io.mateu.erp.model.config.AppConfig;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class CommissionTerms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "terms")
    private List<CommissionTermsLine> lines = new ArrayList<>();

    @PostPersist
    public void post() {
        WorkflowEngine.add(() -> {
            try {
                Helper.transact(em -> {

                    AppConfig.get(em).getCommissionTerms().add(em.find(CommissionTerms.class, getId()));

                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
