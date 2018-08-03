package com.tairanchina.csp.dew.core.h2;

import com.tairanchina.csp.dew.core.h2.entity.Job;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.function.Consumer;


/**
 * Created by hzlizx on 2018/7/31 0031
 */
public class H2Utils {
    private static final Logger logger = LoggerFactory.getLogger(H2Utils.class);

    private static JdbcConnectionPool jdbcConnectionPool;

    private static boolean inited = false;

    /**
     * 初始化H2数据库
     *
     * @param url      地址（默认 jdbc:h2:data/cluster）
     * @param user     用户名（默认 default_user）
     * @param pd       密码（默认 default_password）
     */
    public static void init(String url, String user, String pd) throws SQLException {
        if (StringUtils.isNullOrEmpty(url)) {
            url = "jdbc:h2:./data/cluster";
        }
        if (StringUtils.isNullOrEmpty(user)) {
            user = "default_user";
        }
        if (StringUtils.isNullOrEmpty(pd)) {
            pd = "default_password";
        }
        jdbcConnectionPool = JdbcConnectionPool.create(url, user, pd);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = jdbcConnectionPool.getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rsTables = meta.getTables(null, null, "MQ_JOB_2",
                    new String[]{"TABLE"});
            if (!rsTables.next()) {
                stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS MQ_JOB_2(ADDRESS VARCHAR(1024),JOB_ID VARCHAR(1024),STATUS VARCHAR(1024),MSG TEXT,CREATED_TIME TIMESTAMP ,PRIMARY KEY(JOB_ID))");
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


    public static boolean createJob(String address, String jobId, String status, String msg) throws SQLException {
        if (!inited) {
            throw new SQLException("H2 not initialized");
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = jdbcConnectionPool.getConnection();
            stmt = conn
                    .prepareStatement("INSERT INTO MQ_JOB_2 VALUES(?,?,?,?,?)");
            stmt.setString(1, address);
            stmt.setString(2, jobId);
            stmt.setString(3, status);
            stmt.setString(4, msg);
            stmt.setDate(5, new Date(System.currentTimeMillis()));
            return stmt.execute();
        } finally {
            releaseConnection(conn, stmt, null);
        }
    }

    public static boolean deleteJob(String jobId) throws SQLException {
        if (!inited) {
            throw new SQLException("H2 not initialized");
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = jdbcConnectionPool.getConnection();
            stmt = conn
                    .prepareStatement("DELETE FROM MQ_JOB_2 WHERE JOB_ID=?");
            stmt.setString(1, jobId);
            return stmt.execute();
        } finally {
            releaseConnection(conn, stmt, null);
        }
    }


    public static Job getJob(String jobId) throws SQLException {
        if (!inited) {
            throw new SQLException("H2 not initialized");
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = jdbcConnectionPool.getConnection();
            stmt = conn
                    .prepareStatement("SELECT * FROM MQ_JOB_2 WHERE JOB_ID=?");
            stmt.setString(1, jobId);
            rs = stmt.executeQuery();
            return convertResultSetToJob(rs);
        } finally {
            releaseConnection(conn, stmt, null);
        }
    }

    public static Job getLastJob(String address) throws SQLException {
        if (!inited) {
            throw new SQLException("H2 not initialized");
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = jdbcConnectionPool.getConnection();
            stmt = conn
                    .prepareStatement("SELECT * FROM MQ_JOB_2 where ADDRESS = ? ORDER BY CREATED_TIME DESC LIMIT 0,1");
            stmt.setString(1, address);
            rs = stmt.executeQuery();
            return convertResultSetToJob(rs);
        } finally {
            releaseConnection(conn, stmt, null);
        }
    }

    public static void runH2Job(String address, Consumer<String> consumer) {
        do {
            try {
                Job lastJob = H2Utils.getLastJob(address);
                if (lastJob != null) {
                    logger.info("accept message from h2");
                    consumer.accept(lastJob.getMsg());
                    H2Utils.deleteJob(lastJob.getJobId());
                } else {
                    logger.info("message from h2 clean");
                    break;
                }
            } catch (SQLException e) {
                logger.error("h2 job run error", e);
                break;
            }
        } while (true);
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

    private static Job convertResultSetToJob(ResultSet rs) throws SQLException {
        if (rs == null) {
            return null;
        }
        if (rs.next()) {
            Job job = new Job();
            job.setAddress(rs.getString(1));
            job.setJobId(rs.getString(2));
            job.setStatus(rs.getString(3));
            job.setMsg(rs.getString(4));
            job.setCreatedTime(rs.getDate(5));
            return job;
        } else {
            return null;
        }
    }
}
