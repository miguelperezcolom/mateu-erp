package io.mateu.common;

import com.google.common.base.Strings;
import io.mateu.common.model.authentication.User;
import io.mateu.common.model.ui.EditedRecord;
import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseMDDApp extends AbstractApplication {

    @Override
    public boolean isLastEditedAvailable() {
        return true;
    }

    @Override
    public void getLastEdited(UserData user, Callback<Data> callback) {
        try {


            Data data = new Data();

            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MMM HH:mm:ss");

            List<Data> g = new ArrayList<>();

            if (user != null && !Strings.isNullOrEmpty(user.getLogin())) Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    List<EditedRecord> rs = em.createQuery("select x from " + EditedRecord.class.getName() + " x where x.user = :u order by x.id desc").setParameter("u", em.find(User.class, user.getLogin())).setMaxResults(100).getResultList();

                    for (EditedRecord r : rs) {
                        g.add(new Data("id", r.getId(), "url", r.getUri(), "name", r.getName(), "when", r.getWhen().format(f), "icon", r.getIcon()));
                    }

                }
            });

            data.set("records", g);

            callback.onSuccess(data);


        } catch (Throwable e) {
            e.printStackTrace();
            callback.onFailure(e);
        }
    }
}
