package com.tairanchina.csp.dew.core.cluster.ha;

import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.core.cluster.ha.dto.HAConfig;
import com.tairanchina.csp.dew.core.cluster.ha.entity.PrepareCommitMsg;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class H2ClusterHA implements ClusterHA {

    private static final Logger logger = LoggerFactory.getLogger(H2ClusterHA.class);

    private static JdbcConnectionPool jdbcConnectionPool;

    @Override
    public void init(HAConfig haConfig) throws SQLException {
        String url = "jdbc:h2:" + haConfig.getStoragePath() + haConfig.getStorageName() + ";DB_CLOSE_ON_EXIT=FALSE";
        jdbcConnectionPool = JdbcConnectionPool.create(url, haConfig.getAuthUsername() == null ? "" : haConfig.getAuthUsername(), haConfig.getAuthPassword() == null ? "" : haConfig.getAuthPassword());
        try (Connection conn = jdbcConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS MQ_MSG(ADDR VARCHAR(1024),MSG_ID VARCHAR(32),MSG TEXT,CREATED_TIME TIMESTAMP ,PRIMARY KEY(MSG_ID))");
        }
    }

    @Override
    public String mq_afterPollMsg(String addr, String msg) {
        String sql = "INSERT INTO MQ_MSG VALUES(?,?,?,?)";
        Date date = new Date(System.currentTimeMillis());
        try {
            String uuid = $.field.createUUID();
            update(sql, addr, uuid, msg, date);
            return uuid;
        } catch (SQLException e) {
            logger.error("Create HA job error.", e);
            return "0";
        }
    }

    @Override
    public void mq_afterMsgAcked(String id) {
        String sql = "DELETE FROM MQ_MSG WHERE MSG_ID = ?";
        try {
            update(sql, id);
        } catch (SQLException e) {
            logger.error("Delete HA job error.", e);
        }
    }

    @Override
    public List<PrepareCommitMsg> mq_findAllUnCommittedMsg(String addr) {
        String sql = "SELECT * FROM MQ_MSG where ADDR = ? ORDER BY CREATED_TIME DESC";
        try {
            return queryList(sql, addr);
        } catch (SQLException e) {
            logger.error("Query HA job error.", e);
            return new ArrayList<>();
        }
    }

    private static boolean update(String sql, Object... params) throws SQLException {
        try (Connection conn = jdbcConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= params.length; i++) {
                stmt.setObject(i, params[i - 1]);
            }
            return stmt.execute();
        }
    }

    private static List<PrepareCommitMsg> queryList(String sql, Object... params) throws SQLException {
        ResultSet rs = null;
        try (Connection conn = jdbcConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= params.length; i++) {
                stmt.setObject(i, params[i - 1]);
            }
            rs = stmt.executeQuery();
            return convertResultSetToJob(rs);
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
    }

    private static List<PrepareCommitMsg> convertResultSetToJob(ResultSet rs) throws SQLException {
        if (rs == null) {
            return null;
        }
        List<PrepareCommitMsg> jobs = new ArrayList<>();
        while (rs.next()) {
            PrepareCommitMsg prepareCommitMsg = new PrepareCommitMsg();
            prepareCommitMsg.setAddr(rs.getString(1));
            prepareCommitMsg.setMsgId(rs.getString(2));
            prepareCommitMsg.setMsg(rs.getString(3));
            prepareCommitMsg.setCreatedTime(rs.getDate(4));
            jobs.add(prepareCommitMsg);
        }
        return jobs;
    }

}
