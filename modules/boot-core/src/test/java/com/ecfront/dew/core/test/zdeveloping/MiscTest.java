package com.ecfront.dew.core.test.zdeveloping;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.ecfront.dew.common.$;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.jdbc.DS;
import com.ecfront.dew.core.jdbc.dialect.DialectType;
import org.junit.Assert;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MiscTest {

    public void testReplaceAll() {
        String str = "select * from t_test_crud_s_entity where 1 =1 and  field_a= #{ fieldA } and field_c = #{fc} order by code desc";
        str = str.replaceAll("((and)|(or)|(AND)|(OR))(\\s*\\S*)*\\#(\\s*\\S*)*\\}", "");
        System.out.println(str);
    }

    public void testSqlReplace() {
        String sql = "select * from table where" +
                " id = #{b} and ( name1= #{a} or name2 = #{c} or name3 = #{d} ) " +
                "and no1 like #{ ee } and no2 like #{f} ";
        Object[] result = DS.packageSelect(sql, new LinkedHashMap<String, Object>() {{
            put("a", "1");
            put("c", "3");
            put("ee", "5");
        }}, DialectType.H2);
        System.out.println(result);
    }

    public void testSqlParser() {
        String sql = "select * from table";
        SQLStatementParser parser = new SQLStatementParser(sql);
        SQLSelectStatement statement = (SQLSelectStatement) parser.parseStatementList().get(0);
        SQLExpr sqlExpr = ((SQLSelectQueryBlock) statement.getSelect().getQuery()).getWhere();
        DS.formatWhere(sqlExpr);
        System.out.println(statement.toString());
        sql = "select * from table where  id = #{sss}";
        parser = new SQLStatementParser(sql);
        statement = (SQLSelectStatement) parser.parseStatementList().get(0);
        sqlExpr = ((SQLSelectQueryBlock) statement.getSelect().getQuery()).getWhere();
        DS.formatWhere(sqlExpr);
        System.out.println(statement.toString());
        sql = "select * from table where f1 between #{x} and #{y}";
        parser = new SQLStatementParser(sql);
        statement = (SQLSelectStatement) parser.parseStatementList().get(0);
        sqlExpr = ((SQLSelectQueryBlock) statement.getSelect().getQuery()).getWhere();
        DS.formatWhere(sqlExpr);
        System.out.println(statement.toString());
        sql = "select * from table where" +
                " id = #{sss} and ( name1= ? or name2 = #{aaa} or name3 = ? ) " +
                "and age in (?,?) and no1 like ? and no2 like #{aaa} " +
                "or f1 between #{x} and #{y} or f2 = ?";
        parser = new SQLStatementParser(sql);
        statement = (SQLSelectStatement) parser.parseStatementList().get(0);
        sqlExpr = ((SQLSelectQueryBlock) statement.getSelect().getQuery()).getWhere();
        DS.formatWhere(sqlExpr);
        System.out.println(statement.toString());
        sql = "select * from t1 left join t2 tt on t1.id=t2.rel_id where" +
                " t1.id = #{sss} and ( t1.name1= ? or t1.name2 = #{aaa} or t1.name3 = ? ) " +
                "and tt.age in (?,?) and tt.no1 like ? and tt.no2 like #{aaa} " +
                "or tt.f1 between #{x} and #{y} or tt.f2 = ?";
        parser = new SQLStatementParser(sql);
        statement = (SQLSelectStatement) parser.parseStatementList().get(0);
        sqlExpr = ((SQLSelectQueryBlock) statement.getSelect().getQuery()).getWhere();
        DS.formatWhere(sqlExpr);
        System.out.println(statement.toString());
    }

    public void testError() {
        Map<String, String> a = new HashMap<>();
        a.put("1", "1");
        Dew.E.checkNotEmpty(a, new RuntimeException());
        a.clear();
        Dew.E.checkNotEmpty(a, Dew.E.e("1001", new RuntimeException("")));
        a.put("1", "1");
    }


    public void testInvokeAll() throws InterruptedException {
        int taskNum = 1000;
        int tempNum = taskNum;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Callable<String>> tasks = new ArrayList<>();
        while (tempNum-- > 0) {
            tasks.add(() -> {
                Thread.sleep(new Random().nextInt(100));
                return $.field.createUUID();
            });
        }
        List<Future<String>> result = executorService.invokeAll(tasks);
        Set<String> finalResult = result.stream().map(r -> {
            try {
                return r.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toSet());
        Assert.assertEquals(taskNum, finalResult.size());
    }

}
