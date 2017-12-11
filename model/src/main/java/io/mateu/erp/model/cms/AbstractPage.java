package io.mateu.erp.model.cms;

import com.google.common.base.Strings;
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

    private String layout;


    public String toMd() {
        // crear indice

        StringBuffer s = new StringBuffer("");
        s.append("---\n" +
                "title: \"" + getTitle() + "\"\n" +
                "date: 2017-11-10T13:52:59+01:00\n" +
                "draft: false\n" +
                ((!Strings.isNullOrEmpty(getLayout()))?"layout: \"" + getLayout() + "\"\n":"") +
                ((!Strings.isNullOrEmpty(getKeywords()))?"keywords: \"" + getKeywords() + "\"\n":"") +
                ((!Strings.isNullOrEmpty(getDescription()))?"description: \"" + getDescription() + "\"\n":"") +
                ((!Strings.isNullOrEmpty(getAuthor()))?"author: \"" + getAuthor() + "\"\n":"") +
                "---");

        return s.toString();
    }
}
