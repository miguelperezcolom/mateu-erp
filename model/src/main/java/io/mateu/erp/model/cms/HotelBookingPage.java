package io.mateu.erp.model.cms;

import io.mateu.ui.mdd.server.annotations.OwnedList;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class HotelBookingPage extends AbstractPage {

    @OneToMany
    @OwnedList
    private List<Card> messages = new ArrayList<>();

}
