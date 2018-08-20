package io.mateu.erp.model.world;

import io.mateu.mdd.core.model.common.File;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.annotations.Index;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Entity
@Getter@Setter
public class PostalCode {

    @Id
    private String id;

    @Index
    private String countryCode;

    @Index
    private String postalCode;

    private String placeName;
    // state
    private String adminName1;
    private String adminCode1;
    // county / province
    private String adminName2;
    private String adminCode2;
    // community
    private String adminName3;
    private String adminCode3;
    private String latitude;
    private String longitude;
    private String accuracy;


    @Action
    public static void importFromFile(EntityManager em, File file) {

    }

    public static void importFromStream(EntityManager em, InputStream s) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(s));

        int pos = 0;
        String l;
        while ((l = r.readLine()) != null) {

            System.out.print("" + pos++ + ":" + l);

            String[] ts = l.split("\\t");

            if (ts.length == 12) {
                String id = ts[0].toLowerCase() + "-" + ts[1].toLowerCase();
                PostalCode c = em.find(PostalCode.class, id);
                if (c == null) {
                    c = new PostalCode();
                    c.setId(id);
                    em.persist(c);
                }

                c.setCountryCode(ts[0]);
                c.setPostalCode(ts[1]);
                c.setPlaceName(ts[2]);

                c.setAdminName1(ts[3]);
                c.setAdminCode1(ts[4]);
                c.setAdminName2(ts[5]);
                c.setAdminCode2(ts[6]);
                c.setAdminName3(ts[7]);
                c.setAdminCode3(ts[8]);
                c.setLatitude(ts[9]);
                c.setLongitude(ts[10]);
                c.setAccuracy(ts[11]);

                System.out.println(" > OK");
            } else {
                System.out.println(" > No tiene 12 columnas");
            }

        }
        r.close();
    }


    public static void main(String... args) throws Throwable {

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                PostalCode.importFromStream(em, PostalCode.class.getResourceAsStream("/postalcodes/allCountries.txt"));

            }
        });

    }
}
