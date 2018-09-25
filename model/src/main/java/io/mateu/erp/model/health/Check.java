package io.mateu.erp.model.health;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
@Table(name = "_check")
public class Check {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Report report;

    @NotEmpty
    private String name;

    private boolean ok;

    private String msgs;

}
