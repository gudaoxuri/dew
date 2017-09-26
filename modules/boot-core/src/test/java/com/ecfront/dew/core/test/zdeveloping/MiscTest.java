package com.ecfront.dew.core.test.zdeveloping;

import com.ecfront.dew.common.$;
import com.ecfront.dew.core.Dew;
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
