package com.tairanchina.csp.dew.core.h2;

import com.tairanchina.csp.dew.core.h2.entity.MQJOB;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;


/**
 * Created by hzlizx on 2018/7/31 0031
 */
public class H2Utils {
    private static final Logger logger = LoggerFactory.getLogger(H2Utils.class);

    private static JdbcConnectionPool jdbcConnectionPool;

    private static boolean inited = false;

    /**
     * 初始化H2数据库
     * @param url       地址（默认 jdbc:h2:data/cluster）
     * @param user      用户名（默认 default_user）
     * @param password  密码（默认 default_password）
     */
    public static void init(String url, String user, String password) throws SQLException {
        if(StringUtils.isNullOrEmpty(url)){
            url = "jdbc:h2:./data/cluster";
        }
        if(StringUtils.isNullOrEmpty(user)){
            user = "default_user";
        }
        if(StringUtils.isNullOrEmpty(password)){
            password = "default_password";
        }
        jdbcConnectionPool = JdbcConnectionPool.create(url, user, password);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = jdbcConnectionPool.getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rsTables = meta.getTables(null, null, "MQ_JOB_1",
                    new String[] { "TABLE" });
            if (!rsTables.next()) {
                stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS MQ_JOB_1(JOB_ID VARCHAR(1024),STATUS VARCHAR(1024),MSG TEXT,CREATED_TIME TIMESTAMP ,PRIMARY KEY(JOB_ID))");
            }
            rsTables.close();
            inited = true;
        } finally {
            releaseConnection(conn, stmt, null);
        }


    }

    public static JdbcConnectionPool getJdbcConnectionPool() {
        return jdbcConnectionPool;
    }

    public static void setJdbcConnectionPool(JdbcConnectionPool jdbcConnectionPool) {
        H2Utils.jdbcConnectionPool = jdbcConnectionPool;
    }


    public static boolean createJob(String jobId,String status,String msg) throws SQLException {
        if(!inited){
            throw new SQLException("H2 not initialized");
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = jdbcConnectionPool.getConnection();
            stmt = conn
                    .prepareStatement("INSERT INTO MQ_JOB VALUES(?,?,?,?)");
            stmt.setString(1, jobId);
            stmt.setString(2, status);
            stmt.setString(3, msg);
            stmt.setDate(4, new Date(System.currentTimeMillis()));
            return stmt.execute();
        } finally {
            releaseConnection(conn, stmt, null);
        }
    }

    public static boolean deleteJob(String jobId) throws SQLException {
        if(!inited){
            throw new SQLException("H2 not initialized");
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = jdbcConnectionPool.getConnection();
            stmt = conn
                    .prepareStatement("DELETE FROM MQ_JOB WHERE JOB_ID=?");
            stmt.setString(1, jobId);
            return stmt.execute();
        } finally {
            releaseConnection(conn, stmt, null);
        }
    }


    public static MQJOB getJob(String jobId) throws SQLException {
        if(!inited){
            throw new SQLException("H2 not initialized");
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = jdbcConnectionPool.getConnection();
            stmt = conn
                    .prepareStatement("SELECT * FROM MQ_JOB WHERE JOB_ID=?");
            stmt.setString(1, jobId);
            rs = stmt.executeQuery();
            if(rs.next()){
                MQJOB mqjob = new MQJOB();
                mqjob.setJOB_ID(rs.getString(1));
                mqjob.setSTATUS(rs.getString(2));
                mqjob.setMSG(rs.getString(3));
                mqjob.setCREATED_TIME(rs.getDate(4));
                return mqjob;
            }else {
                return null;
            }
        } finally {
            releaseConnection(conn, stmt, null);
        }
    }

    public static MQJOB getLastJob() throws SQLException {
        if(!inited){
            throw new SQLException("H2 not initialized");
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = jdbcConnectionPool.getConnection();
            stmt = conn
                    .prepareStatement("SELECT * FROM MQ_JOB ORDER BY CREATED_TIME DESC LIMIT 0,1");
            rs = stmt.executeQuery();
            if(rs.next()){
                MQJOB mqjob = new MQJOB();
                mqjob.setJOB_ID(rs.getString(1));
                mqjob.setSTATUS(rs.getString(2));
                mqjob.setMSG(rs.getString(3));
                mqjob.setCREATED_TIME(rs.getDate(4));
                return mqjob;
            }else {
                return null;
            }
        } finally {
            releaseConnection(conn, stmt, null);
        }
    }

    private static void releaseConnection(Connection conn, Statement stmt,
                                          ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }
}
