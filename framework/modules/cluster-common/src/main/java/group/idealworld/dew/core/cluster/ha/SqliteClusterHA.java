package group.idealworld.dew.core.cluster.ha;

import com.ecfront.dew.common.$;
import group.idealworld.dew.core.cluster.dto.MessageWrap;
import group.idealworld.dew.core.cluster.ha.dto.HAConfig;
import group.idealworld.dew.core.cluster.ha.entity.PrepareCommitMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 集群HA处理 H2 实现.
 *
 * @author gudaoxuri
 */
public class SqliteClusterHA implements ClusterHA {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqliteClusterHA.class);

    private static SQLiteConnectionPoolDataSource dataSource;

    private static boolean update(String sql, Object... params) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= params.length; i++) {
                stmt.setObject(i, params[i - 1]);
            }
            return stmt.execute();
        }
    }

    private static List<PrepareCommitMsg> queryList(String sql, Object... params) throws SQLException {
        ResultSet rs = null;
        try (Connection conn = dataSource.getConnection();
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
            prepareCommitMsg.setMsg($.json.toObject(rs.getString(3), MessageWrap.class));
            prepareCommitMsg.setCreatedTime(rs.getDate(4));
            jobs.add(prepareCommitMsg);
        }
        return jobs;
    }

    @Override
    public void init(HAConfig haConfig) throws SQLException {
        dataSource = new SQLiteConnectionPoolDataSource();
        dataSource.setUrl("jdbc:sqlite:sample.db");
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS MQ_MSG("
                    + "ADDR VARCHAR(1024)," + "MSG_ID VARCHAR(32)," + "MSG TEXT," + "CREATED_TIME TIMESTAMP ,"
                    + "PRIMARY KEY(MSG_ID)" + ")");
        }
    }

    @Override
    public String mqAfterPollMsg(String addr, MessageWrap msg) {
        String sql = "INSERT INTO MQ_MSG VALUES(?,?,?,?)";
        Date date = new Date(System.currentTimeMillis());
        try {
            String uuid = $.field.createUUID();
            update(sql, addr, uuid, $.json.toJsonString(msg), date);
            return uuid;
        } catch (SQLException e) {
            LOGGER.error("Create HA job error.", e);
            return "0";
        }
    }

    @Override
    public void mqAfterMsgAcked(String id) {
        String sql = "DELETE FROM MQ_MSG WHERE MSG_ID = ?";
        try {
            update(sql, id);
        } catch (SQLException e) {
            LOGGER.error("Delete HA job error.", e);
        }
    }

    @Override
    public List<PrepareCommitMsg> mqFindAllUnCommittedMsg(String addr) {
        String sql = "SELECT * FROM MQ_MSG where ADDR = ? ORDER BY CREATED_TIME DESC";
        try {
            return queryList(sql, addr);
        } catch (SQLException e) {
            LOGGER.error("Query HA job error.", e);
            return new ArrayList<>();
        }
    }

}
