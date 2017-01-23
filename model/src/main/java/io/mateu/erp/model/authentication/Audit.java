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
        if (getCreatedBy() != null) s += "Created by " + getCreatedBy().getLogin();
        if (getCreated() != null) s += " " + getCreated();
        if (getModifiedBy() != null) s += ", modified by " + getCreatedBy().getLogin();
        if (getModified() != null) s += " " + getCreated();

        return s;
    }
}
