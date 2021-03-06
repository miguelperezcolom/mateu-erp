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
public class HotelServicesPage extends AbstractPage {

    @OneToMany
    @JoinColumn(name = "page_id")
    private List<Card> services = new ArrayList<>();

}
