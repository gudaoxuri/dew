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
     * @param url  地址（默认 jdbc:h2:data/cluster）
     * @param user 用户名（默认 default_user）
     * @param pwd  密码（默认 default_password）
     */
    public static void init(String url, String user, String pwd) throws SQLException {
        if (StringUtils.isNullOrEmpty(url)) {
            url = "jdbc:h2:./data/ha";
        }
        if (StringUtils.isNullOrEmpty(user)) {
            user = "default_user";
        }
        if (StringUtils.isNullOrEmpty(pwd)) {
            pwd = "default_password";
        }
        jdbcConnectionPool = JdbcConnectionPool.create(url, user, pwd);
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
        String sql = "INSERT INTO MQ_JOB_2 VALUES(?,?,?,?,?)";
        Date date = new Date(System.currentTimeMillis());
        return execute(sql, address, jobId, status, msg,date);
    }

    public static boolean deleteJob(String jobId) throws SQLException {
        String sql = "DELETE FROM MQ_JOB_2 WHERE JOB_ID=?";
        return execute(sql, jobId);
    }


    public static Job getJob(String jobId) throws SQLException {
        String sql = "SELECT * FROM MQ_JOB_2 WHERE JOB_ID=?";
        return executeReturn(sql, jobId);
    }

    public static Job getLastJob(String address) throws SQLException {
        String sql = "SELECT * FROM MQ_JOB_2 where ADDRESS = ? ORDER BY CREATED_TIME DESC LIMIT 0,1";
        return executeReturn(sql, address);
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

    private static boolean execute(String sql, Object... params) throws SQLException {
        if (!inited) {
            throw new SQLException("checkpoint not initialized");
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = jdbcConnectionPool.getConnection();
            stmt = conn
                    .prepareStatement(sql);
            for (int i = 1; i <= params.length; i++) {
                stmt.setObject(i, params[i-1]);
            }
            return stmt.execute();
        } finally {
            releaseConnection(conn, stmt, null);
        }
    }
    private static Job executeReturn(String sql, Object... params) throws SQLException {
        if (!inited) {
            throw new SQLException("checkpoint not initialized");
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs;
        try {
            conn = jdbcConnectionPool.getConnection();
            stmt = conn
                    .prepareStatement(sql);
            for (int i = 1; i <= params.length; i++) {
                stmt.setObject(i, params[i-1]);
            }
            rs = stmt.executeQuery();
            return convertResultSetToJob(rs);
        } finally {
            releaseConnection(conn, stmt, null);
        }
    }
}
