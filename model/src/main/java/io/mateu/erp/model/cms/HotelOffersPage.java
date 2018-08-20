package io.mateu.erp.model.cms;

import io.mateu.mdd.core.annotations.OwnedList;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
