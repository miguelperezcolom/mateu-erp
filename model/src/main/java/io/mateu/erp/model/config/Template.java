package io.mateu.erp.model.config;

import io.mateu.ui.mdd.serverside.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by miguel on 21/1/17.
 */
@Entity
@Getter
@Setter
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //TODO: falta lista aplicaciones

    private String name;

    @TextArea
    private String freemarker;

}