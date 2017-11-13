package io.mateu.erp.model.cms;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter@Setter
public class HotelBookingPage extends AbstractPage {


    @OneToMany
    private List<Card> messages = new ArrayList<>();

}
