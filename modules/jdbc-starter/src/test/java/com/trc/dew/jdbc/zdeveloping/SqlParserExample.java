package com.trc.dew.jdbc.zdeveloping;


import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 开发时测试，暂时保留
 */
public class SqlParserExample {

    public static void main(String[] args) {

        Assert.assertTrue(true);

        String sql = "SELECT *\n" +
                "FROM app_version a\n" +
                "WHERE a.name = #{name} AND a.inner_number = #{innerNumber} " +
                "AND a.force_number = #{forceNumber} AND a.device_type = #{deviceType} AND a.app_flag = #{appFlag}";

        String sql2 = "SELECT n.*,r2.*,r.*\n" +
                "FROM node n \n" +
                "INNER JOIN node_relation r ON n.id = r.from_id \n" +
                "LEFT JOIN node_relation2 r2 ON n.id = r.from_id \n" +
                "WHERE n.type = #{type} AND n.level = #{level} AND r.from_id = #{fromId} AND r.to_id = #{toId}";

        String sql3 = "SELECT *\n" +
                "FROM app_version\n" +
                "WHERE device_type = #{deviceType} AND app_flag = #{appFlag}\n" +
                "ORDER BY inner_number DESC\n" +
                "LIMIT 1";

        String sql4 = "SELECT * FROM product ORDER BY created_time DESC ";

        String sql5 ="SELECT\n" +
                "\ttt.*, hotel_count,\n" +
                "\troom_count,\n" +
                "\tac_count\n" +
                "FROM\n" +
                "\t(\n" +
                "\t\tSELECT\n" +
                "\t\t\ta. NAME AS province,\n" +
                "\t\t\tSUM(s.elec_csum) AS elec_csum,\n" +
                "\t\t\tSUM(s.ac_runtime) AS ac_runtime_mils,\n" +
                "\t\t\tSUM(s.virtual_elec_csum) AS virtual_elec_csum\n" +
                "\t\tFROM\n" +
                "\t\t\tsys_area a\n" +
                "\t\tLEFT JOIN hotel h ON h.province = a. NAME\n" +
                "\t\tLEFT JOIN hotel_building b ON h.id = b.hotel_id\n" +
                "\t\tLEFT JOIN hotel_room r ON r.hotel_building_id = b.id\n" +
                "\t\tLEFT JOIN stats_room_energy_day s ON s.room_id = r.id\n" +
                "\t\tAND (\n" +
                "\t\t\t(\n" +
                "\t\t\t\ts. YEAR = 2017\n" +
                "\t\t\t\tAND s. MONTH = 1\n" +
                "\t\t\t\tAND s. DAY = 2\n" +
                "\t\t\t\tAND s. HOUR >= 3\n" +
                "\t\t\t) OR (\n" +
                "\t\t\t\ts. YEAR = 2017\n" +
                "\t\t\t\tAND s. MONTH = 1\n" +
                "\t\t\t\tAND s. DAY = 2\n" +
                "\t\t\t\tAND s. HOUR <= 10\n" +
                "\t\t\t)\n" +
                "\t\t)\n" +
                "\t\tWHERE\n" +
                "\t\t\ta.area_type = 2\n" +
                "\t\tGROUP BY\n" +
                "\t\t\ta. NAME\n" +
                "\t) AS tt\n" +
                "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tprovince,\n" +
                "\t\tCOUNT(DISTINCT hotel_name) AS hotel_count,\n" +
                "\t\tSUM(room_count) AS room_count,\n" +
                "\t\tSUM(ac_count) AS ac_count\n" +
                "\tFROM\n" +
                "\t\t(\n" +
                "\t\t\tSELECT\n" +
                "\t\t\t\tprovince,\n" +
                "\t\t\t\thotel_name,\n" +
                "\t\t\t\tcount(DISTINCT room_id) AS room_count,\n" +
                "\t\t\t\tSUM(ac_count) AS ac_count\n" +
                "\t\t\tFROM\n" +
                "\t\t\t\t(\n" +
                "\t\t\t\t\tSELECT\n" +
                "\t\t\t\t\t\ta. NAME AS province,\n" +
                "\t\t\t\t\t\th.hotel_name,\n" +
                "\t\t\t\t\t\tr.id AS room_id,\n" +
                "\t\t\t\t\t\tcount(d.id) AS ac_count\n" +
                "\t\t\t\t\tFROM\n" +
                "\t\t\t\t\t\tsys_area a\n" +
                "\t\t\t\t\tLEFT JOIN hotel h ON h.province = a. NAME\n" +
                "\t\t\t\t\tLEFT JOIN hotel_building b ON h.id = b.hotel_id\n" +
                "\t\t\t\t\tLEFT JOIN hotel_room r ON r.hotel_building_id = b.id\n" +
                "\t\t\t\t\tLEFT JOIN ac_device d ON r.id = d.hotel_room_id\n" +
                "\t\t\t\t\tWHERE\n" +
                "\t\t\t\t\t\ta.area_type = 2\n" +
                "\t\t\t\t\tGROUP BY\n" +
                "\t\t\t\t\t\tprovince,\n" +
                "\t\t\t\t\t\thotel_name,\n" +
                "\t\t\t\t\t\tr.id,\n" +
                "\t\t\t\t\t\td.id\n" +
                "\t\t\t\t) t1\n" +
                "\t\t\tGROUP BY\n" +
                "\t\t\t\tprovince,\n" +
                "\t\t\t\thotel_name\n" +
                "\t\t) t2\n" +
                "\tGROUP BY\n" +
                "\t\tprovince\n" +
                ") AS t3 ON tt.province = t3.province";
        SQLSelectStatement statement = (SQLSelectStatement) new MySqlStatementParser(sql3).parseSelect();
        SQLExpr sqlExpr = ((SQLSelectQueryBlock) statement.getSelect().getQuery()).getWhere();
        SQLTableSource sqlTableSource = ((SQLSelectQueryBlock) statement.getSelect().getQuery()).getFrom();
        List<SQLSelectItem> selectList = ((SQLSelectQueryBlock) statement.getSelect().getQuery()).getSelectList();
        List<SQLSelectItem> addList = new ArrayList<>();
        SqlParserExample.formatFrom(sqlTableSource, selectList, addList);
        selectList.addAll(addList);
        String result = statement.toString();
        System.out.println(result);


    }

