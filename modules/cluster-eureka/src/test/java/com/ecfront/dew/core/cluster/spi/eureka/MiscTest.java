package com.ecfront.dew.core.cluster.spi.eureka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MiscTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testForkJoin();
    }

    public static void testForkJoin() throws ExecutionException, InterruptedException {

        List<String> receives= Arrays.asList("1","2","3","4","1","2","3","4","1","2","3","4","1","2","3","4","1","2","3","4","1","2","3","4","1","2","3","4");
        long start=System.currentTimeMillis();

        List<Boolean> result = new ForkJoinPool(100).submit(() ->
                receives.parallelStream().map(item -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getId());
            return true;
        }).collect(Collectors.toList())).get();


       System.out.println("Use:"+(System.currentTimeMillis()-start)/1000);
    }
}
