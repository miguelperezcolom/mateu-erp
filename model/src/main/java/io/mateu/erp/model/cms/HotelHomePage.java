package io.mateu.erp.model.cms;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class HotelHomePage extends AbstractPage {

    private String mainMessage;

    private String subMessage;



    @OneToMany
    @JoinColumn(name = "page_id")
    private List<Card> messages = new ArrayList<>();



}
