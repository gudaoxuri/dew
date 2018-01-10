package com.ecfront.dew.controller;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 *
 * @author hzzjb
 * @date 2017/9/19
 */
@RestController
@RequestMapping("performance")
public class TestController {

    private final Logger logger = LoggerFactory.getLogger(TestController.class);

    /**
     *  收到get请求立马返回
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getAtOnce() {
        long st = System.currentTimeMillis();
        try {
            return "{\"message\": \"test\"}";
        } finally {
            long et = System.currentTimeMillis();
            logger.info("get at once used ms :" + (et - st));
        }
    }

    /**
     * 收到get请求延迟后再返回
     * @param delayTime 延迟时间毫秒
     */
    @RequestMapping(value = "delay/{time}", method = RequestMethod.GET)
    @ResponseBody
    public String getWithDelay(@PathVariable("time") String delayTime) {
        long st = System.currentTimeMillis();
        try {
            long delay;
            try {
                delay = Integer.valueOf(delayTime);
            } catch (Exception e) {
                logger.error("parse delay time error", e);
                delay = 1000;
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                logger.error("sleep error", e);
            }
            return "get with delay";
        } finally {
            long et = System.currentTimeMillis();
            logger.info("get with delay used ms :" + (et - st));
        }
    }

    /**
     *  收到post请求立马返回
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public String postAtOnce() {
        long st = System.currentTimeMillis();
        try {
            return "{\"message\": \"test\"}";
        } finally {
            long et = System.currentTimeMillis();
            logger.info("post at once used ms :" + (et - st));
        }
    }

    /**
     * 模拟大事务接口 sleep 10秒
     */
    @RequestMapping(value = "file", method = RequestMethod.GET)
    public Integer doWithLargeTime() {
        long st = System.currentTimeMillis();
        try {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                logger.error("sleep error", e);
            }
            return 1;
        } finally {
            long et = System.currentTimeMillis();
            logger.info("file used ms :" + (et - st));
        }
    }

    /**
     * 混合接口 3成200ms,7成600ms
     */
    @RequestMapping(value = "mix", method = RequestMethod.GET)
    public Integer doMix() {
        long st = System.currentTimeMillis();
        try {
            try {
                Integer random = new Random().nextInt(10);
                Long delay = random >= 3 ? 200L : 600L;
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                logger.error("sleep error", e);
            }
            return 1;
        } finally {
            long et = System.currentTimeMillis();
            logger.info("mix used ms :" + (et - st));
        }
    }

    /**
     * 收到post请求延迟后再返回
     * @param time 延迟时间毫秒
     */
    @RequestMapping(value = "delay", method = RequestMethod.POST)
    @ResponseBody
    public String postWithDelay(String time) {
        long st = System.currentTimeMillis();
        try {
            long delay;
            try {
                delay = Integer.valueOf(time);
            } catch (Exception e) {
                logger.error("parse delay time error", e);
                delay = 1000;
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                logger.error("sleep error", e);
            }
            return "post with delay";
        } finally {
            long et = System.currentTimeMillis();
            logger.info("post with delay used ms :" + (et - st));
        }
    }
}
