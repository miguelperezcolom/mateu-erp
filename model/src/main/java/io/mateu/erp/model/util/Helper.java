package io.mateu.erp.model.util;


import io.mateu.ui.core.server.SQLTransaction;
import io.mateu.ui.core.server.Utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 13/9/16.
 */
public class Helper {

    private static DataSource dataSource;
    private static EntityManagerFactory emf;


    public static void transact(SQLTransaction t) throws Exception {

        transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                t.run(em.unwrap(Connection.class));

            }
        });

    }



    public static void transact(JPATransaction t) throws Exception {

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

    public static void notransact(SQLTransaction t) throws Exception {

        transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                t.run(em.unwrap(Connection.class));

            }
        });

    }
    public static void notransact(JPATransaction t) throws Exception {

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


    public static Object[][] select(String sql) throws Exception {
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


    public static void execute(String sql) throws Exception {

        transact(new SQLTransaction() {
            @Override
            public void run(Connection conn) throws Exception {

                Statement s = conn.createStatement();
                s.execute(sql);

            }
        });

    }

    public static Object selectSingleValue(String sql) throws Exception {
        Object o = null;
        Object[][] r = select(sql);
        if (r.length > 0 && r[0].length > 0) o = r[0][0];
        return o;
    }


    public static void update(String sql) throws Exception {

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return total;
    }


    public static Object[][] selectPage(String sql, int desdeFila, int numeroFilas) throws Exception {
        return select(sql + " LIMIT " + numeroFilas + " OFFSET " + desdeFila);
    }
}
