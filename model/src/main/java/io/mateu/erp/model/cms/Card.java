package io.mateu.erp.model.cms;

import io.mateu.erp.model.common.File;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String header;

    private String text;

    @ManyToOne
    private File image;


    public Card() {}

    public Card(String header, String text, File image) {
        this.header = header;
        this.text = text;
        this.image = image;
    }
}
