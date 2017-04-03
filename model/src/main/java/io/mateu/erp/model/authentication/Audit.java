package io.mateu.erp.model.authentication;

import io.mateu.ui.mdd.server.interfaces.AuditRecord;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * Created by miguel on 19/1/17.
 */
@Embeddable
@Getter@Setter
public class Audit implements AuditRecord {

    public Audit() {

    }

    public Audit(User u) {
        this.createdBy = u;
    }

    @ManyToOne
    private User createdBy;

    private LocalDateTime created = LocalDateTime.now();

    @ManyToOne
    private User modifiedBy;

    private LocalDateTime modified;


    @Override
    public String toString() {
        String s = "";
        String s1 = "";
        if (getCreatedBy() != null) s1 += "by " + getCreatedBy().getLogin();
        if (getCreated() != null) s1 += (("".equals(s1))?"":" ") + getCreated();
        String s2 = "";
        if (getModifiedBy() != null) s2 += "by " + getModifiedBy().getLogin();
        if (getModified() != null) s2 += (("".equals(s1))?"":" ") + getModified();

        if (!"".equals(s1)) s += "Created " + s1;
        if (!"".equals(s2)){
            if ("".equals(s)) s += "Modified ";
            else s += ", modified ";
            s += s2;
        }

        return s;
    }

    @Override
    public void touch(EntityManager em, String login) {
        if (login != null && !"".equals(login)) {
            User u = em.find(User.class, login);
            setModifiedBy(u);
            if (getCreatedBy() == null) setCreatedBy(u);
        }
        if (getCreated() == null) setCreated(LocalDateTime.now());
        setModified(LocalDateTime.now());
    }

    public void touch(User user) {
        setModifiedBy(user);
        if (getCreatedBy() == null) setCreatedBy(user);

        if (getCreated() == null) setCreated(LocalDateTime.now());
        setModified(LocalDateTime.now());
    }
}
