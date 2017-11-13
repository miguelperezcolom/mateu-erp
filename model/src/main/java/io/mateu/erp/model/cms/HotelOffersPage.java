package io.mateu.erp.model.cms;

import io.mateu.ui.mdd.server.annotations.OwnedList;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter@Setter
public class HotelOffersPage extends AbstractPage {


    @OneToMany
    @OwnedList
    private List<Card> offers = new ArrayList<>();

}
