package io.mateu.erp.model.authentication;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * Created by miguel on 19/1/17.
 */
@Embeddable
@Getter@Setter
public class Audit {

    @ManyToOne
    private User createdBy;

    @Temporal(TIMESTAMP)
    private Date created = new Date();

    @ManyToOne
    private User modifiedBy;

    @Temporal(TIMESTAMP)
    private Date modified;


    @Override
    public String toString() {
        String s = "";
        String s1 = "";
        if (getCreatedBy() != null) s1 += "by " + getCreatedBy().getLogin();
        if (getCreated() != null) s1 += (("".equals(s1))?"":" ") + getCreated();
        String s2 = "";
        if (getModifiedBy() != null) s2 += "by " + getCreatedBy().getLogin();
        if (getModified() != null) s2 += (("".equals(s1))?"":" ") + getCreated();

        if (!"".equals(s1)) s += "Created " + s1;
        if (!"".equals(s2)){
            if ("".equals(s)) s += "Modified ";
            else s += ", modified ";
            s += s1;
        }

        return s;
    }
}
