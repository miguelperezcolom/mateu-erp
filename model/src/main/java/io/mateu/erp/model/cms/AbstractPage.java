package io.mateu.erp.model.cms;

import com.google.common.io.Files;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "websitepage")
public class AbstractPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String title;

    private String keywords;

    private String description;

    private String author;


    public String toMd() {
        // crear indice

        StringBuffer s = new StringBuffer("");
        s.append("---\n" +
                "title: \"" + getTitle() + "\"\n" +
                "date: 2017-11-10T13:52:59+01:00\n" +
                "draft: false\n" +
                "---");

        return s.toString();
    }
}
