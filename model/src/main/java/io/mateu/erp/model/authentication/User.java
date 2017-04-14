package io.mateu.erp.model.authentication;

import io.mateu.erp.model.common.File;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for users of our erp. It can be an internal user or a user created for a customer or a supplier
 *
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Table(name = "_USER")
@Getter@Setter
public class User implements WithTriggers {

    @Embedded
    @Output
    private Audit audit = new Audit();

    /**
     * login must always be uppercase. It is the primary key.
     */
    @Id
    @ListColumn("Login")
    @Unmodifiable
    @Required
    private String login;

    @ListColumn("Name")
    @Required
    private String name;

    @ListColumn("Email")
    @Required
    private String email;

    @Ignored
    private String password;

    @ListColumn("Status")
    @Required
    private USER_STATUS status;


    @OneToMany
    @Ignored
    private List<Permission> permissions = new ArrayList<Permission>();


    @Ignored
    @ManyToOne
    private File photo;


    @Action(name = "test est. 1")
    public static void testEstatico1(@Required@Caption("a") String a, @Caption("b")String b) {
        System.out.println("testEstatico1(" + a + ", " + b + ")");
    }

    @Action(name = "test est. 2")
    public static String testEstatico2(String a, String b) {
        System.out.println("testEstatico2(" + a + ", " + b + ")");
        return "" + a + b;
    }

    @Action(name = "test 1")
    public void test1(String a, String b) {
        System.out.println("test1(" + a + ", " + b + ")");
    }

    @Action(name = "test 2")
    public String test2(String a, String b) {
        System.out.println("test2(" + a + ", " + b + ")");
        return "" + a + b;
    }

    @Override
    public void beforeSet(EntityManager em, boolean isNew) throws Exception {

    }

    @Override
    public void afterSet(EntityManager em, boolean isNew) throws Exception {
        setPassword("1");
    }

    @Override
    public void beforeDelete(EntityManager em) throws Exception {

    }

    @Override
    public void afterDelete(EntityManager em) throws Exception {

    }
}
