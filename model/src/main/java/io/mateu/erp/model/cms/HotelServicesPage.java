package io.mateu.erp.model.cms;

import io.mateu.ui.mdd.server.annotations.OwnedList;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter@Setter
public class HotelServicesPage extends AbstractPage {


    @OneToMany
    @OwnedList
    private List<Card> services = new ArrayList<>();

}
