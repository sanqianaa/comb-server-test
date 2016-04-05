package com.netease.napm.test.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.netease.napm.test.utils.Const;

/**
 * @Description
 * @author hzdaiyuan
 * @date 2016-3-30 下午3:05:46
 */

@Service
public class DbService implements NapmCombService {
    private static final Logger logger = Logger.getLogger(DbService.class);
    private static final String SQL_EXAMPLE = "SHOW TABLES;";
    private static final String ERROR_EXAMPLE = "SELECT FROM DDB";

    /**
     * 
     * @Title: invoke
     * @Description: invoke remote db service
     * @param url
     * @return invoke result
     * @throws ClassNotFoundException
     * @throws Exception
     */
    @Override
    public boolean invoke(String url, String dataType) throws Exception {
        return invoke(url, SQL_EXAMPLE, dataType);
    }

    public boolean invoke(String url, String sql, String dataType) throws SQLException, ClassNotFoundException {
        logger.info(String.format("executeSQL>>>URL:%s | SQL:%s | dataType:%s", url, sql, dataType));
        return executeSql(url, sql, dataType);
    }

    private boolean executeSql(String url, String sql, String dataType) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");// load mysql driver
            conn = DriverManager.getConnection(url, "root", "root");// get sql
                                                                    // connection
            Statement stmt = conn.createStatement();
            if (Const.QUERY.equals(dataType)) {
                return executeQuery(stmt, sql);
            } else if (Const.UPDATE.equals(dataType)) {
                return executeUpdate(stmt, sql);
            } else {
                /**
                 * other
                 */
                return execute(stmt, sql);
            }

        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private boolean execute(Statement stmt, String sql) throws SQLException {
        return stmt.execute(sql);
    }

    private boolean executeQuery(Statement stmt, String sql) throws SQLException {
        ResultSet rs = stmt.executeQuery(sql);// execute query sql
        boolean tag = rs != null;
        while (rs.next()) {
            logger.info("db query result:" + rs.getString(1));
        }
        return tag;
    }

    private boolean executeUpdate(Statement stmt, String sql) throws SQLException {
        return stmt.executeUpdate(sql) > 0;// execute query sql
    }

    /**
     * @Title: main
     * @Description: TODO
     * @Author: hzdaiyuan
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        DbService dbService = new DbService();
        dbService
                .invoke("jdbc:mysql://localhost:3306/napm_integration_ddb?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true",
                        ERROR_EXAMPLE, "");
    }
}
