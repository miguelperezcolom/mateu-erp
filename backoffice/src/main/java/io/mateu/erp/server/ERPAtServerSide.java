package io.mateu.erp.server;

import io.mateu.erp.model.util.Helper;
import io.mateu.ui.core.server.BaseServerSideApp;
import io.mateu.ui.core.server.SQLTransaction;
import io.mateu.ui.core.server.ServerSideApp;
import io.mateu.ui.core.server.Utils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
}
