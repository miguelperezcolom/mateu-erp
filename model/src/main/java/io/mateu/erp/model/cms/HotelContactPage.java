package io.mateu.erp.model.cms;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class HotelContactPage extends AbstractPage {

    private String address;

    private String telephone;

    private String email;

}
