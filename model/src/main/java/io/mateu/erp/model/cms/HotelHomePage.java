package io.mateu.erp.model.cms;

import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter@Setter
public class HotelHomePage extends AbstractPage {

    private String mainMessage;

    private String subMessage;

    @OneToMany
    private List<Card> messages = new ArrayList<>();

}
