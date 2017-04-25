package io.mateu.ui.mdd.server.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.mateu.ui.core.server.SQLTransaction;
import io.mateu.ui.core.server.Utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 13/9/16.
 */
public class Helper {

    private static DataSource dataSource;
    private static EntityManagerFactory emf;

    private static ObjectMapper mapper = new ObjectMapper();

    // Create your Configuration instance, and specify if up to what FreeMarker
// version (here 2.3.25) do you want to apply the fixes that are not 100%
// backward-compatible. See the Configuration JavaDoc for details.
    private static Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);

    {
// Specify the source where the template files come from. Here I set a
// plain directory for it, but non-file-system sources are possible too:
        try {
            cfg.setDirectoryForTemplateLoading(new File(""));
        } catch (IOException e) {
            e.printStackTrace();
        }

// Set the preferred charset template files are stored in. UTF-8 is
// a good choice in most applications:
        cfg.setDefaultEncoding("UTF-8");

// Sets how errors will appear.
// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
        cfg.setLogTemplateExceptions(false);
    }


    public static Map<String, Object> fromJson(String json) throws IOException {
        if (json == null || "".equals(json)) json = "{}";
        return mapper.readValue(json, Map.class);
    }

    public static String toJson(Object o) throws IOException {
        return mapper.writeValueAsString(o);
    }


    public static void transact(SQLTransaction t) throws Throwable {

        transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                t.run(em.unwrap(Connection.class));

            }
        });

    }



    public static void transact(JPATransaction t) throws Throwable {

        EntityManager em = getEMF().createEntityManager();

        try {

            em.getTransaction().begin();

            t.run(em);


            em.getTransaction().commit();


        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
            throw e;
        }

        em.close();

    }

    private static EntityManagerFactory getEMF() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("default");
        }
        return emf;
    }

    public static void notransact(SQLTransaction t) throws Throwable {

        transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                t.run(em.unwrap(Connection.class));

            }
        });

    }
    public static void notransact(JPATransaction t) throws Throwable {

        EntityManager em = getEMF().createEntityManager();

        try {

            t.run(em);

        } catch (Exception e) {
            e.printStackTrace();
            em.close();
            throw e;
        }

        em.close();

    }

    public static String md5(String s) {
        return s;
    }

    public static void setDataSource(DataSource dataSource) {
        Helper.dataSource = dataSource;
    }


    public static Object[][] select(String sql) throws Throwable {
        final Object[][][] r = {null};

        notransact(new SQLTransaction() {
            @Override
            public void run(Connection conn) throws Exception {

                System.out.println("sql: " + sql); //prettySQLFormat(sql));

                Statement s = conn.createStatement();
                ResultSet rs = s.executeQuery(sql);
                if (rs != null) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    List<Object[]> aux = new ArrayList<Object[]>();
                    int fila = 0;
                    while (rs.next()) {
                        if (fila > 0 && fila % 1000 == 0) System.out.println("filas =" + fila + ":SQL>>>" + sql.replaceAll("\\n", " ") + "<<<SQL");
                        fila++;
                        Object[] f = new Object[rsmd.getColumnCount()];
                        for (int i = 0; i < rsmd.getColumnCount(); i++) {
                            f[i] = rs.getObject(i + 1);
                        }
                        aux.add(f);
                    }
                    r[0] = aux.toArray(new Object[0][0]);
                }

            }
        });

        return r[0];
    }


    public static void execute(String sql) throws Throwable {

        transact(new SQLTransaction() {
            @Override
            public void run(Connection conn) throws Exception {

                Statement s = conn.createStatement();
                s.execute(sql);

            }
        });

    }

    public static Object selectSingleValue(String sql) throws Throwable {
        Object o = null;
        Object[][] r = select(sql);
        if (r.length > 0 && r[0].length > 0) o = r[0][0];
        return o;
    }


    public static void update(String sql) throws Throwable {

        transact(new SQLTransaction() {
            @Override
            public void run(Connection conn) throws Exception {
                Statement s = conn.createStatement();
                s.executeUpdate(sql);
            }
        });

    }


    public static int getNumberOfRows(String sql) {
        int total = 0;
        if (!Utils.isEmpty(sql)) {
            try {

                if (sql.contains("/*noenelcount*/")) {
                    String sqlx = "";
                    boolean z = true;
                    for (String s : sql.split("/\\*noenelcount\\*/")) {
                        if (z) sqlx += s;
                        z = !z;
                    }
                    sql = sqlx;
                }

                sql = sql.replaceAll("aquilapaginacion", "");

                String aux = "select count(*) from (" + sql + ") x";
                total = ((Long) selectSingleValue(aux)).intValue();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return total;
    }


    public static Object[][] selectPage(String sql, int desdeFila, int numeroFilas) throws Throwable {
        return select(sql + " LIMIT " + numeroFilas + " OFFSET " + desdeFila);
    }

    public static void touch(Object o, EntityManager em, String login) {

    }

    public static String freemark(String freemarker, Map<String, Object> root) throws IOException, TemplateException {

        long t0 = new Date().getTime();

        //Template temp = cfg.getTemplate("test.ftlh");

        Template temp = new Template("name", new StringReader(freemarker),
                new Configuration());

        /*
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("greetTemplate", "<#macro greet>Hello</#macro>");
        stringLoader.putTemplate("myTemplate", "<#include \"greetTemplate\"><@greet/> World!");
        cfg.setTemplateLoader(stringLoader);
        */

        StringWriter out = new StringWriter(); //new OutputStreamWriter(System.out);
        temp.process(root, out);

        System.out.println("freemarker template compiled and applied in " + (new Date().getTime() - t0) + " ms.");

        return out.toString();
    }

    public static URL whichJar(Class c) {
        return c.getProtectionDomain().getCodeSource().getLocation();
    }
}