    private static void formatFrom(SQLTableSource sqlTableSource, List<SQLSelectItem> selectList, List<SQLSelectItem> addList) {
        if (sqlTableSource == null) {
            return;
        }
        if (sqlTableSource instanceof SQLExprTableSource) {
            doFormat((SQLExprTableSource) sqlTableSource, selectList, addList);
        }
        if (sqlTableSource instanceof SQLJoinTableSource) {
            formatFrom(((SQLJoinTableSource) sqlTableSource).getRight(), selectList, addList);
            formatFrom(((SQLJoinTableSource) sqlTableSource).getLeft(), selectList, addList);
        }
    }

    private static void doFormat(SQLExprTableSource sqlTableSource, List<SQLSelectItem> selectList, List<SQLSelectItem> addList) {
        System.out.println("表名：    " + sqlTableSource.getExpr() + "         别名：     " + sqlTableSource.getAlias());
        Iterator<SQLSelectItem> iterator = selectList.iterator();
        while (iterator.hasNext()) {
            SQLSelectItem sqlSelectItem = iterator.next();
            if (sqlSelectItem.getExpr() instanceof SQLPropertyExpr) {
                SQLPropertyExpr expr = (SQLPropertyExpr) sqlSelectItem.getExpr();
                SQLIdentifierExpr expr_owner = (SQLIdentifierExpr) expr.getOwner();
                if ((expr_owner.getName() + "." + expr.getName()).equals(sqlTableSource.getAlias() + ".*")) {
                    iterator.remove();
                    addList.add(new SQLSelectItem(new SQLPropertyExpr(expr_owner.getName(), "hehe1")));
                    addList.add(new SQLSelectItem(new SQLPropertyExpr(expr_owner.getName(), "hehe2")));
                    addList.add(new SQLSelectItem(new SQLPropertyExpr(expr_owner.getName(), "hehe3")));
                    addList.add(new SQLSelectItem(new SQLPropertyExpr(expr_owner.getName(), "hehe4")));
                }

            } else if (sqlSelectItem.getExpr() instanceof SQLObjectImpl) {

                System.out.println();
            }
        }

    }

    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == '_') {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


}
