package io.mateu.erp.model.monitoring;

import com.google.common.base.Strings;
import io.mateu.mdd.core.model.util.EmailHelper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
@Getter
@Setter
public class Watcher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String emails;

    private boolean active;

    public void notify(Alarm alarm) {

        if (!Strings.isNullOrEmpty(getEmails())) {
            for (String email : getEmails().split("[,; ]")) {
                email = emails.trim();
                try {
                    EmailHelper.sendEmail(email, "ALARM: " + getName(), "", false);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }

    }
}
