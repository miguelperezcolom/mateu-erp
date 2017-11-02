package io.mateu.erp.model.cms;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Created by miguel on 21/1/17.
 */
@Entity
@Getter
@Setter
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    private String path;

    private byte[] bytes;
}
