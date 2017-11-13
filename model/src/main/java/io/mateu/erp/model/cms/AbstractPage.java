package io.mateu.erp.model.cms;

import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class AbstractPage {

    private String title;

    private String keywords;

    private String description;

    private String author;



}
