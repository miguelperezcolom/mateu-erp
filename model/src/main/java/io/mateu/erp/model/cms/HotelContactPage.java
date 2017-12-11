package io.mateu.erp.model.cms;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter@Setter
public class HotelContactPage extends AbstractPage {

    private String address;

    private String telephone;

    private String email;

}
