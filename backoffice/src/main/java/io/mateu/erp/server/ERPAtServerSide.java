package io.mateu.erp.server;

import io.mateu.erp.model.authentication.Grant;
import io.mateu.erp.model.authentication.Permission;
import io.mateu.erp.model.authentication.USER_STATUS;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.population.Populator;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import io.mateu.ui.core.server.BaseServerSideApp;
import io.mateu.ui.core.server.SQLTransaction;
import io.mateu.ui.core.server.ServerSideApp;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.UserData;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created by miguel on 3/1/17.
 */
public class ERPAtServerSide extends BaseServerSideApp implements ServerSideApp {
    @Override
    public DataSource getJdbcDataSource() throws Exception {
        return null;
    }

    @Override
    public Object[][] select(String sql) throws Exception {
        return Helper.select(sql);
    }

    @Override
    public void execute(String sql) throws Exception {
        Helper.execute(sql);
    }

    @Override
    public Object selectSingleValue(String sql) throws Exception {
        return Helper.selectSingleValue(sql);
    }

    @Override
    public void update(String sql) throws Exception {
        Helper.update(sql);
    }

    @Override
    public int getNumberOfRows(String sql) {
       return Helper.getNumberOfRows(sql);
    }

    @Override
    public Object[][] selectPage(String sql, int desdeFila, int numeroFilas) throws Exception {
        return Helper.selectPage(sql, desdeFila, numeroFilas);
    }

    @Override
    public void transact(SQLTransaction t) throws Exception {

        Helper.transact(t);

    }

    @Override
    public void notransact(SQLTransaction t) throws Exception {
        Helper.notransact(t);
    }

    @Override
    public UserData authenticate(String login, String password) throws Exception {
        if (login == null || "".equals(login.trim()) || password == null || "".equals(password.trim())) throw new Exception("Username and password are required");

        UserData d = new UserData();
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                if (em.createQuery("select x.login from User x").getResultList().size() == 0) {
                    Populator.populate();
                }


                User u = em.find(User.class, login.toLowerCase().trim());
                if (u != null) {
                    if (u.getPassword() == null) throw new Exception("Missing password for user " + login);
                    if (!password.trim().equalsIgnoreCase(u.getPassword().trim())) throw new Exception("Wrong password");
                    if (USER_STATUS.INACTIVE.equals(u.getStatus())) throw new Exception("Deactivated user");
                    d.setName(u.getName());
                    d.setEmail(u.getEmail());
                    d.setLogin(login);
                    for (Permission p : u.getPermissions()) d.getPermissions().add(Math.toIntExact(p.getId()));
                } else throw new Exception("No user with login " + login);
            }
        });
        return d;
    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword) throws Exception {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                User u = em.find(User.class, login.toLowerCase().trim());
                if (u != null) {
                    if (!oldPassword.trim().equalsIgnoreCase(u.getPassword().trim())) throw new Exception("Wrong old password");
                    u.setPassword(newPassword);
                } else throw new Exception("No user with login " + login);
            }
        });
    }

    @Override
    public void updateProfile(String login, String name, String email, FileLocator foto) throws Exception {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                User u = em.find(User.class, login.toLowerCase().trim());
                if (u != null) {
                    u.setName(name);
                    u.setEmail(email);
                } else throw new Exception("No user with login " + login);
            }
        });
    }

    @Override
    public String getXslfoForListing() throws Exception {
        String[] s = {""};
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                s[0] = AppConfig.get(em).getXslfoForList();
            }
        });
        return s[0];
    }
}
