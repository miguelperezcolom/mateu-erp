package io.mateu.erp.model.cms;

import io.mateu.ui.mdd.server.annotations.OwnedList;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class HotelOffersPage extends AbstractPage {

    @OneToMany
    @OwnedList
    @JoinColumn(name = "page_id")
    private List<Card> offers = new ArrayList<>();

}
