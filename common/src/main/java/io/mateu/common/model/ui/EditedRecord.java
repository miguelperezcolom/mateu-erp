package io.mateu.common.model.ui;

import com.google.common.base.Strings;
import io.mateu.common.model.authentication.User;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.ERPServiceImpl;
import io.mateu.ui.mdd.server.MemorizadorRegistroEditado;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class EditedRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String icon;

    @ManyToOne
    private User user;

    private String name;

    @Column(name = "_when")
    private LocalDateTime when = LocalDateTime.now();

    private String uri;

    static {
        ERPServiceImpl.memorizadorRegistrosEditados = new MemorizadorRegistroEditado() {
            @Override
            public void recordar(UserData userData, boolean isNew, String name, String sourceUri, Object id) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            Helper.transact(new JPATransaction() {
                                @Override
                                public void run(EntityManager em) throws Throwable {

                                    EditedRecord r = new EditedRecord();

                                    r.setIcon((isNew)?"new":"edit");
                                    r.setName(name);
                                    if (userData != null && !Strings.isNullOrEmpty(userData.getLogin())) r.setUser(em.find(User.class, userData.getLogin()));

                                    String u = sourceUri;

                                    if (isNew) {
                                        u += "/";
                                        if (id instanceof String) u += "s" + id;
                                        else if (id instanceof Long) u += "l" + id;
                                        else if (id instanceof Integer) u += "i" + id;
                                    }

                                    r.setUri(u);

                                    em.persist(r);

                                }
                            });

                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }

                    }
                }).start();
            }
        };
    }


}
