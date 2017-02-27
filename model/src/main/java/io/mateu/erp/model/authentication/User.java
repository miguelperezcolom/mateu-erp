package io.mateu.erp.model.authentication;

import io.mateu.ui.mdd.server.annotations.*;
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
public class User {

    @Embedded
    @Output
    private Audit audit = new Audit();

    /**
     * login must always be uppercase. It is the primary key.
     */
    @Id
    @ListColumn("Login")
    private String login;

    @ListColumn("Name")
    @Unmodifiable
    private String name;

    @ListColumn("Email")
    private String email;

    @Ignored
    private String password;

    @ListColumn("Status")
    private USER_STATUS status;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Grant> grants = new ArrayList<Grant>();


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

}
